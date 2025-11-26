#!/bin/bash

# Script para configurar assinatura local (desenvolvimento)

KEYSTORE_PATH="app/debug.keystore"

if [ ! -f "$KEYSTORE_PATH" ]; then
    echo "🔑 Generating debug keystore..."
    keytool -genkey -v \
        -keystore "$KEYSTORE_PATH" \
        -alias androiddebugkey \
        -keyalg RSA \
        -keysize 2048 \
        -validity 10000 \
        -storepass android \
        -keypass android \
        -dname "CN=Android Debug,O=Android,C=US"
    
    echo "✅ Debug keystore created!"
else
    echo "✅ Debug keystore already exists"
fi
