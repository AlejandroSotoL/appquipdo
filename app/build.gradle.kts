plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.tramites1cero1.tramiappquibdo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.tramites1cero1.tramiappquibdo"
        minSdk = 24
        targetSdk = 35
        versionCode = 3
        versionName = "1.0.3"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Librerías Compose
    implementation ("androidx.compose.material3:material3")
    implementation ("androidx.compose.material:material")
    // Otros básicos
    implementation("androidx.compose.ui:ui")
    implementation ("androidx.compose.ui:ui-tooling-preview")
    implementation(libs.androidx.foundation.layout)
    implementation(libs.foundation)

    debugImplementation ("androidx.compose.ui:ui-tooling")


    implementation(libs.androidx.benchmark.traceprocessor.android)
    // --- BOMs (Bill of Materials) ---
    val composeBom = platform("androidx.compose:compose-bom:2025.05.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation(platform(libs.firebase.bom))

    // --- Firebase ---
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.config)


    // --- Google Services ---
    implementation(libs.play.services.auth)
    implementation(libs.play.services.location)

    // --- Bundles ---
    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.compose.ui)
    implementation(libs.bundles.camera)
    implementation(libs.bundles.retrofit)
    implementation(libs.bundles.coil)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)
    implementation(libs.barcode.scanning)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.glide)

    // --- Interoperabilidad con Views ---
    implementation(libs.androidx.ui.viewbinding)
    implementation(libs.androidx.webkit)
    implementation(libs.androidx.browser)

    // --- Testing ---
    debugImplementation(libs.androidx.ui.tooling)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation (libs.androidx.runtime.livedata)

    // Zxing
    implementation (libs.zxing.android.embedded)
    implementation (libs.core)
    implementation(libs.jsoup)

    testImplementation(libs.junit)
    // Estas también son comunes para pruebas de Android, es bueno tenerlas
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation("com.google.firebase:firebase-auth:23.1.0")

    // In your app's build.gradle.kts file:
    implementation("com.google.android.play:app-update:2.1.0")
    // For Kotlin users also import the Kotlin extensions library for Play In-App Update:
    implementation("com.google.android.play:app-update-ktx:2.1.0")

    // Rating Bar
    implementation ("com.google.android.play:review-ktx:2.0.1")
    // ViewModel con helpers de Kotlin
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")

    // Si usas Fragments (para viewModels() en Fragment)
    implementation ("androidx.fragment:fragment-ktx:1.8.3")


}