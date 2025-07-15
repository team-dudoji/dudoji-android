import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.dudoji.android"
    compileSdk = 35

    // api key load
    fun getApiKey(propertyKey: String): String{
        return gradleLocalProperties(rootDir, providers).getProperty(propertyKey, "")
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true //뷰 바인딩
    }

    defaultConfig {
        applicationId = "com.dudoji.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "GOOGLE_MAPS_API_KEY", "\"${getApiKey("GOOGLE_MAPS_API_KEY")}\"")
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"${getApiKey("KAKAO_NATIVE_APP_KEY")}\"")
        buildConfigField("String", "HOST_IP_ADDRESS", "\"${getApiKey("HOST_IP_ADDRESS")}\"")

        addManifestPlaceholders(mapOf("GOOGLE_MAPS_API_KEY" to getApiKey("GOOGLE_MAPS_API_KEY")))
        addManifestPlaceholders(mapOf("KAKAO_NATIVE_APP_KEY" to getApiKey("KAKAO_NATIVE_APP_KEY")))

        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.android.maps.utils)

    implementation(libs.androidx.lifecycle.runtime.ktx)

    // for rest api
    implementation(libs.retrofit) // Retrofit 기본 라이브러리
    implementation(libs.converter.gson)
    implementation(libs.converter.scalars)

    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    implementation(libs.v2.user) // 카카오 로그인 API 모듈

    implementation(libs.lottie) // 로띠 애니메이션

    implementation(libs.androidx.security.crypto) // 암호화 라이브러리

    implementation(libs.androidx.biometric.ktx) // 생체 인식 라이브러리
}