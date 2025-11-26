plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.androidide"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.androidide"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

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
        viewBinding = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/*.kotlin_module"
            )
        }
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    // AndroidX
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")
    
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    
    // Kotlin Compiler (embedded)
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.9.21")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.21")
    
    // D8/R8 para DEX
    implementation("com.android.tools:r8:8.2.42")
    
    // AAPT2 e recursos
    implementation("com.android.tools.build:aapt2-proto:8.2.0-10154469")
    implementation("com.android.tools.build:apksig:8.2.0")
    
    // Code Editor
    implementation("io.github.Rosemoe.sora-editor:editor:0.23.2")
    implementation("io.github.Rosemoe.sora-editor:language-textmate:0.23.2")
    
    // File picker
    implementation("com.github.AdrienPoupa:filepicker:1.1.0")
    
    // JSON
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Apache Commons
    implementation("commons-io:commons-io:2.15.1")
}
