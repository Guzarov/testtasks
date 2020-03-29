# Генератор числовых идентификаторов.

## Описание задачи

Разработать REST-сервис с единственным GET-методом по получению уникального 
числового идентификатора: 1, 2, 3 и т.д. Идентификатор должен быть целым 
натуральным числом. При каждом вызове данного метода должен возвращаться новый 
идентификатор. Идентификаторы не должны повторяться, однако могут идти не 
по порядку.

Пример ответа сервиса
`{
  "id": 42
}`

Сервис должен хорошо горизонтально масштабироваться для обработки большого 
количества запросов (до 1 млн в секунду). Для разработки следует использовать 
фреймворк Spring. Другие технологии или библиотеки - по желанию.

## Описание решения

### Алгоритмы

Переключение между алгоритмами выполняется с помощью указания профилей spring - алгоритм 1 
включается для профиля default или diapasons. Алгоритм 2 - uuid.

#### Алгоритм 1
С помощью spring-web и memcached реализован rest сервис в соответствии с описанием. 
Для обеспечения уникальности генерируемых ключей используется следующий алгоритм:
- сервис с помощью compare and swap резервирует в memcached диапазон идентификаторов, 
которые он будет возвращать
- в ответ на запросы сервис последовательно выдает идентификаторы из диапазона
- при достижении конца диапазона сервис резервирует следующий диапазон с учетом 
диапазонов, зарезервированных другими инстансами

Пример, иллюстрирующий алгоритм находится в 
com.testtask.idgenerator.MemcachedGeneratorTest.testSmth

У алгоритма есть параметр - размер диапазона (задается через переменную окружения 
DIAPASON_SIZE), чем это значение больше, тем реже сервис будет обращаться к 
memcached-серверу. Впоследствии возможно ввести автоматическую стратегию взамен текущей
реализации (com.testtask.idgenerator.generator.memcached.DiapasonSizeStrategy)

#### Алгоритм 2

Основан на генерации UUID - уникальное целое натуральное число является десятичной 
записью 128 битного hex значения UUID. Уникальность гарантируется пренебрежимо малой 
вероятностью повторов (цитата википедии: "Общее количество уникальных ключей UUID 
(без учёта версий) составляет 2^128 = 256^16 или около 3,4 × 10^38. Это означает, что 
генерируя 1 триллион ключей каждую наносекунду, перебрать все возможные значения 
удастся лишь за 10 миллиардов лет. ")

Алгоритм 2 не содержит параметров, состояния и не использует общие данные. 

### Реализация

Сервис реализован на базе фреймворка spring. Снабжен тестами (юнит и интеграционным).
Сборка выполняется с помощью gradle. Для запуска и для выполнения интеграционного
теста должен быть установлен docker.

### Сборка и запуск

Для сборки нужно выполнить команду

`./gradlew bootJar`

для запуска тестов

`./gradlew check`

Для запуска окружения включая swagger-ui и memcached сервис нужно (после сборки) выполнить команду

`docker-compose up`

Swagger ui доступен по адресу http://localhost:8081/

По умолчанию сервис запускается на базе алгоритма 1, чтобы изменить это поведение нужно указать 
нужный профиль в переменной среды GENERATOR_IMPL (см. docker-compose.yml:9) 