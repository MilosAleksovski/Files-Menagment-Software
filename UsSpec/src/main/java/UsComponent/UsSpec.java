package UsComponent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.List;

/**
 * Specifikacija komponente upravljanje skladistem.
 *
 * @author Igor , Milos
 */

public abstract class UsSpec {


    /**
     * Polje koje predstavlja skladiste.
     */
    public Storage storage;
    /**
     * Polje koje predstavlja konfiguraciju skladista.
     */
    public KonfigStorage konfigStorage;

    /**
     *  Metoda koja proverava da li je skladiste na odredjenoj putanji skladiste po specifikaciji.
     * @param path
     * @return
     *  1- "noPath" ukoliko putanja ne postoji.
     *  2- "noJson" ukoliko putanja ne sadrzi odgovarajuci JSON fajl za skladiste.
     *  3-  Putanju do JSON fajla za dalju konverziju.
     * @throws IOException
     */
    public abstract String  checkStorage(String path) throws IOException;

    /**
     *  Metoda koja inicijalizuje skladiste i setuje njegove JSON fajlove.
     * @param path
     * Putanja naseg novog skladista.
     * @return
     * Vraca objekat Storage koji predstavlja skladiste.
     * Objekat tipa Storage koji predstavlja skladiste.
     * @throws IOException
     */
    public abstract Storage  initStorage(String path) throws IOException;


    /**
     *
     * @param path
     * @return
     * @throws IOException
     */

    public String initStorage2(String path) throws IOException {
        String pathCheck = checkStorage(path); //Proveri da li skladiste postoji,vrati noPath ukoliko ne postoji putanja,noJson na putanji nema Json fajla,pathCheck putanja gde se json nalazi
        if(pathCheck.equals("noPath")){
            storage=  initStorage(path);
            konfigStorage= new KonfigStorage();
            return  "noPath";
        }
        else if(pathCheck.contains("noJson")){
            return "noJson";
        }
        else{
            convertJson(pathCheck);
            return "truePath";
        }
    }

    /**
     * Metoda koja kreira korisnika.
     * @param username
     * Username korisnika.
     * @param password
     * Password korisnika.
     * @param privilege
     * Privilegija korisnika. Korisnik može biti admin ili readonly.
     */

    public void createUser(String username,String password,Privileges privilege) {
        User user=new User(username, password, privilege);

        for(User users:storage.getUsers()){
            if(users.getUsername().equals(username) &&
                    users.getPassword().equals(password)) {
                System.out.println("Korisnik vec postoji u skladistu!");
                return;

            }
            if(users.getPrivilege().equals(Privileges.owner) && privilege.equals(Privileges.owner)){
                System.out.println("Ownner skladista vec postoji!");
                return;
            }
        }

        storage.addUsers(user);
        if(privilege.equals(Privileges.owner))
            storage.setCurrentUser(user);
        createJson();
    }

    /**
     * Metoda za konekciju na skladiste/login.
     * Proverava username i password korisnika koji pokusava da se konektuje.
     * @param username
     * Username korisnika.
     * @param password
     * Password korisnika.
     * @return
     * true ako su podaci ispravni
     * false ako podaci nisu ispravni
     */

    public boolean login(String username,String password)
    {
        if(storage==null)
            System.out.println("storage null");
        for(User user:storage.getUsers())
        {
            if(user.getUsername().equals(username) &&
                user.getPassword().equals(password)) {
                storage.setCurrentUser(user);
                return true;
            }
        }
        return false;
    }

    /**
     * Metoda koja konvertuje JSON fajlove u odgovarajuce klase.
     * @param path
     *  Putanja JSON fajla za skladiste.
     */

