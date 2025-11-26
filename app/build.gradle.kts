plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

val versionMajor = 1
val versionMinor = 0
val versionPatch = 0
val versionNameSuffix: String by project.properties.withDefault { "" }

android {
    namespace = "com.androidide"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.androidide"
        minSdk = 26
        targetSdk = 34
        versionCode = versionMajor * 10000 + versionMinor * 100 + versionPatch
        versionName = "$versionMajor.$versionMinor.$versionPatch$versionNameSuffix"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
    }

    signingConfigs {
        create("release") {
            val keystoreFile = file("release.keystore")
            if (keystoreFile.exists()) {
                storeFile = keystoreFile
                storePassword = System.getenv("KEYSTORE_PASSWORD") ?: ""
                keyAlias = System.getenv("KEY_ALIAS") ?: ""
                keyPassword = System.getenv("KEY_PASSWORD") ?: ""
            }
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isMinifyEnabled = false
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            val releaseSigningConfig = signingConfigs.findByName("release")
            if (releaseSigningConfig?.storeFile?.exists() == true) {
                signingConfig = releaseSigningConfig
            }
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
        viewBinding = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/*.kotlin_module",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1"
            )
        }
        jniLibs {
            useLegacyPackaging = true
        }
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = true
        disable += listOf("MissingTranslation", "ExtraTranslation")
        xmlReport = true
        htmlReport = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    // AndroidX Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    // Activity & Fragment
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Kotlin Compiler (embeddable para compilar código Kotlin)
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.9.21")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.21")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.21")

    // D8/R8 para DEX compilation
    implementation("com.android.tools:r8:8.2.42")

    // APK Signing
    implementation("com.android.tools.build:apksig:8.2.0")

    // Code Editor (Sora Editor)
    implementation("io.github.Rosemoe.sora-editor:editor:0.23.2")
    implementation("io.github.Rosemoe.sora-editor:language-textmate:0.23.2")
    implementation("io.github.Rosemoe.sora-editor:language-java:0.23.2")

    // JSON
    implementation("com.google.code.gson:gson:2.10.1")

    // Apache Commons
    implementation("commons-io:commons-io:2.15.1")

    // Terminal Emulator (opcional)
    implementation("com.termux.termux-app:terminal-view:0.118.0") {
        isTransitive = false
    }

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
}
