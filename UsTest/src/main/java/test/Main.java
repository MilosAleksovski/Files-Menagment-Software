package test;

import UsComponent.*;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        try {
            Class.forName("gdrive.GdriveImpl");
        }catch(Exception e) {
            try {
                Class.forName("local.LocImpl");
            }catch(Exception e1) {
                System.err.println("Nije pronadjena implementacija!");
                System.exit(0);
            }
        }
        String path=args[0];
        UsSpec spec= ImplManager.getUsSpec();
        String check =spec.initStorage2(path);

        if(check =="noJson"){
            System.out.println("Putanja postoji ali se na njoj ne nalazi skladiste");
            System.out.println("Pokrenite program sa drugom putanjom");
            return;
        }

        Scanner unos=new Scanner(System.in);
        System.out.println("username:");
        String username=unos.next();
        System.out.println("password:");
        String password=unos.next();

        if(check=="noPath")
        {
            spec.createUser(username,password,Privileges.owner);
            System.out.println("Korisnice  " + username + " Vi ste "+spec.getCurrentUser().getPrivilege() +" skladista.\nUkucajte usHelp za listu komandi.");

        }
        else if(check == "truePath")
        {
            while(true) {
                boolean login = spec.login(username, password);
                if (login)
                    break;
                else {
                    System.out.println("Netacni podaci\nUnesite username i password:");
                    System.out.println("username:");
                    username = unos.next();
                    System.out.println("password:");
                    password = unos.next();
                }
            }
            System.out.println("Korisnice:" + spec.getCurrentUser().getUsername() + " uspesno ste se loginovali," +
                    "Vi ste "+spec.getCurrentUser().getPrivilege() +" skladista.\nUkucajte usHelp za listu komandi.");
        }
        String putanja;
        while(true)
        {
            Scanner sc=new Scanner(System.in);
            String command=sc.next();
            if(command.equals("break"))
                break;
            try {
                if (ImplManager.getCliOptions().contains(CliOptions.valueOf(command))) {
                    switch (command) {
                        case "usHelp":
                            help(spec.getCurrentUser().getPrivilege().toString());
                            break;
                        case "SetInvalidExtension":
                            if (spec.getCurrentUser().getPrivilege().equals(Privileges.owner)) {
                                System.out.println("Unesite putanju fajla");
                                putanja = unos.next();
                                System.out.println("Unesite ekstenziju");
                                String ekst = unos.next();
                                if (spec.setExt(putanja,ekst)) {
                                    System.out.println("Uspesno ste postavili ekstenziju");
                                } else System.out.println("Neuspesno postavljanje ekstenzije");
                            }
                            else System.out.println("Nemate pristup datoj funkciji!");
                            break;
                        case "SetMaxFileInFolder":
                            if (spec.getCurrentUser().getPrivilege().equals(Privileges.owner)) {
                                System.out.println("Setujte maksimalan broj fajlova");
                                int velicina = unos.nextInt();
                                System.out.println("Unesite putanju direktorijuma");
                                putanja = unos.next();
                                if (spec.setMaxSize(velicina, putanja)) {
                                    System.out.println("Uspesno ste kreirali fajl");
                                } else System.out.println("Neuspesno kreiranje fajla");
                            } else System.out.println("Nemate pristup datoj funkciji!");
                            break;
                        case "SetStorageSize":
                            if (spec.getCurrentUser().getPrivilege().equals(Privileges.owner)) {
                                System.out.println("Unesite velicinu u bajtovima");
                                Double velicina = unos.nextDouble();
                                if (spec.setStorageSize(velicina)) {
                                    System.out.println("Uspesno ste kreirali fajl");
                                } else System.out.println("Neuspesno kreiranje fajla");
                            } else System.out.println("Nemate pristup datoj funkciji!");
                            break;
                        case "crtFile":
                            if (!spec.getCurrentUser().getPrivilege().equals(Privileges.readonly)) {
                                System.out.println("Unesite putanju");
                                putanja = unos.next();
                                if (spec.createFile(putanja)) {
                                    System.out.println("Uspesno ste kreirali fajl");
                                } else System.out.println("Neuspesno kreiranje fajla");
                            } else System.out.println("Nemate pristup datoj funkciji!");
                            break;
                        case "download":
                            if (!spec.getCurrentUser().getPrivilege().equals(Privileges.readonly)) {
                                System.out.println("Unesite putanju");
                                putanja = unos.next();
                                if (spec.download(putanja)) {
                                    System.out.println("Uspesno ste skinuli fajl");
                                } else System.out.println("Neuspesno skidanje fajla, fajl ne postoji");
                            } else System.out.println("Nemate pristup datoj funkciji!");
                            break;
                        case "crtDir":
                            if (!spec.getCurrentUser().getPrivilege().equals(Privileges.readonly)) {
                                System.out.println("Unesite putanju");
                                putanja = unos.next();
                                if (spec.createDirectory(putanja)) {
                                    System.out.println("Uspesno ste kreirali direktorijum");
                                } else System.out.println("Neuspesno kreiranje direktorijuma");
                            } else System.out.println("Nemate pristup datoj funkciji!");
                            break;
                        case "mvFile":
                            if (!spec.getCurrentUser().getPrivilege().equals(Privileges.readonly)) {
                                System.out.println("Unesite putanju od");
                                putanja = unos.next();
                                System.out.println("Unesite putanju do");
                                String putanjado = unos.next();
                                if (spec.moveFile(putanja, putanjado)) {
                                    System.out.println("Uspesno ste premestili file");
                                } else {
                                    System.out.println("Zadata putanja ne postoji");
                                }
                            } else System.out.println("Nemate pristup datoj funkciji!");
                            break;
                        case "mvDir":
                            if (!spec.getCurrentUser().getPrivilege().equals(Privileges.readonly)) {
                                System.out.println("Unesite putanju od");
                                putanja = unos.next();
                                System.out.println("Unesite putanju do");
                                String putanjado = unos.next();
                                if (spec.moveDirectory(putanja, putanjado)) {
                                    System.out.println("Uspesno ste premestili direktorijum");
                                } else {
                                    System.out.println("Zadata putanja ne postoji");
                                }
                            } else System.out.println("Nemate pristup datoj funkciji!");
                            break;
                        case "delFile":
                            if (!spec.getCurrentUser().getPrivilege().equals(Privileges.readonly)) {
                                System.out.println("Unesite putanju");
                                putanja = unos.next();
                                if (putanja.endsWith("users.json")) {
                                    System.out.println("Ne mozete obrisati users file!");
                                    break;
                                }
                                if (spec.deleteFile(putanja)) {
                                    System.out.println("Uspesno ste obrisali file");
                                } else {
                                    System.out.println("Zadata putanja ne postoji");
                                }
                            } else System.out.println("Nemate pristup datoj funkciji!");
                            break;
                        case "delDir":
                            if (!spec.getCurrentUser().getPrivilege().equals(Privileges.readonly)) {
                                System.out.println("Unesite putanju ");
                                putanja = unos.next();
                                if (spec.deleteDirectory(putanja)) {
                                    System.out.println("Uspesno ste obrisali direktorijum");
                                } else {
                                    System.out.println("Zadata putanja ne postoji");
                                }
                            } else System.out.println("Nemate pristup datoj funkciji!");
                            break;
                        case "crtUser":
                            if (spec.getCurrentUser().getPrivilege().equals(Privileges.owner)) {
                                System.out.println("Unesite username i password");
                                System.out.println("username:");
                                username = unos.next();
                                System.out.println("password:");
                                password = unos.next();
                                System.out.println("Unesite privilegiju korisnika(1-admin\n2-readonly");
                                String privilege = unos.next();
                                try {
                                    Privileges privileges = Privileges.valueOf(privilege);
                                    spec.createUser(username, password, privileges);
                                } catch (Exception e) {
                                    System.out.println("Zadata privilegija ne postoji!");
                                }
                            } else System.out.println("Nemate pristup datoj funkciji!");
                            break;
                        case "lsFileFromDir":
                            System.out.println("Unesite putanju ");
                            putanja = unos.next();
                            spec.listFilesFromDirectory(putanja);
                            break;
                        case "lsDirFromDir":
                            System.out.println("Unesite putanju ");
                            putanja = unos.next();
                            spec.listDirsFromDirectory(putanja);
                            break;
                        case "lsFileByName":
                            System.out.println("Unesite putanju ");
                            putanja = unos.next();
                            spec.listFilesByNameInStorage(putanja);
                            break;
                        case "lsFileByExtension":
                            System.out.println("Unesite putanju ");
                            putanja = unos.next();
                            spec.listFilesByExtension(putanja, putanja);
                            break;
                        case "sortFileByName":
                            System.out.println("Unesite putanju ");
                            putanja = unos.next();
                            spec.sortFilesByName(putanja);
                            break;
                        case "logOut":
                            System.out.println("Da li zelite da se prijavite(y) ili da napustite storage(n)?\ny/n");
                            String value = unos.next();
                            if (value.equals("y")) {
                                while (true) {
                                    System.out.println("username:");
                                    username = unos.next();
                                    System.out.println("password:");
                                    password = unos.next();
                                    if (spec.login(username, password)) {
                                        System.out.println("Korisnice:" + spec.getCurrentUser().getUsername() + " uspesno ste se loginovali," +
                                                "Vi ste " + spec.getCurrentUser().getPrivilege() + " skladista.\nUkucajte usHelp za listu komandi.");
                                        break;
                                    } else System.out.println("Netacni podaci");
                                }
                            } else {
                                return;
                            }
                            break;
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
                System.out.println("Uneta komanda ne postoji!");
            }
        }
    }

    public static void help(String privilege){
        if(privilege.equals("owner")){
            System.out.println("crtFile- Kreiranje fajla na zadatoj putanji");
            System.out.println("crtDir- Kreiranje direktorijuma na zadatoj putanji");
            System.out.println("delFile- Brisanje fajla na zadatoj putanji");
            System.out.println("delDir- Brisanje direktorijuma na zadatoj putanji");
            System.out.println("mvFile- premestanje fajla na zadatu putanju");
            System.out.println("mvDir- premestanje direktorijuma na zadatu putanju");
            System.out.println("crtUser- kreiranje korisnika u skladistu");
            System.out.println("logOut- odjavite se sa skladista");
            System.out.println("lsFileFromDir- izlistajte fajlove iz direktorijuma");
            System.out.println("lsDirFromDir- izlistajte direktorijume iz direktorijuma");
            System.out.println("lsFileByName- izlistajte fajlove iz storage - a po imenu");
            System.out.println("lsFileByExtension-izlistajte fajlove iz storage - a po ekstenziji");
            System.out.println("sortFileByName- sortirajte fajlove po imenu");
            System.out.println("download- preuzeti zeljeni fajl");
            System.out.println("SetStorageSize- postavite maksimalnu velicinu storage-a");
            System.out.println("SetMaxFileInFolder- postavi ogranicenje od kolicini fajlova u jednom direktorijumu");
            System.out.println("SetInvalidExtension- postavi ogranicenje u vidu ekstenzije");
        }
       else if(privilege.equals("admin")){
            System.out.println("crtFile- Kreiranje fajla na zadatoj putanji");
            System.out.println("crtDir- Kreiranje direktorijuma na zadatoj putanji");
            System.out.println("delFile- Brisanje fajla na zadatoj putanji");
            System.out.println("delDir- Brisanje direktorijuma na zadatoj putanji");
            System.out.println("mvFile- premestanje fajla na zadatu putanju");
            System.out.println("mvDir- premestanje direktorijuma na zadatu putanju");
            System.out.println("logOut- odjavite se sa skladista");
            System.out.println("lsFileFromDir- izlistajte fajlove iz direktorijuma");
            System.out.println("lsDirFromDir- izlistajte direktorijume iz direktorijuma");
            System.out.println("lsFileByName- izlistajte fajlove iz storage - a po imenu");
            System.out.println("lsFileByExtension-izlistajte fajlove iz storage - a po ekstenziji");
            System.out.println("sortFileByName- sortirajte fajlove po imenu");
            System.out.println("download- preuzeti zeljeni fajl");
        }
       else if(privilege.equals("readOnly")){
            System.out.println("logOut- odjavite se sa skladista");
            System.out.println("lsFileFromDir- izlistajte fajlove iz direktorijuma");
            System.out.println("lsDirFromDir- izlistajte direktorijume iz direktorijuma");
            System.out.println("lsFileByName- izlistajte fajlove iz storage - a po imenu");
            System.out.println("lsFileByExtension-izlistajte fajlove iz storage - a po ekstenziji");
            System.out.println("sortFileByName- sortirajte fajlove po imenu");
        }
    }
}
