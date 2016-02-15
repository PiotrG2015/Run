###Piotr Gorczyca

###Aplikacja mobilna dla biegaczy Run!


####Kr�tki opis dzia�ania aplikacji
Aplikacja ma na celu umo�liwienie jej u�ytkownikom �atwej komunikacji w celu ustalenia wsp�lnych trening�w biegowych, dzi�ki implementacji nast�puj�cych funkcjonalno�ci:
+	Tworzenie nowego konta
+	Edycja profilu
+	Wysy�anie i odbieranie prywatnych wiadomo�ci
+	Przegl�danie profil�w innych u�ytkownik�w
+	Odczytywanie/dodawanie komentarzy na profilach innych u�ytkownik�w
+	Wy�wietlanie listy trening�w dodanych przez u�ytkownik�w
+	Dodawanie/usuwanie trening�w
+	Mo�liwo�� zg�oszenia udzia�u w danym treningu
+	Wyszukiwanie u�ytkownik�w


####Technologie zastosowane przy budowie aplikacji oraz bazy danych
Projekt sk�ada si� z dw�ch cz�ci:
+	Bazy danych zaimplementowanej w MySQL do kt�rej obs�ugi u�y�em og�lnodost�pnego frameworka Phalcon PHP. Do jego wyboru przekona�y mnie liczne opinie nt. jego wysokiej wydajno�ci w por�wnaniu do innych popularnych framwork�w.
Stworzy�em proste API, kt�re ma za zadanie u�atwi� komunikacj� z aplikacj� poprzez pobieranie danych oraz kodowanie ich w formacie JSON (analogicznie w przypadku zapisu danych).

+	Aplikacji android do kt�rej utworzenia wykorzysta�em bibliotek� Volley u�atwiaj�c� tworzenie zapyta�.

####Opis struktury bazy danych
 

Baza danych sk�ada si� z 6 tabel:
+	users � tabela reprezentuj�ca pojedynczego u�ytkownika
+	trainings � tabela reprezentuj�ca pojedynczy trening
+	comments � tabela reprezentuj�ca pojedynczy komentarz
+	messages � tabela reprezentuj�ca pojedy�cz� wiadomo��
+	attendings � tabela reprezentuj�ca powi�zanie pomi�dzy treningiem a osob� bior�c� w nim udzia�
+	profiles � tabela reprezentuj�ca profil pojedynczego u�ytkownika

####Struktura zbudowanego API
![Struktura zbudowanego API](http://i.imgur.com/kYHqBUS.png "Struktura zbudowanego API")


Implementacje powy�szych metod znajduj� si� w pliku index.php, w katalogu api/ projektu.
Opr�cz standardowych zapyta� typu SELECT, INSERT, UPDATE, DELETE stworzy�em r�wnie� kilka funkcji pomocniczych w bazie MySQL:
+	getAverageDistanceByGender(Gender VARCHAR(10)) � zwraca �redni dystans trening�w wzgl�dem p�ci
+	getMaxDistanceById(id INT(11)) � zwraca maksymalny przebiegni�ty dystans przez u�ytkownika o podanym id
+	getNewUserCountByGender(Gender VARCHAR(10)) � zwraca liczb� nowych u�ytkownik�w zarejestrowanych w bie��cym miesi�cy wzgl�dem p�ci
+	getTotalDistanceById(id INT(11)) � zwraca ca�kowity dystans przebiegni�ty przez osobe o podanym id
+	idIntoUniqueId(id INT(11)) � zwraca unikalne id u�ytkownika bazuj�c na publicznym id, funkcja s�u�y do przypisania wiadomo�ci do odpowiedniego odbiorcy, bez znajomo�ci jego unikalnego (prywatnego) id
Utworzy�em trigger odpowiedzialny za dodanie nowego wiersza do tabeli profiles po utworzeniu nowego konta, a tak�e widok do wy�wietlania wszystkich przysz�ych trening�w.
#####Struktura plik�w katalogu api/  :
 
+	.htaccess � plik konfiguracyjny serwera Apache
+	index.php � plik zawieraj�cy implementacj� podanych wy�ej metod GET, POST, PUT, DELETE
+	config.php � plik zawieraj�cy dane potrzebne do po��czenia si� z serwerem lokalnym oraz baz� danych takie jak: nazwa u�ytkownika, has�o, nazwa bazy danych
+	PassHash.php � plik zawieraj�cy implementacj� funkcji odpowiedzialnych za (de)kodowanie has�a oraz sprawdzanie jego poprawno�ci
+	models/ - folder zawieraj�cy pliki php opisuj�ce model danych zawartych w poszczeg�lnych tabelach

####Opis struktury aplikacji mobilnej
Aplikacja sk�ada si� na 12 �ekran�w�, s� to:
+	Ekran logowania
+	Ekran rejestracji
+	Lista trening�w
+	Ekran danego treningu
+	Profil u�ytkownika
+	Statystyki
+	Wiadomo�ci
+	Ekran czatu
+	Edycja danych profilu
+	Ekran dodawania komentarza (formularz)
+	Ekran dodawania treningu (formularz)
+	Wyszukiwanie u�ytkownik�w

 
Zarz�dzanie sesj� logowania
Aplikacja dodatkowo korzysta z bazy SQLite, aby przechowywa� najcz�ciej u�ywane, powtarzaj�ce si� dane.
Tabela przechowuje dane aktualnie zalogowanego u�ytkownika takie jak:
+	Imi�
+	Adres e-mail
+	Unikalne id
+	Publiczne id
+	Dat� utworzenia konta

Dane te przekazywane s� podczas pomy�lnego zalogowania do aplikacji i przechowywane do momentu wylogowania si� przez u�ytkownika.
W celu zarz�dzania sesj� logowania stworzy�em klas� o nazwie SessionManager, w kt�rej przy pomocy Shared Preferences przechowuj� warto�� typu boolean m�wi�c� o tym czy u�ytkownik jest zalogowany. Po pomy�lnym zalogowaniu warto�� wspomnianego pola ustawiana jest na true, a przypadku wylogowania na false.

####Przyk�adowe screeny z dzia�aj�cej aplikacji
![Przyk�adowe screeny z dzia�aj�cej aplikacji](http://i.imgur.com/SNp6lBa.png "Przyk�adowe screeny z dzia�aj�cej aplikacji")


�r�d�a, z kt�rych korzysta�em:
- http://www.icons4android.com/iconset/25
- http://developer.android.com/training/material/index.html
- http://www.androidhive.info/
- https://docs.phalconphp.com/en/latest/index.html

