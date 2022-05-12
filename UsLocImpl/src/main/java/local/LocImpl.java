package local;

import UsComponent.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Lokalna implementacija koja koristi nasu specifikaciju.
 *
 * @author Igor
 */

public class LocImpl extends UsSpec {

    static
    {
        ImplManager.registerExporter(new LocImpl()); //class for name pravi lokalnu implementaciju
        ImplManager.setCliOptions(Arrays.asList(CliOptions.values()));
    }

    @Override
    public Storage initStorage(String path) {
        System.out.println("Creating storage . . .");
        if(new File(path).mkdirs()) {
            Storage storage = new Storage(path);
            storage.setJsonPath(path+"\\users.json");
            storage.setKofnigPath(path+"\\konfig.json");
            return storage;
        }
        return  null;
    }

    @Override
    public boolean createFile(String name) {
        String s2 = name;
        int brojIt = 1;
        if(name.contains("#")){
            String niz[] = name.split("#",2);
            brojIt = Integer.parseInt(niz[1]);
            if(brojIt < 1){
                brojIt = 1;
            }
            name = niz[0];
        }

        for(int j = 0; j < brojIt; j++ ) {


            String dodatak = String.valueOf(j + 1);
            if(j == 0){
                dodatak = "";
            }

            try {

                File file = new File(getStRoot() + "\\"+dodatak + name);
                File parent = file.getParentFile();
                int maxFiles = 0;

                String putanja = parent.getAbsolutePath().replace("\\", "/");

                List<String> ekstenzije = new ArrayList<>();

                for (NasFolder nasFolder : konfigStorage.getFolders()) {
                    if (nasFolder.getFolderId().equals(putanja)) {
                        maxFiles = nasFolder.getFileNumber();
                        ekstenzije = nasFolder.getZabranjeneEkstenzije();
                        break;
                    }
                }
                int childNum = 0;

                try {
                    childNum = parent.listFiles().length;
                } catch (Exception e) {

                }
                if (childNum + 1 > maxFiles && maxFiles != 0) {
                    return false;
                }
                for (String eks : ekstenzije) {
                    if (name.endsWith(eks)) {
                        return false;
                    }
                }

                file.createNewFile();


            } catch (IOException e) {
                String noviString = "";
                List<String> folder = Arrays.asList(name.split("/"));
                for (int i = 0; i <= folder.size() - 2; i++) {
                    noviString += folder.get(i);
                    if (i == folder.size() - 2) {
                        break;
                    }
                    noviString += "/";
                }
                createDirectory(noviString);
                createFile(name);
            }


        }
        return true;
    }

    @Override
    public boolean createDirectory(String name) {
        File file=new File(getStRoot()+"\\"+name);
        if(file.mkdirs())
            return true;
        else
            return false;
    }

