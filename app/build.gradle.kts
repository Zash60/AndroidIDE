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
        multiDexEnabled = true
    }

    packaging {
        resources {
            // Exclui arquivos de metadados inúteis que causam conflitos
            excludes += listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/ASL2.0",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/*.kotlin_module",
                "**/*.kotlin_metadata",
                "META-INF/services/javax.annotation.processing.Processor",
                "META-INF/versions/9/module-info.class",
                "module-info.class"
            )
            
            // Resolve o conflito "2 files found with path" escolhendo a primeira ocorrência
            // Isso corrige o erro do kotlin-compiler-embeddable colidindo com o stdlib
            pickFirsts += listOf(
                "kotlin/reflect/reflect.kotlin_builtins",
                "kotlin/kotlin.kotlin_builtins",
                "kotlin/collections/collections.kotlin_builtins",
                "kotlin/ranges/ranges.kotlin_builtins",
                "kotlin/annotation/annotation.kotlin_builtins",
                "kotlin/internal/internal.kotlin_builtins",
                "kotlin/coroutines/coroutines.kotlin_builtins"
            )
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Android X
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // UI Libraries
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")
    implementation("io.github.Rosemoe.sora-editor:editor:0.23.2")
    implementation("io.github.Rosemoe.sora-editor:language-textmate:0.23.2")

    // Compilers & Tools
    implementation("com.android.tools:r8:8.2.42")
    implementation("com.android.tools.build:apksig:8.2.0")
    
    // Forçar versão exata para evitar mismatch
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.9.21")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.21")

    // Utils
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("commons-io:commons-io:2.15.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
