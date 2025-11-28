plugins {
    id("com.android.application")
    // We will apply google-services plugin using apply(...) below because
    // some Google plugin versions are not on the Gradle Plugin Portal for the plugins DSL.
}

android {
    namespace = "com.example.stepsync"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.stepsync"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
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

    // If you ever add Java/Kotlin source sets customization, do it here.
}

dependencies {
    // Firebase
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.firebase:firebase-database:21.0.0")

    // AndroidX & Material
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}

// Apply google services plugin (Kotlin DSL friendly way)
apply(plugin = "com.google.gms.google-services")

