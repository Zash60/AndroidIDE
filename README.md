# 🛠️ Android IDE

[![Build Status](https://github.com/user/AndroidIDE/workflows/Build%20Android%20IDE/badge.svg)](https://github.com/user/AndroidIDE/actions)
[![Release](https://img.shields.io/github/v/release/user/AndroidIDE)](https://github.com/user/AndroidIDE/releases)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

IDE completo para desenvolver aplicativos Android diretamente no seu dispositivo Android, sem necessidade de PC!

## 📱 Screenshots

[Screenshots aqui]

## ✨ Funcionalidades

- 📝 Editor de código com syntax highlighting (Kotlin, Java, XML)
- 📁 Gerenciador de arquivos integrado
- 🔨 Compilador Kotlin/Java embutido
- 📦 Geração de APK
- 🔑 Assinatura automática de APK
- 🎨 Temas claro/escuro
- ⌨️ Atalhos de teclado
- 🔍 Busca e substituição

## 📥 Download

### Releases Estáveis
Baixe a versão mais recente em [Releases](https://github.com/user/AndroidIDE/releases)

### Nightly Builds
Builds automáticos diários disponíveis em [Actions](https://github.com/user/AndroidIDE/actions/workflows/nightly.yml)

## 📋 Requisitos

- Android 8.0 (API 26) ou superior
- ~200MB de armazenamento
- 2GB+ RAM recomendado

## 🚀 Como Usar

1. Instale o APK
2. Conceda permissões de armazenamento
3. Crie um novo projeto ou abra existente
4. Edite o código
5. Clique em "Build" para gerar o APK

## 🛠️ Compilar do Código Fonte

```bash
# Clone o repositório
git clone https://github.com/user/AndroidIDE.git
cd AndroidIDE

# Download dependências SDK
chmod +x scripts/download-sdk.sh
./scripts/download-sdk.sh

# Build debug
./gradlew assembleDebug

# Build release
./gradlew assembleRelease
