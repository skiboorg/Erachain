# Blockchain platform Erachain

For clone code:  
1. Use InteliJ IDEA - New > Project from Version Control...
1. After load project - import Gradle project
1. Install Lombok (Settings - Plugins)
1. For RUN - select Application

Настройка запуска - Приложение и обязательно выбрать нативный Java SDK 1.8, использовать встроенный в IDEA нельзя! Иначе будет ошибка при коммитах базы данных.  
https://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html#javasejdk


Используйте Java 64 SDK

Для тестирования так же использовать  

Для сборки JAR файла без прогона тестов используем ключи:  
-x test

Описание кдючей запуска приложения в файле:  
ERA\z_bath_examples\readme.txt

## Java
Для корректной работы MapDB нужна именно Java 1.8 (vers 8) и проект собирается и тестируется именно с этой версией.
 Иначе бедет вызывать ошибка при коммите и закрывании базы:

> DCSet.close:1674 - java.io.IOException: Запрошенную операцию нельзя выполнить для файла с открытой пользователем сопоставленной секцией

Так же выдает предупреждения:  
```
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by org.mapdb.SerializerPojo$FieldInfo (file:/C:/Users/adm/.gradle/caches/modules-2/files-2.1/org.mapdb/mapdb/1.0.7/a4d6cad9402e671b0a547275dee40294eba8a5c5/mapdb-1.0.7.jar) to field java.util.Vector.capacityIncrement
WARNING: Please consider reporting this to the maintainers of org.mapdb.SerializerPojo$FieldInfo
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
```

Настройка версии - в Структура Проекта - там модули Тест и Майн - для них выбрать в зависмотях версию Явы 1.8

Не,, чот-то не помогло. Нужно JUnit настривать отдельно - создать тест не как Градле а как JUnit его запускать