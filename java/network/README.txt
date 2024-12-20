Projekt SKJ - Jakub Korzynski

W projekcie zostaly zaimplementowane wszystkie funkcje przedstawione w wymaganiach.

______________________________________________________________

	Klasy:

______________________________________________________________

	Klasa DatabaseNode - glowna klasa programu. Interpretuje poczatkowe argumenty i wykonuje polaczenia do przekazanych wezlów. W razie wyjatku próba zostaje zerwana, a polaczenie nie zostaje dodane do puli connections. Po wstepnych polaczeniach, oczekuje komunikacji od innych wezlów. 
	
	Pola DatabaseNode:

	-> connections - lista wszystkich obecnych polaczen.
	-> currentRequests - set przechowujacy wszystkie obecnie wykonywane zapytania wezla, za wyjatkiem polecen TERMINATING_ON_REQUEST i NOT_A_CLIENT. Zapobiega to zapetlaniu zapytan w sieci.
	-> listenOn - port, na którym wezel nasluchuje nadchodzacych polaczen.

	Metody DatabaseNode:

	-> terminateNode() - iteratorem, przechodzi po liscie connections i je przerywa, po czym wychoodzi z programu. 
	-> checkThreads() - wedlug pola ConnectionThread isTerminating sprawdza, czy polaczenia w liscie connections sa juz gotowe do wyrzucenia, i jeseli tak, to je wyrzuca.
	-> getValidConnections() - zwrcana liste zdatnych do komunikacji polaczen

______________________________________________________________

	
	Klasa ConnectionThread - klasa odpowiedzialna za utrzymywanie polaczen i komunikacje miedzy wezlami. Stale nasluchuje przychodzacych wiadomosci i je interpretuje. 

	Pola ConnectionThread:

	-> socket, in, out - obiekty Socket, BufferedReader i PrintWriter odpowiedzialne za polaczenie i komunikacje.
	-> ip, port - ip i port komunikacyjny wezla, z którym nawiazane zostalo polaczenie.
	-> isTerminating - w przypadku wywolania metody terminate(), zostaje ustawiony na true, wykorzystany w checkThreads() klasy DatabaseNode.
	-> counter - statyczny licznik polaczen.
	-> privateCount - ID polaczenia.
	-> isClient - czy obecne polaczenie jest z klientem, jezeli tak to po odeslaniu odpowiedzi wywolywana jest metoda terminate().
	-> communicationHandler - obiekt klasy CommunicationHandler odpowiedzialny za interpretacje odpowiedzi na zapytania i sprawdza, czy odpowiedz jest juz gotowa, tj. czy na dane zapytanie odpowiedzialy juz wszystkie sasiadujace wezly.

	Metody ConnectionThread:

	-> run() - przeciazona metoda klasy Thread, odpowiedzialna za zbieranie wysylanych na port nowych wiadomosci i ich interpretacje. Sprawdza, czy wiadomosc jest przekazana od innego wezla (FORWARD/) oraz czy zostala zapetlona. W przypadku zapetlenia, od razu odsyla odpowiedz ERROR (odpowiednio sformatowana pod protokol). W razie odpowiedzi, przekazuje ja do interpretacji CommunicationHandlerowi. W przypadku rzucenia wyjatku, polaczenie zostaje zerwane i wywolywana jest metoda terminate().
	->  setValue(String arg) - metoda odpowiedzialna za operacje set-value. String arg zostaje rozdzielony na int setKey (klucz szukany) i int setValue (wartosc, ktora ma zostac po dokonaniu zamiany). W przypadku trafienia klucza (getKey), odslyane OK, w przeciwnym przypadku pytanie zostaje przeslane do pierwszego polaczenia z innym wezlem sieci (dalszymi zajmuje sie communicationHandler). 
	-> getValue() - metoda odpowiedzialna za operacje get-value. W razie trafienia klucza (getKey), odsyla odpowiedz, w przeciwnym przypadku pytanie zostaje przeslane do pierwszego polaczenia z innym wezlem sieci (dalszymi zajmuje sie communicationHandler). 
	-> findKey() - metoda odpowiedzialna za operacje find-key. W razie trafienia klucza (getKey), zwracany jest lokalny adres i port wezla, w przeciwnym przypadku pytanie zostaje przeslane do pierwszego polaczenia z innym wezlem sieci (dalszymi zajmuje sie communicationHandler). 
	-> getMin(), getMax() - konstruuje pierwsza odpowiedz do dalszych porownan, po czym wysyla zapyatnie do pierwszego mozliwego wezla. Dalszym porownaniem zajmuje sie communicationHandler.
	-> newRecord(String record) - metoda odpowiedzialna za operacje new-record. String record zostaje rodzielony na klucz i wartosc po czym obecne wartosci zostaja nadpisane.
	-> terminateAll() - metoda odpowiedzialna za operacje terminate. Rozsyla do wszystkich, poza wysylajacym zapytanie, polaczen komunikat o zamknieciu wezla, a do wysylajacego zapytanie zwraca komunikat OK i wywoluje metode terminateNode() klasy DatabaseNode.
	-> send(String str, ConnectionThread author, ListIterator<ConnectionThread> iter) - metoda odpowiedzialna za pierwszy komunikat w sprawie nowego zapytania z innymi wezlami. Do listy obecnych zapytan zostaje dodane zapytanie, a do communicationHandlera dodatkowo iterator listy polaczen, po czym zapytanie wysyla do pierwszego wezla wskazanego w metodzie operacji.
	-> sendNext(String str)- metoda wysylajaca wiadomosc na polaczeniu, zakladajaca ze wykonywane zadanie jest juz zapisane w liscie i w communicationHandlerze.
	-> terminate() - metoda zmieniajaca status polaczenia na gotowy do usuniecia oraz zamykajaca strumienie i socket.
	-> countConnections() - pokazuje informacje o ilosci obecnie utrzymywanych polaczen. 


