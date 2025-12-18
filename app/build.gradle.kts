plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    alias(libs.plugins.crashlytics)
}

android {
    namespace = "pa.saferide"
    compileSdk = 36

    defaultConfig {
        applicationId = "pa.saferide"
        minSdk = 23
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    // 🔧 CORE
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))

    // 🔧 COMPOSE UI
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.material3)

    // 🔧 COMPOSE EXTENSIONS
    implementation("androidx.compose.material:material-icons-extended:1.7.3")
    implementation("androidx.compose.animation:animation:1.7.3")
    implementation("androidx.compose.foundation:foundation:1.7.3")
    implementation("androidx.compose.runtime:runtime-livedata:1.7.3")

    // 🔧 NAVIGATION
    implementation("androidx.navigation:navigation-compose:2.8.3")

    // 🔧 VIEWMODEL & LIFECYCLE
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-process:2.8.6")

    // 🔧 PERMISSION HANDLING (PENTING untuk Bluetooth)
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")

    // 🔧 BLUETOOTH & CONNECTIVITY
    implementation("androidx.core:core-ktx:1.13.2")
    implementation("androidx.appcompat:appcompat:1.7.0")

    // 🔧 IMAGE LOADING
    implementation("io.coil-kt:coil-compose:2.6.0")

    // 🔧 SECURITY & ENCRYPTION
    implementation("at.favre.lib:bcrypt:0.10.2")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // 🔧 FIREBASE
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    // 🔧 COROUTINES
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

    // 🔧 NETWORKING (untuk future API calls)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.14")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.14")

    // 🔧 PREFERENCES
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // 🔧 WORK MANAGER (untuk background tasks future)
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // 🔧 TESTING
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.2")

    // 🔧 DEBUG
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.ui.tooling)
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.13")
    implementation("com.jakewharton.timber:timber:5.0.1")
}