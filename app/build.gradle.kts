plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.currencyexchange"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.currencyexchange"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Retrofit
    implementation(libs.interceptor)
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.retrofit.adapter)
    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    // Architecture Components
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.runtime)
    // Dagger 2
    implementation(libs.dagger)
    ksp(libs.dagger.compiler)
    //Lottie
    implementation(libs.lottie)
    //rxJava
    implementation(libs.rxJava)
    implementation(libs.rxJavaAndroid)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.espresso.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}