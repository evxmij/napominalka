# Android-приложение «Трекер обучения программированию» — архитектура

## 1. Технологический стек
- **Язык:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Архитектура:** MVVM + Repository
- **Локальное хранилище:** Room
- **Фоновые задачи:** WorkManager
- **Таймер активной сессии:** Foreground Service + persistent notification
- **Уведомления:** NotificationChannel + AlarmManager (точечные сценарии) + WorkManager (периодические проверки)
- **Экспорт/импорт:** Storage Access Framework (SAF)
- **Формат бэкапа:** JSON (версионированная схема)

## 2. Модули приложения

### 2.1 app (presentation)
Содержит:
- Compose экраны
- Navigation graph
- ViewModel
- UI state/event модели

### 2.2 core-domain
Содержит:
- Модели предметной области (`Session`, `DayReview`, `Topic`, `Task`, `Settings`)
- UseCase-классы (старт/пауза/стоп сессии, вычисление streak, прогресса и т.д.)

### 2.3 core-data
Содержит:
- Room entities + DAO
- Mappers entity <-> domain
- Repository реализации
- Экспорт/импорт JSON

### 2.4 core-notifications
Содержит:
- Логику расписаний напоминаний
- Учет quiet hours
- Быстрый старт сессии из notification action

> На MVP можно начать с одного `app` модуля и выделить остальные позже.

## 3. Схема хранения данных

## 3.1 Session
- `id: Long`
- `startAt: Instant`
- `endAt: Instant?`
- `durationSec: Long`
- `category: SessionCategory` (`THEORY`, `PRACTICE`, `PROJECT`, `DEBUG`)
- `topicId: Long?`
- `taskId: Long?`
- `note: String`
- `status: SessionStatus` (`RUNNING`, `PAUSED`, `FINISHED`)

## 3.2 DayReview
- `date: LocalDate` (PK)
- `productivity1to10: Int`
- `did: String`
- `blockers: String`
- `planTomorrow: String`
- `totalLearnedSec: Long` (кеш для быстрого показа)

## 3.3 Topic
- `id: Long`
- `title: String`
- `module: String?`
- `status: TopicStatus` (`NOT_STARTED`, `IN_PROGRESS`, `DONE`)
- `tags: List<String>` (через join table или сериализацию)

## 3.4 Task
- `id: Long`
- `title: String`
- `done: Boolean`
- `priority: TaskPriority` (`LOW`, `MEDIUM`, `HIGH`)
- `dueDate: LocalDate?`
- `topicId: Long?`

## 3.5 Settings
- `id: Int = 1`
- `theme: ThemeMode` (`SYSTEM`, `LIGHT`, `DARK`)
- `weeklyGoalMinutes: Int`
- `remindersEnabled: Boolean`
- `reminderScheduleType: ReminderScheduleType` (`DAILY`, `WEEKLY_N_TIMES`, `CUSTOM_DAYS`)
- `reminderTime: LocalTime`
- `reminderDaysMask: Int` (битовая маска дней недели)
- `smartReminderEnabled: Boolean`
- `quietHoursEnabled: Boolean`
- `quietFrom: LocalTime`
- `quietTo: LocalTime`

## 4. Ключевые use-cases
1. **StartSessionUseCase** — старт сессии, создание/восстановление состояния.
2. **PauseSessionUseCase** — фиксация elapsed до паузы.
3. **StopSessionUseCase** — завершение, расчет `durationSec`, запись истории.
4. **RecoverRunningSessionUseCase** — восстановление после kill/restart.
5. **GetTodayDashboardUseCase** — минуты за день, задачи, кнопка оценки дня.
6. **SaveDayReviewUseCase** — оценка дня + заметки.
7. **GetStatsUseCase** — day/week/month, streak, прогресс по темам.
8. **ExportBackupUseCase / ImportBackupUseCase** — JSON backup.
9. **ExportCsvReportUseCase** — отчет сессий за период.

## 5. Навигация (минимум)
- `HomeScreen` (Сегодня)
- `SessionScreen` (таймер/параметры)
- `PlanScreen` (темы+задачи)
- `StatsScreen`
- `DiaryScreen`
- `SettingsScreen`
- `QuickStartSessionScreen` (entrypoint из уведомления)

## 6. Фоновые процессы и надежность

### 6.1 Таймер
- Foreground Service хранит активную сессию.
- Каждую секунду/5 сек публикует тик в StateFlow.
- При сворачивании/kill состояние сессии в Room + DataStore.

### 6.2 Напоминания
- Плановые: WorkManager periodic (или AlarmManager для точного времени).
- Smart reminder: вечерняя проверка “были ли сегодня сессии?”.
- Quiet hours: фильтр перед отправкой уведомления.

### 6.3 Перезагрузка устройства
- BootReceiver пересоздает расписания напоминаний.
- Активная незавершенная сессия восстанавливается как paused-with-recovery flag.

## 7. Экспорт/импорт

### 7.1 JSON backup
- Корневой объект:
  - `schemaVersion`
  - `exportedAt`
  - `sessions[]`
  - `dayReviews[]`
  - `topics[]`
  - `tasks[]`
  - `settings`
- При импорте:
  - проверка `schemaVersion`
  - валидация обязательных полей
  - транзакционное восстановление

### 7.2 CSV report
- Колонки: `id,startAt,endAt,durationSec,category,topic,task,note`
- Диапазон дат выбирает пользователь.

## 8. Acceptance mapping
- ✅ Сессии: старт/пауза/стоп + история + edit/delete
- ✅ Напоминания + quiet hours + переход в QuickStart
- ✅ Day review с автоподсказкой дневного времени
- ✅ Темы/задачи и прогресс
- ✅ Статистика day/week/month + streak
- ✅ Экспорт/импорт JSON + CSV
- ✅ Устойчивость при rotate/background/restart (в пределах Android ограничений)

## 9. План разработки (итерации)

### Итерация 1 (MVP Core)
- Room модели + DAO
- Session timer + Foreground service
- Home + Session + History

### Итерация 2
- Plan (topics/tasks)
- Day review + Diary
- Базовая статистика

### Итерация 3
- Reminders + quiet hours + quick start
- Backup/import JSON + CSV

### Итерация 4
- Полировка UX + темизация + цели/виджет/focus/templates
