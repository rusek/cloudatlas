cloudatlas
==========

Konfiguracja Eclipse'a
--------------------

* Zainstaluj wtyczkę m2e
* Wykonaj `mvn eclipse:eclipse` wewnątrz katalogu repozytorium
* Zaimportuj projekt

Budowanie i uruchamianie
--------------------

    mvn package

Buduje projekt, uruchamia testy i generuje archiwa .jar w katalogu target/.

    java -jar target/cloudatlas-1.0-SNAPSHOT-jar-with-dependencies.jar
    
Uruchamia interpreter zapytań.
