###Piotr Gorczyca

###Aplikacja mobilna dla biegaczy Run!


####KrÛtki opis dzia≥ania aplikacji
Aplikacja ma na celu umoøliwienie jej uøytkownikom ≥atwej komunikacji w celu ustalenia wspÛlnych treningÛw biegowych, dziÍki implementacji nastÍpujπcych funkcjonalnoúci:
+	Tworzenie nowego konta
+	Edycja profilu
+	Wysy≥anie i odbieranie prywatnych wiadomoúci
+	Przeglπdanie profilÛw innych uøytkownikÛw
+	Odczytywanie/dodawanie komentarzy na profilach innych uøytkownikÛw
+	Wyúwietlanie listy treningÛw dodanych przez uøytkownikÛw
+	Dodawanie/usuwanie treningÛw
+	MoøliwoúÊ zg≥oszenia udzia≥u w danym treningu
+	Wyszukiwanie uøytkownikÛw


####Technologie zastosowane przy budowie aplikacji oraz bazy danych
Projekt sk≥ada siÍ z dwÛch czÍúci:
+	Bazy danych zaimplementowanej w MySQL do ktÛrej obs≥ugi uøy≥em ogÛlnodostÍpnego frameworka Phalcon PHP. Do jego wyboru przekona≥y mnie liczne opinie nt. jego wysokiej wydajnoúci w porÛwnaniu do innych popularnych framworkÛw.
Stworzy≥em proste API, ktÛre ma za zadanie u≥atwiÊ komunikacjÍ z aplikacjπ poprzez pobieranie danych oraz kodowanie ich w formacie JSON (analogicznie w przypadku zapisu danych).

+	Aplikacji android do ktÛrej utworzenia wykorzysta≥em bibliotekÍ Volley u≥atwiajπcπ tworzenie zapytaÒ.

####Opis struktury bazy danych
 

Baza danych sk≥ada siÍ z 6 tabel:
+	users ñ tabela reprezentujπca pojedynczego uøytkownika
+	trainings ñ tabela reprezentujπca pojedynczy trening
+	comments ñ tabela reprezentujπca pojedynczy komentarz
+	messages ñ tabela reprezentujπca pojedyÒczπ wiadomoúÊ
+	attendings ñ tabela reprezentujπca powiπzanie pomiÍdzy treningiem a osobπ biorπcπ w nim udzia≥
+	profiles ñ tabela reprezentujπca profil pojedynczego uøytkownika

####Struktura zbudowanego API
![Struktura zbudowanego API](http://i.imgur.com/kYHqBUS.png "Struktura zbudowanego API")


Implementacje powyøszych metod znajdujπ siÍ w pliku index.php, w katalogu api/ projektu.
OprÛcz standardowych zapytaÒ typu SELECT, INSERT, UPDATE, DELETE stworzy≥em rÛwnieø kilka funkcji pomocniczych w bazie MySQL:
+	getAverageDistanceByGender(Gender VARCHAR(10)) ñ zwraca úredni dystans treningÛw wzglÍdem p≥ci
+	getMaxDistanceById(id INT(11)) ñ zwraca maksymalny przebiegniÍty dystans przez uøytkownika o podanym id
+	getNewUserCountByGender(Gender VARCHAR(10)) ñ zwraca liczbÍ nowych uøytkownikÛw zarejestrowanych w bieøπcym miesiπcy wzglÍdem p≥ci
+	getTotalDistanceById(id INT(11)) ñ zwraca ca≥kowity dystans przebiegniÍty przez osobe o podanym id
+	idIntoUniqueId(id INT(11)) ñ zwraca unikalne id uøytkownika bazujπc na publicznym id, funkcja s≥uøy do przypisania wiadomoúci do odpowiedniego odbiorcy, bez znajomoúci jego unikalnego (prywatnego) id
Utworzy≥em trigger odpowiedzialny za dodanie nowego wiersza do tabeli profiles po utworzeniu nowego konta, a takøe widok do wyúwietlania wszystkich przysz≥ych treningÛw.
#####Struktura plikÛw katalogu api/  :
 
+	.htaccess ñ plik konfiguracyjny serwera Apache
+	index.php ñ plik zawierajπcy implementacjÍ podanych wyøej metod GET, POST, PUT, DELETE
+	config.php ñ plik zawierajπcy dane potrzebne do po≥πczenia siÍ z serwerem lokalnym oraz bazπ danych takie jak: nazwa uøytkownika, has≥o, nazwa bazy danych
+	PassHash.php ñ plik zawierajπcy implementacjÍ funkcji odpowiedzialnych za (de)kodowanie has≥a oraz sprawdzanie jego poprawnoúci
+	models/ - folder zawierajπcy pliki php opisujπce model danych zawartych w poszczegÛlnych tabelach

####Opis struktury aplikacji mobilnej
Aplikacja sk≥ada siÍ na 12 ÑekranÛwî, sπ to:
+	Ekran logowania
+	Ekran rejestracji
+	Lista treningÛw
+	Ekran danego treningu
+	Profil uøytkownika
+	Statystyki
+	Wiadomoúci
+	Ekran czatu
+	Edycja danych profilu
+	Ekran dodawania komentarza (formularz)
+	Ekran dodawania treningu (formularz)
+	Wyszukiwanie uøytkownikÛw

 
Zarzπdzanie sesjπ logowania
Aplikacja dodatkowo korzysta z bazy SQLite, aby przechowywaÊ najczÍúciej uøywane, powtarzajπce siÍ dane.
Tabela przechowuje dane aktualnie zalogowanego uøytkownika takie jak:
+	ImiÍ
+	Adres e-mail
+	Unikalne id
+	Publiczne id
+	DatÍ utworzenia konta

Dane te przekazywane sπ podczas pomyúlnego zalogowania do aplikacji i przechowywane do momentu wylogowania siÍ przez uøytkownika.
W celu zarzπdzania sesjπ logowania stworzy≥em klasÍ o nazwie SessionManager, w ktÛrej przy pomocy Shared Preferences przechowujÍ wartoúÊ typu boolean mÛwiπcπ o tym czy uøytkownik jest zalogowany. Po pomyúlnym zalogowaniu wartoúÊ wspomnianego pola ustawiana jest na true, a przypadku wylogowania na false.

####Przyk≥adowe screeny z dzia≥ajπcej aplikacji
![Przyk≥adowe screeny z dzia≥ajπcej aplikacji](http://i.imgur.com/SNp6lBa.png "Przyk≥adowe screeny z dzia≥ajπcej aplikacji")


èrÛd≥a, z ktÛrych korzysta≥em:
- http://www.icons4android.com/iconset/25
- http://developer.android.com/training/material/index.html
- http://www.androidhive.info/
- https://docs.phalconphp.com/en/latest/index.html

