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

    ./registry.sh
    
Uruchamia rejestr RMI.


    ./agent.sh <config>
    
Uruchamia agenta z podanym plikiem konfiguracyjnym, np. `./agent.sh agent.uw.khaki13.properties`.


    ./client.sh <komenda> <arg1> <arg2> ...

Wykonuje podaną komendę klienta. `./client.sh --help` wypisuje listę dostępnych komend i sposób specyfikowania
lokalizacji, pod którą znajduje się agent. Klient jest w stanie komunikować się z wieloma agentami na raz
i pozwala na pracę w trybie interaktywnym (komenda `shell`).  

Opcje konfiguracyjne agenta
--------------------

* `zoneName` - globalna nazwa strefy agenta, np. `/uw/khaki13`; wartość wymagana;
* `host` - nazwa hosta, na której agent nasłuchuje na przychodzące połączenia; domyślnie `localhost`;
* `port` - numer portu, na którym agent nasłuchuje na przychodzące połączenia; wartość wymagana;
* `gossipInterval` - odstęp pomiędzy kolejnymi plotkowaniami (w ms); domyślnie 5000;
* `zoneRefreshInterval` - odstęp pomiędzy odświeżeniami ZMI (w ms); domyślnie 5000;
* `maxZoneAge` - wiek ZMI, po przekroczeniu którego strefa jest usuwana z pamięci agenta (w ms); domyślnie 60000;
* `gossipLevelSelectionStrategy` - specyfikuje strategię wyboru poziomu do plotkowania; możliwe wartości:
    * `random` (domyślnie) - poziom jest wybierany losowo;
    * `weightedRandom` - poziom jest wybierany losowo z prawdopodobieństwem malejącym wykładniczo względem poziomu;
    * `roundRobin` - poziomy są wybierane cyklicznie;
    * `weightedRoundRobin` - poziomy są wybierane cyklicznie, z prawdopodobieństwem malejącym wykładniczo względem poziomu;
    * `whatever` - wybierana jest losowo jakaś strategia;
* `fallbackContacts` - lista zapasowych kontaktów, w formacie `hostname:port, hostname:port, ...`; domyślnie pusta;

Uruchamianie agentów w sieci
--------------------

Przed uruchomieniem agenta na zdalnej maszynie należy w odpowiednim pliku konfiguracyjnym agenta `agent.*.properties` ustawić
odpowiednio wartość opcji `host` - możliwe jest podanie zarówno nazwy hosta `somename` jak i adres IP `192.168.0.123`. Ustawienie
wartości `0.0.0.0` lub `127.0.0.1` skutkuje różnymi błędami w komunikacji, zarówno między agentami, jak i agentem i klientem.
Zaobserwowane błędy to m.in. `IOException: Zły argument`, który skutkuje wywaleniem procesu agenta, gdy ten nasłuchuje na adresie `localhost`
i jednocześnie wykorzystuje to samo gniazdo sieciowe do komunikacji z innymi agentami, którzy nie są uruchomieni lokalnie.
W przypadku połączenia klienta z agentem źle ustawiona nazwa hosta prowadzi do błędu `Connection refused` na etapie wywoływania
zdalnych metod - samo nawiązanie połączenia nie powoduje błędu.

Dodatkowo przed wykonaniem skryptu `./agent.sh agent.<zoneName>.properties` należy wykonać `./registry.sh`. Po skonfigurowaniu
wszystkich agentów można zweryfikować działanie systemu m.in. poprzez `./client.sh -l <host1> -l <host2> getMyGlobalName`.

Oprócz uruchomienia agentów na każdej z maszyn należy również odpalić klienta, który będzie zbierał statystyki. Służy do tego komenda
`./client.sh sendStatsLoop 5000`.

Aby instalować zapytania i obserwować ich wyniki najlepiej wykonać komendę `./agent.sh -l <host1> -l <host2> shell` i dalsze polecenia
wprowadzać w trybie interaktywnym. Wszystkie polecenia są rozsyłane do podanych agentów. Przykładowo instalację zapytania można
dokonać poprzez:

    >: installQuery &num_processes "SELECT sum(num_processes) AS num_processes"

Po przeliczeniu ZMI wykonanie polecenia

    >: getAttributeValue / num_processes

powinno wypisać dla każdego agenta zagregowaną wartość atrybutu `num_processes` w korzeniu.