    public void convertJson(String path) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        try {
            storage = gson.fromJson(new FileReader(path),Storage.class);
            konfigStorage = gson.fromJson(new FileReader(storage.getKofnigPath()),KonfigStorage.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda koja kreira JSON fajlove za skladiste.
     * Kreira JSON fajl za skladiste i konfiguracioni JSON fajl.
     */

    public void createJson() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        try {
            Writer writer=new FileWriter(storage.getJsonPath());
            gson.toJson(storage,writer);
            writer.flush();
            writer=new FileWriter(storage.getKofnigPath());
            gson.toJson(konfigStorage,writer);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metoda koja postavlja ogranicenje za velicinu skladista.
     * Update JSON fajlova sa novim promenama tako sto se ponovo poziva funkcija createJSON.
     * @param size
     * Ogranicenje velicine skladista.
     * @return
     */

    public boolean setStorageSize(Double size){
        konfigStorage.setStorageSize(size);
        createJson();
        return true;
    }

    /**
     * Metoda koja postavlja ogranicenje za maksimalan vroj fajlova u okviru skladista.
     * @param size
     * Ogranicenje broja fajlova.
     * @param path
     * Putanja gde postavljamo ogranicenje.
     * @return
     */

    public boolean setMaxSize(int size,String path){
        for(NasFolder nasFolder:konfigStorage.getFolders()){
            if(nasFolder.getFolderId().equals(storage.getStoragePath()+"/"+path)){
                nasFolder.setFileNumber(size);
                createJson();
                return true;
            }
        }
        NasFolder nasFolder = new NasFolder();
        nasFolder.setFolderId(storage.getStoragePath()+"/"+path);
        nasFolder.setFileNumber(size);
        konfigStorage.addFolder(nasFolder);
        createJson();
        return true;
    }

    /**
     * Metoda koja zabranjuje odredjenje ekstenzije u skladistu.
     * @param putanja
     * Putanja gde postavljamo ogranicenje.
     * @param ekstenzija
     * Nepozeljna ekstenzija.
     * @return
     */

    public boolean setExt(String putanja,String ekstenzija){
        for(NasFolder nasFolder:konfigStorage.getFolders()){
            if(nasFolder.getFolderId().equals(storage.getStoragePath()+"/"+putanja)){
                nasFolder.dodajEkstenziju(ekstenzija);
                createJson();
                return true;
            }
        }
        NasFolder nasFolder = new NasFolder();
        nasFolder.setFolderId(storage.getStoragePath()+"/"+putanja);
        nasFolder.dodajEkstenziju("."+ekstenzija);
        konfigStorage.addFolder(nasFolder);
        createJson();
        return true;
    }

    /**
     * Metoda koja kreira fajl na osnovu prosledjenog imena.
     * Fajl mora imati ekstenziju.
     * @param name
     * Ime fajla sa svojom ekstenzijom.
     * @return
     * true ako je fajl uspesno kreiran
     * false ako fajl nije kreiran.
     */
    public abstract boolean createFile(String name);

    /**
     * Metoda koja kreira direktorijum na osnovu prosledjenog imena.
     * @param name
     * @return
     * true ako je fajl uspesno kreiran
     * false ako fajl nije kreiran.
     */
    public abstract boolean createDirectory(String name);

    /**
     *  Metoda koja premesta fajlove sa jednog mesta na drugo.
     *
     * @param filePath
     * Putanja fajla koji se premesta.
     * @param destinationPath
     * Putanja do koje se premesta fajl.
     * @return
     * true ukoliko je fajl uspesno premesten.
     * false ukoliko fajl nije premesten.
     */
    public abstract boolean moveFile(String filePath,String destinationPath);

    /**
     * Metoda koja premesta direktorijum sa jednog mesta na drugo.
     *
     * @param dirPath
     * Putanja direktorijuma koji se premesta.
     * @param destinationPath
     * Putanja do koje se premesta direktorijum.
     * @return
     * true ukoliko je direktorijum uspesno premesten.
     * false ukoliko direktorijum nije premesten.
     */
    public abstract boolean moveDirectory(String dirPath,String destinationPath);

    /**
     * Metoda koja brise fajl sa odredjene putanje u skladistu.
     * @param deletePath
     * Putanja fajla koji se brise.
     * @return
     * true ukoliko je fajl uspesno obrisan.
     * false ukoliko fajl nije obrisan.
     */
    public abstract boolean deleteFile(String deletePath);

    /**
     * Metoda koja brise direktorijum sa odredjene putanje u skladistu.
     * @param name
     * Putanja direktorijuma koji se brise.
     * @return
     * true ukoliko je direktorijum uspesno obrisan.
     * false ukoliko direktorijum nije obrisan.
     */
    public abstract boolean deleteDirectory(String name);

    /**
     * Metoda koja preuzima fajl sa skladista.
     * @param path
     * Putanja fajla
     * @return
     * True ukoliko je fajl uspesno preuzet.
     * false ukoliko fajl nije uspesno preuzet.
     */
    public abstract boolean download(String path);

    /**
     *  Metoda koja izlistava sve fajlove iz direktorijuma.
     * @param directory
     * Putanja direktorijuma koji se izlistava.
     * @return
     * Listu svih fajlova koji se nalaze u direktorijumu.
     */
    public abstract List listFilesFromDirectory(String directory);

    /**
     *  Metoda koja izlistava sve direktorijuma iz direktorijuma.
     * @param directory
     * Putanja direktorijuma koji se izlistava.
     * @return
     * Listu svih direktorijuma koji se nalaze u direktorijumu.
     */
    public abstract List listDirsFromDirectory(String directory);

    /**
     *
     * @param path
     * @return
     */
    public abstract List listFilesByNameInStorage(String path);

    /**
     *  Metoda koja izlistava sve direktorijuma iz direktorijuma koji imaju prosledjenu ekstenziju.
     * @param path
     * Putanja direktorijuma koji se izlistava.
     * @param extenstion
     * Ekstenzija po kojoj se vrsi izlistavanje.
     * @return
     * Listu svih direktorijuma koji se nalaze u direktorijumu i imaju prosledjenu ekstenziju.
     */
    public abstract List listFilesByExtension(String path,String extenstion);

    /**
     * Sortira fajlove u skladistu po imenu.
     * @param name
     * Ime po kojem sortiramo skladiste.
     * @return
     * Listu fajlova u skladistu koji sadrze prosledjeno ime u svom nazivu.
     */
    public abstract List sortFilesByName(String name);

    public KonfigStorage getKonfigStorage() {
        return konfigStorage;
    }
    public void setStorage(Storage storage) {
        this.storage = storage;
    }
    public Storage getStorage() {
        return storage;
    }
    public String getStRoot() {
        return storage.getStoragePath();
    }
    public User getCurrentUser(){
        return storage.getCurrentUser();
    }
}
