import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

val signingProperties = Properties().apply {
    load(rootProject.file("signing.properties").inputStream())
}

android {
    namespace = "com.example.myapplication"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("hometown") {
            storeFile = file(signingProperties["storeFile"] as String)
            storePassword = signingProperties["storePassword"] as String
            keyAlias = signingProperties["keyAlias"] as String
            keyPassword = signingProperties["keyPassword"] as String
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("hometown")
        }
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("hometown")
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
