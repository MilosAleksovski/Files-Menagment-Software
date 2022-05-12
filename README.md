# UsComponent
  Komponenta za skladištenje i upravljanje fajlovima.

# Opis
  Multimodularni projekat koji je bildan Maven bild alatom i se sastoji od četiri modula 
    1. UsSpec - Specifikacija projekta (API)
    2. UsLocImpl - Lokalna implementacija skladišta.
    3. UsGdriveImpl - Implementacija skladišta na Google drive-u.
    4. UsTest - Testna konzolna aplikacija.
  
# Pokretanje aplikacije
  Testnu aplikaciju je moguće pokrenuti u okviru konzole komandom:
    
    java -jar .\UsTest-1.0-SNAPSHOT.jar [putanja]
    
  gde putanja predstavlja apsolutnu putanju skladišta u lokalnoj, odnosno relativnu putanju
  skladišta u Gdrive implementaciji. Executable jar nalazi se u target folderu UsTest-a
  nakon bildovanja.
  
# Korisnici i korišćenje
  Samo jedan korisnik može koristiti skladište i prilikom pokretanja vrši se autentifikacija.
  Ukoliko skladište ne postoji na zadatoj putanji, kreira se i korisnik koji ga je kreirao je "owner"
  skladišta. Ostali korisnici mogu biti "admin" ili "read only" korisnici.
  Svaki korisnik može videti svoju listu komandi komandom usHelp nakon što unese tačan username i password.
  
 
  

