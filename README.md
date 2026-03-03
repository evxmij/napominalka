# Napominalka — трекер обучения программированию (Android)

Готовый Android-проект (Kotlin + Compose + Room) для офлайн-ведения учебных сессий, плана, дневника и базовой статистики.

## Что реализовано
- API 26+, офлайн-first, локальная БД Room.
- Экраны: Home / Session / Plan / Stats / Diary / Settings.
- Сессии: старт/стоп, выбор типа, заметка, сохранение в историю.
- План: добавление тем и задач, чекбоксы задач.
- Дневник: оценка дня, заметки, история записей.
- Статистика: день/неделя/месяц, streak, общий прогресс.
- Напоминания: WorkManager + notification channel + quick start из уведомления.
- Foreground service для активной сессии (инфраструктура подключена).
- Экспорт/импорт: JSON backup manager + CSV exporter для сессий.

## Требования для сборки
- JDK 17/21 (рекомендуется 21).
- Android SDK Platform 34.
- Build-Tools 34.x.

> В CI/локальной среде можно явно задать Java:
> `export JAVA_HOME=/root/.local/share/mise/installs/java/21.0.2`

## Быстрый запуск (ready-to-use)
```bash
# 1) установка зависимостей и проверка
gradle test

# 2) debug-сборка
gradle assembleDebug

# 3) установка на подключённый девайс/эмулятор
gradle installDebug
```

После установки запускайте приложение `Napominalka` на устройстве.

## Release-сборка
```bash
# unsigned release APK
gradle assembleRelease

# AAB для Play Console
gradle bundleRelease
```

Артефакты:
- `app/build/outputs/apk/release/`
- `app/build/outputs/bundle/release/`

## Подпись релиза (production)
1. Создайте keystore.
2. Добавьте параметры подписи в `~/.gradle/gradle.properties`.
3. Подключите `signingConfigs` в `app/build.gradle.kts`.
4. Выполните `gradle assembleRelease`.

## Структура
- `app/` — Android-клиент.
- `docs/architecture.md` — архитектура проекта.
- `docs/backup-schema-v1.json` — схема JSON backup v1.
- `docs/tz-coverage.md` — сверка реализации с ТЗ.


Важно: в репозитории не хранится `gradle-wrapper.jar` (ограничение платформы PR по бинарным файлам). Если нужен wrapper локально, выполните `gradle wrapper --gradle-version 8.7`.
