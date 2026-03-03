#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

if ! command -v gradle >/dev/null 2>&1; then
  echo "[ERROR] gradle not found in PATH"
  exit 1
fi

if [[ -z "${ANDROID_HOME:-}" && -z "${ANDROID_SDK_ROOT:-}" ]]; then
  echo "[ERROR] Set ANDROID_HOME or ANDROID_SDK_ROOT to Android SDK path"
  exit 1
fi

if [[ -z "${JAVA_HOME:-}" ]]; then
  echo "[WARN] JAVA_HOME is not set; using java from PATH"
fi

echo "[INFO] Building debug APK..."
gradle assembleDebug

APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
if [[ ! -f "$APK_PATH" ]]; then
  echo "[ERROR] APK was not generated at $APK_PATH"
  exit 1
fi

echo "[OK] APK generated: $APK_PATH"
sha256sum "$APK_PATH"
