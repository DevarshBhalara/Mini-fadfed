plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
    id("com.google.dagger.hilt.android")
}

android {

    bundle {
        language {
            enableSplit = true
        }
    }

    namespace = "com.example.mini_fadfed"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mini_fadfed"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
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
    val navVersion = "2.7.2"
    val hiltVersion = "2.46.1"
    val okHttpVersion = "4.11.0"
    val retrofitVersion = "2.9.0"

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    // Hilt
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-compiler:$hiltVersion")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // OkHttp3 and Retrofit
    implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okHttpVersion")
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.14.2")

    //Shimmer
    implementation("com.facebook.shimmer:shimmer:0.5.0@aar")

//    // RoomDB
//    def room_version = "2.5.2"
//
//    implementation "androidx.room:room-ktx:$room_version"
//    implementation "androidx.room:room-runtime:$room_version"
//    annotationProcessor "androidx.room:room-compiler:$room_version"
//
//    // To use Kotlin annotation processing tool (kapt)
//    kapt "androidx.room:room-compiler:$room_version"
}