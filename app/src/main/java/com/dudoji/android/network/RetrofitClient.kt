import com.dudoji.android.BuildConfig
import com.dudoji.android.network.api.service.MapApiService
import com.dudoji.android.network.api.service.UserApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// REST API client
object RetrofitClient {
    private const val BASE_URL = "http://${BuildConfig.HOST_IP_ADDRESS}/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val userApiService: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }

    val mapApiService: MapApiService by lazy {
        retrofit.create(MapApiService::class.java)
    }
}