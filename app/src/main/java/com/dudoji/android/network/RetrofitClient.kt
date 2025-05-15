
import android.content.Context
import android.util.Log
import com.dudoji.android.BuildConfig
import com.dudoji.android.network.api.service.FollowApiService
import com.dudoji.android.network.api.service.LoginApiService
import com.dudoji.android.network.api.service.MapApiService
import com.dudoji.android.network.api.service.PinApiService
import com.dudoji.android.network.api.service.UserApiService
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


// REST API client
object RetrofitClient {
    private const val BASE_URL = "http://${BuildConfig.HOST_IP_ADDRESS}/"

    val gson = GsonBuilder()
        .setLenient()
        .create()

    fun init(context: Context) {
        val client = provideOkHttpClient(context)

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        mapApiService = retrofit.create(MapApiService::class.java)
        userApiService = retrofit.create(UserApiService::class.java)
        followApiService = retrofit.create(FollowApiService::class.java)
        pinApiService = retrofit.create(PinApiService::class.java)
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
            val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
            val token = prefs.getString("jwt", null)

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
    lateinit var mapApiService: MapApiService
    lateinit var followApiService: FollowApiService
    lateinit var pinApiService: PinApiService
}