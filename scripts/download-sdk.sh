#!/bin/bash

set -e

echo "📥 Downloading SDK dependencies (ARM64 for Android Device)..."

# Diretório onde os arquivos ficarão dentro do APK
SDK_ASSETS_DIR="app/src/main/assets/sdk"
mkdir -p "$SDK_ASSETS_DIR"

# 1. Android JAR (android.jar do SDK 34)
if [ ! -f "$SDK_ASSETS_DIR/android.jar" ]; then
    echo "⬇️ Downloading android.jar..."
    curl -L "https://github.com/nicco88/android-jar/raw/master/android-34/android.jar" -o "$SDK_ASSETS_DIR/android.jar"
fi

# 2. Kotlin Stdlib
KOTLIN_VERSION="1.9.21"
if [ ! -f "$SDK_ASSETS_DIR/kotlin-stdlib.jar" ]; then
    echo "⬇️ Downloading kotlin-stdlib.jar..."
    curl -L "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/$KOTLIN_VERSION/kotlin-stdlib-$KOTLIN_VERSION.jar" -o "$SDK_ASSETS_DIR/kotlin-stdlib.jar"
fi

# 3. AAPT2 (Binário ARM64 para rodar no celular)
# Fonte: https://github.com/lzhiyong/android-sdk-tools
if [ ! -f "$SDK_ASSETS_DIR/aapt2" ]; then
    echo "⬇️ Downloading AAPT2 (arm64-v8a)..."
    # URL direta para um binário aapt2 compilado para Android
    curl -L "https://github.com/lzhiyong/android-sdk-tools/raw/master/android-14/aapt2" -o "$SDK_ASSETS_DIR/aapt2"
    # Não precisamos dar chmod +x aqui porque isso é perdido ao empacotar no APK, 
    # faremos isso via código Kotlin ao extrair.
fi

# 4. Lambda Stubs (Opcional, mas útil para compatibilidade Java 8)
if [ ! -f "$SDK_ASSETS_DIR/core-lambda-stubs.jar" ]; then
    echo "⬇️ Downloading core-lambda-stubs.jar..."
    curl -L "https://github.com/nicco88/android-jar/raw/master/android-34/core-lambda-stubs.jar" -o "$SDK_ASSETS_DIR/core-lambda-stubs.jar" || echo "⚠️ Lambda stubs ignorado"
fi

echo "✅ SDK dependencies downloaded into assets!"
ls -la "$SDK_ASSETS_DIR/"
