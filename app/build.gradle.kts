plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "ca.maplenetwork.waitifylock"
    compileSdk = 34

    signingConfigs {
        create("release") {
            keyAlias = System.getenv("KEY_ALIAS") ?: "defaultAlias"
            keyPassword = System.getenv("KEY_PASSWORD") ?: "defaultKeyPassword"
            storeFile = file(System.getenv("KEYSTORE_PATH") ?: "defaultKeystorePath")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: "defaultStorePassword"
        }
    }

    viewBinding {
        enable = true
    }
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "ca.maplenetwork.waitifylock"
        minSdk = 26
        targetSdk = 34
        versionCode = 6
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        signingConfig = signingConfigs.getByName("release")
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
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
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.security:security-crypto:1.0.0")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.10")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}