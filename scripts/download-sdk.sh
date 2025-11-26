#!/bin/bash

set -e

echo "📥 Downloading SDK dependencies..."

SDK_DIR="app/src/main/assets/sdk"
mkdir -p "$SDK_DIR"

# Android JAR (android.jar do SDK)
ANDROID_JAR_URL="https://github.com/nicco88/android-jar/raw/master/android-34/android.jar"
if [ ! -f "$SDK_DIR/android.jar" ]; then
    echo "⬇️ Downloading android.jar..."
    curl -L "$ANDROID_JAR_URL" -o "$SDK_DIR/android.jar"
fi

# Kotlin Stdlib
KOTLIN_VERSION="1.9.21"
KOTLIN_STDLIB_URL="https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/$KOTLIN_VERSION/kotlin-stdlib-$KOTLIN_VERSION.jar"
if [ ! -f "$SDK_DIR/kotlin-stdlib.jar" ]; then
    echo "⬇️ Downloading kotlin-stdlib.jar..."
    curl -L "$KOTLIN_STDLIB_URL" -o "$SDK_DIR/kotlin-stdlib.jar"
fi

# Core KTX (para APIs AndroidX básicas)
CORE_KTX_VERSION="1.12.0"
CORE_KTX_URL="https://repo1.maven.org/maven2/androidx/core/core-ktx/$CORE_KTX_VERSION/core-ktx-$CORE_KTX_VERSION.aar"
if [ ! -f "$SDK_DIR/core-ktx.aar" ]; then
    echo "⬇️ Downloading core-ktx.aar..."
    curl -L "$CORE_KTX_URL" -o "$SDK_DIR/core-ktx.aar"
fi

# Lambda stubs (para compilação)
LAMBDA_STUBS_URL="https://github.com/nicco88/android-jar/raw/master/android-34/core-lambda-stubs.jar"
if [ ! -f "$SDK_DIR/core-lambda-stubs.jar" ]; then
    echo "⬇️ Downloading core-lambda-stubs.jar..."
    curl -L "$LAMBDA_STUBS_URL" -o "$SDK_DIR/core-lambda-stubs.jar" || echo "⚠️ Lambda stubs não disponível"
fi

echo "✅ SDK dependencies downloaded!"
ls -la "$SDK_DIR/"
