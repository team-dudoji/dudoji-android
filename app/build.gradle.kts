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
        addManifestPlaceholders(mapOf("GOOGLE_MAPS_API_KEY" to getApiKey("GOOGLE_MAPS_API_KEY")))

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
    implementation("com.google.android.gms:play-services:12.0.1")

    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.google.code.gson:gson:2.8.8")
}