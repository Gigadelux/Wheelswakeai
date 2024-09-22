plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.wheelswakeai"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.wheelswakeai"
        minSdk = 30
        targetSdk = 34
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
    buildFeatures{
        viewBinding = true
        mlModelBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation("com.google.firebase:firebase-firestore-ktx:24.10.2")
    implementation("com.airbnb.android:lottie:6.3.0")
    implementation("androidx.exifinterface:exifinterface:1.3.7")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.3.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("org.tensorflow:tensorflow-lite-support:0.1.0")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.1.0")
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    implementation ("com.squareup.okhttp3:okhttp-urlconnection:4.4.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // For control over item selection of both touch and mouse driven selection
    implementation("androidx.recyclerview:recyclerview-selection:1.1.0")
    implementation("com.github.ibrahimsn98:speedometer:1.0.1")
}