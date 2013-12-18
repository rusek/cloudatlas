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

    mvn cobertura:cobertura
    
Generuje raport z pokryciem kodu przez testy (dostępny w target/site/cobertura/).

Uruchamianie aplikacji
--------------------

* `./registry.sh` - uruchamia rejestr RMI;
* `./agent.sh <config>` - uruchamia agenta z podanym plikiem konfiguracyjnym;
* `./client.sh <komenda> <arg1> <arg2> ...` - wykonuje podaną komendę klienta, `./client.sh --help` dla listy dostępnych komend  

Opcje konfiguracyjne agenta
--------------------

* `zoneName` - globalna nazwa strefy agenta, np. `/uw/khaki13`; wartość wymagana;
* `host` - nazwa hosta, na której agent nasłuchuje na przychodzące połączenia; domyślnie `localhost`;
* `port` - numer portu, na którym agent nasłuchuje na przychodzące połączenia; wartość wymagana;
* `gossipInterval` - odstęp pomiędzy kolejnymi plotkowaniami (w ms); domyślnie 5000;
* `fallbackContacts` - lista zapasowych kontaktów, w formacie `hostname:port, hostname:port, ...`; domyślnie pusta;