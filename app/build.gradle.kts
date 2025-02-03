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
    implementation(libs.mediation.test.suite)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.google.android.gms:play-services:12.0.1")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Maps SDK for Android
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    // If using Places API
    implementation("com.google.android.libraries.places:places:3.2.0")

    // Google API Client
    implementation("com.google.api-client:google-api-client-android:2.0.0")
}