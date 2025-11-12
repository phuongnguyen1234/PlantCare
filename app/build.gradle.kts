plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.plantcare"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.plantcare"
        minSdk = 26
        targetSdk = 36
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
    buildFeatures {
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    val room_version = "2.8.3"
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Room Database (phần chính)
    implementation("androidx.room:room-runtime:$room_version")

    // Annotation processor để tạo code tự động
    annotationProcessor("androidx.room:room-compiler:$room_version")

    implementation("androidx.work:work-runtime:2.11.0")
    implementation("com.github.PhilJay:MPAndroidChart:3.1.0")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

}