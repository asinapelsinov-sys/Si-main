# Blizzard mod (Fabric 1.21.1)

Новая погода «Метель»: частицы снега и урон от заморозки вне укрытия.
Нужен Fabric Loader 0.16+ и Fabric API.

## Сборка jar
Вариант А (GitHub): загрузите папку в репозиторий -> вкладка Actions -> build -> скачайте artifact `blizzardmod-jar`.
Вариант Б (локально): JDK 21 + Gradle 8.11+, затем `gradle build`.
Готовый файл: build/libs/blizzardmod-1.0.0.jar (не *-sources.jar).
Тест в игре: `gradle runClient`.

## Команды
/blizzard start, /blizzard stop (оп-права). Случайный старт раз в 10-30 мин, длится 5-10 мин.
