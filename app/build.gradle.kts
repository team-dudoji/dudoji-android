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

    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.maps.android:android-maps-utils:2.3.0")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // for rest api
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Retrofit 기본 라이브러리
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.google.code.gson:gson:2.8.8")

    implementation("com.kakao.sdk:v2-user:2.20.6")// 카카오 로그인 API 모듈
}