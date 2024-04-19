plugins {
    id("com.android.application")
    id("kotlin-android")
    id("io.sentry.android.gradle") version "4.4.0"
}

sentry {
    org.set("maple-network")
    projectName.set("waitify-lock")
    authToken.set("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")
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

    viewBinding { enable = true }
    buildFeatures { buildConfig = true }

    defaultConfig {
        applicationId = "ca.maplenetwork.waitifylock"
        minSdk = 26
        targetSdk = 34
        versionCode = 8
        versionName = "1.2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        //signingConfig = signingConfigs.getByName("release")
    }

    buildTypes {
        debug {
            manifestPlaceholders["sentryDsn"] = ""
        }
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            manifestPlaceholders["sentryDsn"] = "https://334bc018aa1528806ef295678c6fd477@o4506374465912832.ingest.us.sentry.io/4507080832516096"
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
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.28")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}