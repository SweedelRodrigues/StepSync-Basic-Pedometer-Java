// Top-level build file (Kotlin DSL)

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Android Gradle plugin (keep in sync with your Android Studio version)
        classpath("com.android.tools.build:gradle:8.2.2")
        // Google services plugin for Firebase
        classpath("com.google.gms:google-services:4.4.2")
    }
}

allprojects {

}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

