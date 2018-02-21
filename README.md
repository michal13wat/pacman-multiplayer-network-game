# Pacman multiplayer network game

## Przykładowe screeny z gry single player:

![](https://github.com/michal13wat/pacman-multiplayer-network-game/blob/master/img/SinglePlayer.PNG?raw=true)

## Przykładowe screeny z gry multiplayer

![](https://github.com/michal13wat/pacman-multiplayer-network-game/blob/master/img/Multiplayer.PNG?raw=true)

## Najprostszy sposób na uruchomienie gry:

Najprostszym i najszybszym sposobem na uruchomienie gry jest zainstalowanie jej poprzez plik: Pacman.msi (który znajduje się w głównym folderze repozytorium), po czym uruchomienie z Menu Start -> Pacman.

------------------

# Instrukcja obsługi:

## 1. Opis gry:

Gracz może sterować Pac-manem albo duszkiem. ​Jeżeli gracz steruje Pac-manem, to stara się zebrać wszystkie monety znajdujące się na planszy, unikając przy tym duszków, natomiast jeżeli gra duszkiem, to stara się uniemożliwić Pac-manowi zjedzenie wszystkich monet.

## 2. Zasady gry:

- Gra trwa do czasu zwycięstwa którejś z grup.
- Gracze mogą się poruszać po polach nie będących ścianami.
- Po zjedzeniu Pac-mana przez duszka - jeżeli graczowi pozostały jakieś życia, jest odsyłany na punkt startowy i traci jedno życie. Także duszki
są odsyłane na punkt startowy.
- Każda zebrana moneta daje Pac-manowi 1 pkt. a wiśni 8 pkt.
- Jak Pac-man zje niebieską kulkę, ma przez określony czas możliwość
zjedzenia duszków i usunięcia ich na chwilę z rozgrywki.
- Pac-man ma od 1 do 5 żyć.

## 3. Sterowanie:
- Menu:
  - Strzałki góra / dół - poruszanie się po menu
  - Enter - wybór zaznaczonej opcji
  - Strzałki prawo / lewo - wybieranie opcji w spinerach
  - q - powrót
  - Esc - wyjście z gry
- Gra:
  - Strzałki - poruszanie postacią
  - q - powrót do menu
  - Esc - wyjście z gry

## 4. Opis menu:

- Ekran startowy:
		
	![](https://github.com/michal13wat/pacman-multiplayer-network-game/blob/master/img/Menu1.PNG?raw=true)

	- Single Player ​- przejście do menu gry dla jednego gracza
	- Multi Player ​ - przejście do menu gry wieloosobowej
	- Exit ​ - wyjście z gry


- Menu gry dla jednego gracza:
	
	![](https://github.com/michal13wat/pacman-multiplayer-network-game/blob/master/img/Menu2.PNG?raw=true)

	- Character ​ - wybór postaci którą gracz chce grać
	- Lives ​ - wybór ilości żyć Pacmana ( od 1 do 5 żyć )
	- Ghosts ​ - wybór ilości duszków ( od 1 do 4 )
	- Back ​ - powrót do ekranu startowego
	- Pozostałe opcje - typ rozgrywki

- Menu gry dla wielu graczy

	![](https://github.com/michal13wat/pacman-multiplayer-network-game/blob/master/img/Menu3.PNG?raw=true)
	
	- Create Game ​ - utwórz nową grę wieloosobową
	- Join Game ​ - dołącz do gry
	- Back ​- powrót do ekranu startowego


## 5. Tworzenie nowej gry wieloosobowej

![](https://github.com/michal13wat/pacman-multiplayer-network-game/blob/master/img/CreateGame.PNG?raw=true)

- Start ​ - postawienie serwera, uruchomienie nowej gry wieloosobowej
- Name ​ - nazwa gracza
- Port - port na którym serwer nasłuchuje ​(​ należy się upewnić czy wybrany numer portu nie jest już zajęty)
- Back ​ - powrót do menu gry wieloosobowej
- Adres serwera gry to adres IP komputera na którym uruchomiona została gra ​ (można go sprawdzić w systemach Windows poleceniem ipconfig w konsoli cmd)


## 6. Dołączanie do gry

![](https://github.com/michal13wat/pacman-multiplayer-network-game/blob/master/img/JoinGame.PNG?raw=true)

- Join - dołącz do gry
- Name​ - nazwa gracza
- IP - adres IP serwera ( gracza który stworzył serwer )
- Port - numer portu na którym nasłuchuje serwer
- Back​ - powrót do menu gry wieloosobowej


Na niektórych systemach, niezbędne będzie wyłączenie zapory internetowej w
celu zezwolenia na funkcje sieciowe gry - dla Windows, należy tego dokonać
poprzez:	
Panel Sterowania -> System i zabezpieczenia -> Zapora systemu Windows -> Włącz lub Wyłącz Zaporę

## UWAGA!

Po zakończeniu rozgrywki należy pamiętać o ponownym włączeniu zapory ​, w celu zachowania bezpieczeństwa komputera!

![](https://github.com/michal13wat/pacman-multiplayer-network-game/blob/master/img/zapora.PNG?raw=true)

## 7. Menu wyboru graczy / ekran oczekiwania (w trybie multiplayer)

![](https://github.com/michal13wat/pacman-multiplayer-network-game/blob/master/img/WaitingScreen.PNG?raw=true)

- Character​ - wybór postaci
- Ready​ - zatwierdzenie wyboru
- Exit Server - wyjdź z serwera
- Players - wyświetla graczy, którzy wybrali (zatwierdzili) swoje postaci, i to, jaką postać wybrali.

**Uwaga** ​- gra rozpoczyna się w momencie, w którym wszyscy ​ **podłączeni** gracze aktywowali opcję “ready” (niezależnie od ich aktualnej liczby) - tak więc, należy wstrzymać się z zatwierdzeniem wyboru aż do momentu podłączenia się reszty graczy.

## 8. Autorzy

- Michał Brzózka
- Jan Grzywacz
- Jakub Grajkowski


