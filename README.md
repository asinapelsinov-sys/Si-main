# Si-mine (Fabric 1.21.1)

Погода «Метель»: быстрый плотный снег (слабая видимость), тёмное небо, урон от заморозки вне укрытия.
После окончания бури над инвентарём (action bar) появляется надпись «Кажется кончилось».
Нужен Fabric Loader 0.16+ и Fabric API.

## Сборка jar
GitHub: загрузите содержимое папки в репозиторий -> Actions -> build -> artifact `simine-jar`.
Локально: JDK 21 + Gradle 8.11+, `gradle build` -> build/libs/simine-1.0.0.jar.

## Команды
/blizzard start, /blizzard stop (оп-права).

## Настройки (src/main/java/com/example/simine/client/SiMineClient.java)
PARTICLES_PER_TICK, NEAR_PARTICLES - количество снега; скорость ветра - в методе spawn();
DARKNESS_RAIN / DARKNESS_THUNDER - затемнение неба.
