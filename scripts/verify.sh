#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

export GRADLE_USER_HOME="${GRADLE_USER_HOME:-$ROOT_DIR/.gradle}"

if [[ -z "${JAVA_HOME:-}" && -x /usr/lib/jvm/java-17-openjdk-amd64/bin/java ]]; then
  export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
fi

if [[ -z "${ANDROID_HOME:-}" && -d /home/dev/android-sdk ]]; then
  export ANDROID_HOME=/home/dev/android-sdk
fi

if [[ -n "${ANDROID_HOME:-}" && -z "${ANDROID_SDK_ROOT:-}" ]]; then
  export ANDROID_SDK_ROOT="$ANDROID_HOME"
fi

git diff --check
./gradlew spotlessCheck lintDebug testDebugUnitTest assembleDebug
