import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
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
        buildConfigField("String", "HOST_PORT", "\"${getApiKey("HOST_PORT")}\"")

        addManifestPlaceholders(mapOf("GOOGLE_MAPS_API_KEY" to getApiKey("GOOGLE_MAPS_API_KEY")))
        addManifestPlaceholders(mapOf("KAKAO_NATIVE_APP_KEY" to getApiKey("KAKAO_NATIVE_APP_KEY")))

        multiDexEnabled = true
    }

    signingConfigs {
        create("release") {
            storeFile = file(getApiKey("KEYSTORE_FILE"))
            storePassword = getApiKey("KEYSTORE_PASSWORD")
            keyAlias = getApiKey("KEY_ALIAS")
            keyPassword = getApiKey("KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
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
    implementation(libs.protolite.well.known.types)
    implementation(libs.play.services.vision)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.android.maps.ktx)

    implementation(libs.android.maps.utils)

    implementation(libs.androidx.lifecycle.runtime.ktx)

    // for rest api
    implementation(libs.retrofit) // Retrofit 기본 라이브러리
    implementation(libs.converter.gson)
    implementation(libs.converter.scalars)

    implementation("io.coil-kt:coil:2.4.0")

    implementation(libs.v2.user) // 카카오 로그인 API 모듈

    implementation(libs.lottie) // 로띠 애니메이션

    implementation(libs.androidx.security.crypto) // 암호화 라이브러리

    implementation(libs.androidx.biometric.ktx) // 생체 인식 라이브러리

    // DI
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1")
}