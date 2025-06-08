plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.realestate"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.realestate"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.monitor)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation (libs.glide)
    annotationProcessor (libs.compiler)


    implementation (libs.gson)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)

    implementation(libs.androidx.room.runtime)

    // If this project only uses Java source, use the Java annotationProcessor
    // No additional plugins are necessary
    annotationProcessor(libs.room.compiler)

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)

    // optional - RxJava2 support for Room
    implementation(libs.androidx.room.rxjava2)

    // optional - RxJava3 support for Room
    implementation(libs.androidx.room.rxjava3)

    // optional - Guava support for Room, including Optional and ListenableFuture
    implementation(libs.androidx.room.guava)

    // optional - Test helpers
    testImplementation(libs.androidx.room.testing)

    // optional - Paging 3 Integration
    implementation(libs.androidx.room.paging)

    // Mockito for unit testing
    testImplementation(libs.mockito.core)
    androidTestImplementation(libs.mockito.android)

    // Architecture components testing
    testImplementation(libs.arch.core.testing)

    // Additional LiveData testing dependencies
    testImplementation(libs.androidx.lifecycle.runtime)
    testImplementation(libs.androidx.lifecycle.common)

    // Room testing
    testImplementation(libs.androidx.core)
    testImplementation(libs.androidx.junit.v115)
    testImplementation(libs.arch.core.testing)
    testImplementation(libs.robolectric)
}
