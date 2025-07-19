import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.dudoji.android.BuildConfig
import com.dudoji.android.login.api.service.LoginApiService
import com.dudoji.android.login.util.getEncryptedPrefs
import com.dudoji.android.mypage.api.service.FollowApiService
import com.dudoji.android.mypage.api.service.MissionApiService
import com.dudoji.android.mypage.api.service.UserApiService
import com.dudoji.android.network.utils.LocalDateTimeAdapter
import com.dudoji.android.pin.api.service.PinApiService
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit


// REST API client
@RequiresApi(Build.VERSION_CODES.O)
object RetrofitClient {
    const val BASE_URL = "http://${BuildConfig.HOST_IP_ADDRESS}"
    lateinit var TOKEN: String

    @RequiresApi(Build.VERSION_CODES.O)
    val gson = GsonBuilder()
        .setLenient()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()

    @RequiresApi(Build.VERSION_CODES.O)
    fun init(context: Context) {
        val client = provideOkHttpClient(context)

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        userApiService = retrofit.create(UserApiService::class.java)
        followApiService = retrofit.create(FollowApiService::class.java)
        pinApiService = retrofit.create(PinApiService::class.java)
        missionApiService = retrofit.create(MissionApiService::class.java)
        Log.d("MapApiService", "Retrofit client initialized")
    }

    fun provideOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(provideAuthInterceptor(context))
            .build()
    }

    fun provideAuthInterceptor(context: Context): Interceptor {
        return Interceptor { chain ->
            val prefs = getEncryptedPrefs(context)
            val token = prefs.getString("jwt", null)
            TOKEN = token ?: ""
            val newRequest = if (token != null) {
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                chain.request()
            }

            chain.proceed(newRequest)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val loginApiService: LoginApiService by lazy {
        retrofit.create(LoginApiService::class.java)
    }

    lateinit var userApiService: UserApiService
    lateinit var followApiService: FollowApiService
    lateinit var pinApiService: PinApiService
    lateinit var missionApiService: MissionApiService
}