    @Override
    public boolean moveFile(String filePath, String destinationPath) {

        double maxSize = konfigStorage.getStorageSize();

        String fromFile = getStRoot()+"\\"+filePath;
        File file =new File(fromFile);

        if(!file.exists()) {

            return false;
        }
        if(new File(getStRoot()+"/"+destinationPath).exists()==false)
        {

            return false;
        }

        String toFile = getStRoot()+"\\"+destinationPath;

        File toF = new File(toFile);





        Path source = Paths.get(fromFile);
        Path target = Paths.get(toFile+"\\"+file.getName());

        try {
            long bytes1 = Files.size(Path.of(toF.getPath()));
            long bytes2 = Files.size(Path.of(file.getPath()));          //Ogranicenje o velicini
            if(bytes1 + bytes2 > maxSize && maxSize!=0.0){
                return false;
            }
            Files.move(source, target);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean moveDirectory(String dirPath, String destinationPath) {

        int dF = 0;

        if(destinationPath.endsWith("Download")){
            dF = 1;
        }

        File source=new File(getStRoot()+"\\"+dirPath);
        File[] files=source.listFiles(); //cuvam fajlove dira u niz.


        //String desPath=getStRoot()+"\\"+destinationPath+"\\"+source.getName(); //Pravljenje putanje za novu destinaciju.

        String tempPath=destinationPath+"\\"+source.getName(); //za potrebe funkcije moveFile.

        //File destination=new File(desPath); //Kreiranje nove destinacije.

        if(!source.exists())
        {

            return false;
        }

           createDirectory(tempPath);

        for(File f:files)
        {
            moveFile(dirPath+"\\"+f.getName(),tempPath);
            //koristim prethodnu f-ju da pomerim sve fajlove u novu dest.
        }

        if(dF == 0)
        deleteDirectory(dirPath);
        //brisem prethodni dir.


        return  true;
    }

    @Override
    public boolean deleteFile(String deletePath) {
        File file=new File(getStRoot()+"\\"+deletePath);
        if(file.delete())
            return true;
        else
            return false;
    }

    @Override
    public boolean deleteDirectory(String name) {
        String dirFile=getStRoot()+"\\"+name;
        File dir=new File(dirFile);

        //brisemo sve fajlove u diru pa onda dir.
        if(dir.isDirectory() && dir.exists()) {
            for (final File fileEntry : dir.listFiles()) {
                fileEntry.delete();
            }
            System.out.println(dir.listFiles().length);
            dir.delete();
            return true;
        }
        else
            return false;
    }

    @Override
    public boolean download(String path) {
        File file = new File(storage.getStoragePath()+"/"+path);

        String downloadPath = "C:\\Users\\Milos Aleksovski\\Desktop\\Download";
        File file2 = new File(downloadPath);
        if(file.isDirectory()){
            moveDirectory(path, downloadPath);
        }
        else{
            moveFile(path, downloadPath);
        }


        return true;
    }

    @Override
    public List listFilesFromDirectory(String directory) {
        File file=new File(getStRoot()+"\\"+directory);
        File[] files=file.listFiles();
        List<File>listaFajlovi = new ArrayList<>();
        if(file.isDirectory())
        {
            for(File f1:files)
            {
                if(f1.isFile())
                    listaFajlovi.add(f1);
            }
        }
        return listaFajlovi;
    }

    @Override
    public List listDirsFromDirectory(String directory) {
        File file=new File(getStRoot()+"\\"+directory);
        File[] files=file.listFiles();
        List<File>listaFajlovi = new ArrayList<>();
        if(file.isDirectory())
        {
            for(File f1:files)
            {
                if(f1.isDirectory())
                    listaFajlovi.add(f1);
            }
        }
        return listaFajlovi;
    }

    @Override
    public List listFilesByNameInStorage(String name) {

            return  null;
    }

    @Override
    public List listFilesByExtension(String path,String extension) {
        File file=new File(getStRoot()+"\\"+path);
        List<File>listaFajlovi = new ArrayList<>();
        FileFilter fileFilter = new FileFilter()
        {
            @Override
            public boolean accept(File file) {
                return !file.isDirectory();
            }
        };

        File[] files=file.listFiles(fileFilter);

        for(File f1:files)
        {
            if(f1.getName().endsWith(extension))
                listaFajlovi.add(f1);
        }
        return listaFajlovi;
    }

    @Override
    public List sortFilesByName(String name) {
        List<File>listaFajlovi = new ArrayList<>();
        File source=new File(getStRoot()+"\\"+name);

        File[] fileList;

        FileFilter fileFilter = new FileFilter()
        {
            @Override
            public boolean accept(File file) {
                return !file.isDirectory();
            }
        };

        fileList = source.listFiles(fileFilter);

        Arrays.sort(fileList, new Comparator()
        {
            @Override
            public int compare(Object f1, Object f2) {
                return ((File) f1).getName().compareTo(((File) f2).getName());
            }
        });

        for(File file:fileList)
        {
            listaFajlovi.add(file);
        }
        return listaFajlovi;
    }


    @Override
    public String checkStorage(String path) {


        if(!Files.exists(Path.of(path))) //Paths of koristi javu 11+anotacije//,nio// ...
        {
            return "noPath";

        }
        else
        {
            File file = new File(path);
            File[] files = file.listFiles();
            for(File f:files){
                if(f.getName().equals("users.json")){
                    return path+"\\users.json";
                }
            }

            return "noJson";
        }

    }


}
