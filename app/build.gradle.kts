plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("androidx.navigation.safeargs.kotlin")
    id ("kotlin-kapt")
}

android {
    namespace = "com.masyanolchik.grandtheftradio2"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.masyanolchik.grandtheftradio2"
        minSdk = 24
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
    kotlinOptions {
        jvmTarget = "18"
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    val navVersion = "2.6.0"
    val gsonVersion = "2.10.1"
    val koinVersion = "3.4.3"
    val media3Version = "1.1.0"
    val roomVersion = "2.5.1"
    val coroutinesVersion = "1.7.1"
    val robolectricVersion = "4.10.3"
    val truthVersion = "1.1.4"
    val mockitoVersion = "5.4.0"
    val fragmentVersion = "1.5.7"

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    implementation ("androidx.room:room-runtime:$roomVersion")
    kapt ("org.xerial:sqlite-jdbc:3.36.0")
    kapt ("androidx.room:room-compiler:$roomVersion")
    implementation ("com.google.code.gson:gson:$gsonVersion")
    implementation ("io.insert-koin:koin-android:$koinVersion")
    // For media playback using ExoPlayer
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    // For DASH playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-dash:$media3Version")
    // For HLS playback support with ExoPlayer
    implementation("androidx.media3:media3-exoplayer-hls:$media3Version")
    // For building media playback UIs
    implementation("androidx.media3:media3-ui:$media3Version")
    // For exposing and controlling media sessions
    implementation("androidx.media3:media3-session:$media3Version")
    testImplementation("junit:junit:4.13.2")
    implementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation ("org.mockito.kotlin:mockito-kotlin:4.0.0")

    testImplementation("io.insert-koin:koin-test:$koinVersion")
    testImplementation("org.robolectric:robolectric:$robolectricVersion")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    testImplementation ("com.google.truth:truth:$truthVersion")
    testImplementation("androidx.test.ext:junit:1.1.5")
    testImplementation ("androidx.test.ext:junit-ktx:1.1.5")
    testImplementation("androidx.test.espresso:espresso-core:3.5.1")
    debugImplementation("androidx.fragment:fragment-testing:$fragmentVersion")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
    androidTestImplementation ("com.google.truth:truth:$truthVersion")
    androidTestImplementation("androidx.navigation:navigation-testing:$navVersion")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.ext:junit-ktx:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation ("androidx.test:core-ktx:1.5.0")
}