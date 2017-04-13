# niezbednik-kreglarza
Niezbędnik Kręglarza - czyli esencjalna aplikacja dla każdego fana kręglarstwa klasycznego.

Aplikacja w fazie wczesnego rozwoju.

ETA@pierwsza glowna wersja: 2-3 tygodnie.

# Co muszę zrobić przed wyjściem aplikacji

**Na głównym ekranie:**
- ~*dodać filtrowanie wyników* - w sumie, search bar starczy~
- ~sortowanie po zawodnikach/klubie~
- ~szukajka w appbarze~
- ~dodac cokolwiek na starcie co zacheca do dodania wynikow nowego uzytkownika~

**Na wyniku:**
raczej nic, ~chyba że jakieś statystyki drobne etc~

**W statystykach:**
- ~powiadomic ze nalezy dodac >2 wyniki aby byly wykresy, i ogarnac co sie dzieje przed ta liczba~
- ~obecnie wszystkie wyniki to jeden zawodnik - trzeba dodac filtrowanie~
- ~i zarazem zmiane pomiedzy zawodnikami~
- ~tabelki ze statystykami~

**Ogółem:**
- dostosowac bardziej aplikacje do material design [rozne drobne duperele]
- ~ogarnac navbar~ i zrobic view o mnie/kontakt
- ~moze cos zrobie z baza danych, bo obecna jest smieszna [ale i tak lepiej niz w pliku, bo moge chociaz migracje zrobic]~
- ~krotki tutorial przy pierwszym uruchomieniu aplikacji~
- ~zmiany QoL w stylu cofania sie do gory przy zapomnieniu o jakims polu w ZmianaActivity~

# Potencjalne ficzery
*Czyli przydatne, lecz nie esencjalne - raczej dodam po pierwszym wyjsciu*

- możliwość dodawania treningow
- zmiana kolorystyki aplikacji [np. tryb nocny]
- możliwość dodawania sprintow [w sumie malo roboty by to wymagalo]
- jakis eksport do .xls/.csv czy cos w tym stylu? 
- mozliwosc ustawienia przypomnien o treningu

# Warte dodania, lecz niewarte starań

**Automatyczne importowanie wyników z arkuszów google**
Każdy turniej jest wpisany w docsy inaczej. Każdy ma pelne, zbierane, dziury itd w innej kolumnie, niektore maja tory tuz po wyniku, niektorzy maja finaly tuz po, etc. - nie da się tego zrobić automatycznie bez zbednego pieprzenia sie.

**Automatyczne wczytywanie wynikow z karteczki**
Wymaga bardzo dobrej znajomosci OpenCV lub innej biblioteki do computer vision. Wykracza poza moje kompetencje

**Przegladanie kregle.net wewnatrz aplikacji**
Bez sensu. Strona ma wersje mobilna oraz jest bardziej zlozona niz to, co jest na feedzie RSS. Od czegos istnieje przegladarka.
