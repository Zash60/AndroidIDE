buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Classpath para ferramentas de build, se necessário
    }
}

// Plugins globais
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.21" apply false
}
