# QA_Diplom_2

Учебный проект по автотестированию API для сайта по заказу бургеров Stellar Burgers.

## Описание

Версия Java 11
Проект использует следующие библиотеки:
- JUnit 4
- RestAssured
- Allure

## Документация

[Ссылка](https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf) на документацию проекта.

### Запуск автотестов

Для запуска автотестов необходимо:

1. Скачать код

 ```sh
   git clone https://github.com/chesterior/QA_Diplom_2.git
   ```
   
2. Запустить команду в проекте, чтобы запустить тесты и сгенерировать отчёт

```sh
mvn clean test
```

3. Для отображения отчета в Allure ввести команду

```sh
mvn allure:serve
```