______________________________________________________________



	Wewnetrzna klasa Request - klasa pomocnicza, przetrzymujaca wszystkie potrzebne do przetwozania zapytania informacje.

	Pola klasy Request:

	-> author - polaczenie, na ktorym przyszlo zapytanie.
	-> isOK - czy zapytanie dostalo choc jedna odpowiedz OK.
	-> isReady - czy zapytanie zostalo juz przetworzone przez wszystkich sasiadow wezla.
	-> forwardString - string zapytania rozslyany do sasiadow wezla.
	-> currentMin, currentMax - wartosci do zapytan get-min, get-max.

	-> nextSend - ListIterator wskazujacy na nastepny wezel do komunikacji.

	Klasa CommunicationHandler - klasa odpowiedzialna za sledzenie postepu wszystkich wykonywanych przez wezel zapytan. 

	Pola klasy CommunicationHandler:

	-> requestAuthorMap - mapa przetrzymujaca pary zapytan i informacji o nich.

	Metody klasy CommunicationHandler:

	-> addRequest(String request, ConnectionThread author, ListIterator<ConnectionThread> iter) - dodaje do mapy nowe zapytanie.
	-> getRequest(String reply) - zwraca Request zawierajacy dany identifikator (czesc reply).
	-> setOK - zmienia status zapytania na OK
	-> removeRequest(String reply) - usuwa z mapy gotowe zapytanie.
	-> getAuthor(String reply) - zwraca polaczenie, z ktorego przyszlo zapytanie o danym identyfikatorze (czesc reply).
	-> handleReply(String reply) - przetwarza odpowiedz na zapytanie. Sprawdza, czy byla to ostatnia potrzebna odpowiedz; jezeli tak do zwraca odpowiedz ostateczna na polaczenie authora, w przeciwnym wypadku rozsyla zapytanie dalej.

	

______________________________________________________________


	Protokol:
	
	-> Protokol opiera sie na jedno liniowych zapytaniach i odpowiedziach. Jezeli zapytanie jest przyjmowane na polaczeniu od klienta, zostaje odpowiednio nadpisane, z kolei odpowiedz zostanie odpowiednio skrocona.

	Wzor przekazywanego zapytania:
	
	FORWARD / adres-na-ktorym-zostalo-odebrane : port-na-ktorym-zostalo-odebrane : zapytanie / zapytanie

	Wzor przekazywanej odpowiedzi:

	REPLY / adres-na-ktorym-zostalo-odebrane : port-na-ktorym-zostalo-odebrane : zapytanie / odpowiedz

	Pierwsza czesc, FORWARD lub REPLY, oznacza, ze jest to zapytanie przekazane od innego wezla sieci lub odpowiedz. Od tego zalezy, czy zapytanie zostanie wpisane na liste wykonywanych (w przypadku FORWARD zostanie wpisane) lub czy zostanie przekazane communicationHandlerowi (REPLY). 

	Srodkowa czesc wiadomosci, czyli adres:port:zapytanie/odpowiedz, stanowi identyfikator przekazywanego zapytania, lub sluzy do zidentyfikowania zapytania na ktore jest odpowiedzia.

	Trzecia czesc, czyli sama wiadomosc operacji lub czysta odpowiedz, zostaje przetworzona i przechodzi przez switch(reply) metody run(). 

	Komunikaty:

	-> NOT_A_CLIENT – komunikat rozsylany na poczatku polaczenia zainicjowanego nie na potrzeby klienta. Swiadczy o braku potrzeby zamkniecia polaczenia po odeslaniu odpowiedzi.
	-> TERMINATING_ON_REQUEST – komunikat o zamknieciu sasiedniego wezla
	-> komunikaty STATUS – nieprzychodzace od innych wezlow, tylko nadpisuja przychodzace wiadomosci, ulatwiaja prace switch(line).
	


________________________________________________________________________________


	Kompilacja i uruchomienie:

	1. Kompilacja
	javac DatabaseNode.java
	javac ConnectionThread.java
	javac CommunicationHandler.java
	javac DatabaseClient.java

	2. Uruchomienie 
	java DatabaseNode -tcpport <numer portu TCP> -record <klucz>:<wartosc> [ -connect <adres>:<port> ]
	java DatabaseClient -gateway <adres>:<numer portu TCP> -operation <operacja z parametrami>


________________________________________________________________________________

	
