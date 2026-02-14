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

Сервисы:
- `db` (PostgreSQL 15)
- `error-free-text-app` (Spring Boot приложение, порт `8080` проброшен)

### 2. Локально (profile `local`)

```bash
./gradlew :error-free-text-app:bootRun
```
Детали:
- приложение работает на порту `8080`
- БД: H2 in-memory

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
