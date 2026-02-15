# Error Free Text

Веб-приложение для автоматической корректировки текста.
Пользователь создает задачу, система обрабатывает текст через Yandex Speller API и отдает результат по `uuid`.

## Stack

- Java 21 (совместимо с требованием Java 17+)
- Spring Boot 3
- Gradle
- PostgreSQL (docker profile)
- Liquibase
- Docker / Docker Compose

## Архитектура

- Монолитное REST-приложение.
- Слой API: `CorrectionTaskController`.
- Слой бизнес-логики: сервисы `Create/Get/Process`.
- Слой хранения: Spring Data JPA + Liquibase.
- Интеграция: `YandexSpellerClient`.
- Фоновая обработка: scheduler + async обработка задач на virtual threads.

Жизненный цикл задачи:
- `NEW` -> создана и ожидает обработки
- `PROCESSING` -> взята в работу
- `DONE` -> текст исправлен
- `ERROR` -> обработка завершилась с ошибкой

## Запуск

### 1. Через Docker Compose (PostgreSQL)

```bash
./gradlew :error-free-text-app:docker
docker compose up
```

Детали:
- порт `8080` проброшен на localhost (контейнер `error-free-text-app`)
- БД PostgreSQL 15 (контейнер `db`)

### 2. Локально (profile `local`)

```bash
./gradlew :error-free-text-app:bootRun
```
Детали:
- порту `8080`
- БД H2 in-memory

## API

Base path: `/tasks`

### 1. Создание задачи

`POST /tasks`

Request:

```json
{
  "text": "Helo world 123 https://example.com",
  "language": "EN"
}
```

Response:

```json
{
  "uuid": "44bd78dc-d08c-41c6-b87d-fb82046bd470"
}
```

### 2. Получение результата

`GET /tasks/{uuid}`

Если задача еще в обработке:

```json
{
  "uuid": "44bd78dc-d08c-41c6-b87d-fb82046bd470",
  "status": "PROCESSING"
}
```

Если задача завершена:

```json
{
  "uuid": "44bd78dc-d08c-41c6-b87d-fb82046bd470",
  "status": "DONE",
  "correctedText": "Hello world 123 https://example.com"
}
```

Если задача завершилась ошибкой:

```json
{
  "uuid": "44bd78dc-d08c-41c6-b87d-fb82046bd470",
  "status": "ERROR",
  "errorMessage": "Some processing error"
}
```

## Параметры

По умолчанию приложение имеет следующие параметры:

### Text processing
- text.splitter.max-chunk-size=10000 — максимальный размер фрагмента текста при разбиении.

### Correction task
- correction.task.provider.page-size=50 — размер страницы при получении задач.
- correction.task.job.delay=60000 — задержка между запусками фоновой задачи (мс).
### Yandex Speller integration
- yandex.speller.base-url=https://speller.yandex.net/services/spellservice.json — базовый URL сервиса.
- yandex.speller.check-texts-uri=/checkTexts — endpoint для проверки текста.
- yandex.speller.connection-timeout=3000 — timeout соединения (мс).
- yandex.speller.read-timeout=5000 — timeout чтения ответа (мс).
- yandex.speller.retry-max-attempts=3 — максимальное количество попыток при ошибке.
- yandex.speller.retry-delay=1000 — начальная задержка между retry (мс).
- yandex.speller.retry-multiplier=2 — множитель для exponential backoff.

## Валидация входящих данных

- `language`: только `EN` или `RU`
- `text`: минимум 3 символа
- `text`: должен содержать буквы (строки из цифр/спецсимволов отклоняются)

Пример ошибки:

```json
{
  "errorMessage": "Task with uuid: 44bd78dc-d08c-41c6-b87d-fb82046bd470 not found",
  "errorCode": 40401,
  "timestamp": "2026-02-14T12:34:56.000Z",
  "path": "/tasks/44bd78dc-d08c-41c6-b87d-fb82046bd470"
}
```

## Логика корректировки текста (Yandex Speller)

- Используется метод `POST checkTexts`.
- Тексты длиннее 10 000 символов делятся на chunks.
- Опции запроса:
    - если есть цифры -> `IGNORE_DIGITS`
    - если есть URL -> `IGNORE_URLS`
    - `FIND_REPEAT_WORDS` всегда выключен
    - `IGNORE_CAPITALIZATION` всегда выключен

Если вызов внешнего API завершился ошибкой, задача переводится в `ERROR`.

## Тесты

- Для большинства классов реализованы unit-тесты
- Для REST-контроллера и сервиса обработки задач реализованы интеграционные тесты.