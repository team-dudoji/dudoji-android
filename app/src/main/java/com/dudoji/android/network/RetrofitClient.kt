
import android.os.Build
import androidx.annotation.RequiresApi
import com.dudoji.android.BuildConfig
import com.dudoji.android.landmark.api.service.LandmarkApiService
import com.dudoji.android.login.api.service.LoginApiService
import com.dudoji.android.follow.api.FollowApiService
import com.dudoji.android.mypage.api.service.MissionApiService
import com.dudoji.android.mypage.api.service.UserApiService
import com.dudoji.android.network.utils.LocalDateTimeAdapter
import com.dudoji.android.pin.api.service.PinApiService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.time.LocalDateTime

// REST API client
@RequiresApi(Build.VERSION_CODES.O)
object RetrofitClient {
    const val BASE_URL = "http://${BuildConfig.HOST_IP_ADDRESS}:${BuildConfig.HOST_PORT}"

    @RequiresApi(Build.VERSION_CODES.O)
    val gson = GsonBuilder()
        .setLenient()
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .create()

    @RequiresApi(Build.VERSION_CODES.O)
    fun initAuthed(client: OkHttpClient) {
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
        landmarkApiService = retrofit.create(LandmarkApiService::class.java)
    }

    fun initNonAuthed(client: OkHttpClient) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        loginApiService = retrofit.create(LoginApiService::class.java)
    }

    lateinit var landmarkApiService: LandmarkApiService
    lateinit var loginApiService: LoginApiService
    lateinit var userApiService: UserApiService
    lateinit var followApiService: FollowApiService
    lateinit var pinApiService: PinApiService
    lateinit var missionApiService: MissionApiService
}