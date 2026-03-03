# Napominalka: подробный мануал по релизной сборке (APK/AAB)

Ниже — пошаговая инструкция «с нуля» для Windows/macOS/Linux.

---

## 1) Что установить

### 1.1 Java (JDK)
Нужен **JDK 17 или 21** (рекомендуется 21).

Проверка:
```bash
java -version
```

Если не установлен:
- **Windows**: Temurin JDK 21 (MSI) — https://adoptium.net/temurin/releases/
- **macOS**: `brew install --cask temurin`
- **Linux (Ubuntu/Debian)**: `sudo apt install openjdk-21-jdk`

---

### 1.2 Android SDK (Platform 34 + Build Tools 34)

Проще всего поставить через **Android Studio**:
1. Установите Android Studio: https://developer.android.com/studio
2. Откройте **SDK Manager**
3. Установите:
   - Android SDK Platform **34**
   - Android SDK Build-Tools **34.x**
   - Android SDK Platform-Tools
   - (опционально) Command-line Tools

Проверка путей SDK:
- Windows: обычно `C:\Users\<you>\AppData\Local\Android\Sdk`
- macOS: обычно `/Users/<you>/Library/Android/sdk`
- Linux: обычно `/home/<you>/Android/Sdk`

---

### 1.3 Gradle
В этом репозитории wrapper jar не хранится, поэтому нужен системный `gradle`.

Проверка:
```bash
gradle -v
```

Если нужен wrapper локально:
```bash
gradle wrapper --gradle-version 8.7
```

---

## 2) Подготовка окружения

### Linux/macOS
```bash
export JAVA_HOME=/path/to/jdk-21
export ANDROID_SDK_ROOT=/path/to/Android/Sdk
export PATH="$JAVA_HOME/bin:$PATH"
```

### Windows PowerShell
```powershell
$env:JAVA_HOME="C:\\Program Files\\Eclipse Adoptium\\jdk-21"
$env:ANDROID_SDK_ROOT="C:\\Users\\<you>\\AppData\\Local\\Android\\Sdk"
$env:Path="$env:JAVA_HOME\\bin;$env:Path"
```

Проверка:
```bash
java -version
gradle -v
```

---

## 3) Сборка debug APK

### Быстро (скриптом проекта)
```bash
ANDROID_SDK_ROOT=/path/to/Android/Sdk JAVA_HOME=/path/to/jdk ./scripts/build_debug_apk.sh
```

Скрипт:
- запускает `gradle assembleDebug`
- проверяет наличие файла
- печатает SHA-256

Итоговый файл:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 4) Подготовка keystore для релиза

Создать keystore (один раз):
```bash
keytool -genkeypair \
  -v \
  -storetype PKCS12 \
  -keystore napominalka-release.jks \
  -alias napominalka \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

Сохраните:
- путь к keystore
- alias
- store password
- key password

⚠️ **Никогда не коммитьте keystore и пароли в git.**

---

## 5) Подключение подписи в Gradle

Добавьте в `~/.gradle/gradle.properties` (локально):
```properties
RELEASE_STORE_FILE=/absolute/path/to/napominalka-release.jks
RELEASE_STORE_PASSWORD=your_store_password
RELEASE_KEY_ALIAS=napominalka
RELEASE_KEY_PASSWORD=your_key_password
```

Добавьте в `app/build.gradle.kts` блоки `signingConfigs` и `buildTypes.release.signingConfig`.

Пример:
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file(providers.gradleProperty("RELEASE_STORE_FILE").get())
            storePassword = providers.gradleProperty("RELEASE_STORE_PASSWORD").get()
            keyAlias = providers.gradleProperty("RELEASE_KEY_ALIAS").get()
            keyPassword = providers.gradleProperty("RELEASE_KEY_PASSWORD").get()
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

---

## 6) Сборка релиза

### APK (signed)
```bash
gradle assembleRelease
```

### AAB (для Google Play)
```bash
gradle bundleRelease
```

Результаты:
- APK: `app/build/outputs/apk/release/`
- AAB: `app/build/outputs/bundle/release/`

---

## 7) Проверка подписи релиза

Проверка APK:
```bash
apksigner verify --verbose app/build/outputs/apk/release/app-release.apk
```

Проверка сертификата:
```bash
apksigner verify --print-certs app/build/outputs/apk/release/app-release.apk
```

---

## 8) Типовые ошибки и как чинить

### `SDK location not found`
Укажите `ANDROID_SDK_ROOT`/`ANDROID_HOME` или добавьте `local.properties`:
```properties
sdk.dir=/path/to/Android/Sdk
```

### `resource ... not found`
Проверьте, что зависимости подтянулись и есть интернет на первом билде.

### `gradle: command not found`
Установите Gradle или локально сгенерируйте wrapper.

### Ошибка подписи релиза
Проверьте alias/пароли/путь к keystore и что переменные реально доступны Gradle.

---

## 9) Минимальный чек-лист перед публикацией

- [ ] `gradle testDebugUnitTest`
- [ ] `gradle assembleRelease`
- [ ] `gradle bundleRelease`
- [ ] `apksigner verify --verbose ...apk`
- [ ] keystore/пароли сохранены в безопасном месте
- [ ] versionCode/versionName обновлены

---

## 10) Быстрые команды (копипаст)

```bash
# 0) окружение
export JAVA_HOME=/path/to/jdk-21
export ANDROID_SDK_ROOT=/path/to/Android/Sdk
export PATH="$JAVA_HOME/bin:$PATH"

# 1) тесты
gradle testDebugUnitTest

# 2) debug apk
./scripts/build_debug_apk.sh

# 3) release apk / aab
gradle assembleRelease
gradle bundleRelease
```
