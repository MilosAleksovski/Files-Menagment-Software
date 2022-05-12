package gdrive;

import UsComponent.*;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GdriveImpl extends UsSpec {

    static
    {
        ImplManager.registerExporter(new GdriveImpl());
        ImplManager.setCliOptions(Arrays.asList(CliOptions.values()));
    }
    private Drive service;

    private GdriveImpl()  {
        try {
            service = DriveConnection.getService();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public Storage initStorage(String path) throws IOException {
        List<String> folders = Arrays.asList(path.split("/"));

        File fileMetadata = new File();
        fileMetadata.setName(folders.get(0));
        fileMetadata.setMimeType("application/vnd.google-apps.folder");

        File file = service.files().create(fileMetadata)
                .setFields("id")
                .execute();

        for(int i = 1; i <= folders.size() - 1; i++){
            fileMetadata = new File();
            fileMetadata.setName(folders.get(i));
            fileMetadata.setMimeType("application/vnd.google-apps.folder");
            fileMetadata.setParents(Collections.singletonList(file.getId()));
            file = service.files().create(fileMetadata).setFields("id, parents, name").execute();

        }

        fileMetadata = new File();
        fileMetadata.setName("users.json");
        fileMetadata.setParents(Collections.singletonList(file.getId()));
        service.files().create(fileMetadata).setFields("id, parents").execute();

        fileMetadata = new File();
        fileMetadata.setName("konfig.json");
        fileMetadata.setParents(Collections.singletonList(file.getId()));
        service.files().create(fileMetadata).setFields("id, parents").execute();

        Storage storage = new Storage(file.getId());
        storage.setJsonPath(".\\UsGdriveImpl\\src\\main\\resources"+folders.toString()+"Users.json");
        storage.setKofnigPath(".\\UsGdriveImpl\\src\\main\\resources"+folders.toString()+"konfig.json");
        return  storage;

    }

    public String createPath(String path) throws IOException {
        List<String> folders = Arrays.asList(path.split("/"));
        File fileMetadata = new File();
        fileMetadata.setName(folders.get(0));
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        fileMetadata.setParents(Collections.singletonList(storage.getStoragePath()));
        File file = service.files().create(fileMetadata)
                .setFields("id")
                .execute();
        for(int i = 1; i <= folders.size() - 1; i++){
            fileMetadata = new File();
            fileMetadata.setName(folders.get(i));
            fileMetadata.setMimeType("application/vnd.google-apps.folder");
            fileMetadata.setParents(Collections.singletonList(file.getId()));
            file = service.files().create(fileMetadata).setFields("id, parents, name").execute();

        }
        return file.getId();
    }
    public int vratiBrojDece(String id){
        ArrayList<File> fajlovi = new ArrayList<>();
        try {
            String queryChildren = "' in parents and visibility = 'limited' and trashed = false";
            String s = "'" + id + queryChildren;
            Drive.Files.List request = service.files().list().setQ(s);
            fajlovi = (ArrayList<File>) request.execute().getFiles();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        File file = null;
        try {
            file = service.files().get(id).execute();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return fajlovi.size();
    }
    @Override
    public boolean createFile(String name)  {
        String path = "";

        List<String>folder = Arrays.asList(name.split("/"));
        for(int i = 0; i <= folder.size() - 2; i++){
            path += folder.get(i);
            if (i == folder.size() - 2){                                //Izvlacimo putanju gde kreiramo fajl
                break;
            }
            path +="/";
        }
        String path2 = "/" + path;
        File fileMetadata = null;

        String result = vratiId(path);

        if(!name.contains("/")){
            result = storage.getStoragePath();
        }
        else if(result.equals("noPath")) {

            try {
                result = createPath(path);
            } catch (IOException exception) {
                exception.printStackTrace();
            }

        }

        int brojDece = vratiBrojDece(result);
        int foldersNumb= 0;
        List<String> ekstenzije = new ArrayList<>();
        for(NasFolder nasFolder:konfigStorage.getFolders()){
            if(folder.size() == 1){
                try {
                    path2 = "/"+  service.files().get(storage.getStoragePath()).execute().getName();

                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }

            if(nasFolder.getFolderId().equals(storage.getStoragePath()+path2)){        //Ucitati ogranicenja ako postoje
                foldersNumb = nasFolder.getFileNumber();
                ekstenzije = nasFolder.getZabranjeneEkstenzije();
                break;
            }
        }
        for (String eks : ekstenzije) {
            if (folder.get(folder.size() - 1).endsWith(eks)) {                                // Proveriti ekstenzije
                return false;
            }
        }
        if(brojDece + 1 > foldersNumb && foldersNumb != 0) {                                //Proveriti broj foldera
            return false;
        }
        fileMetadata = new File();
        fileMetadata.setName(folder.get(folder.size() - 1));                               //Kreiraj na postojecoj putanji
        fileMetadata.setParents(Collections.singletonList(result));
        try {
            service.files().create(fileMetadata).setFields("id, parents").execute();
        } catch (IOException exception) {
            exception.printStackTrace();
        }


        return true;
    }

    @Override
    public boolean createDirectory(String name)  {
        String path = "";

        List<String>folder = Arrays.asList(name.split("/"));
        for(int i = 0; i <= folder.size() - 2; i++){
            path += folder.get(i);
            if (i == folder.size() - 2){                                //Izvlacimo putanju gde kreiramo fajl
                break;
            }
            path +="/";
        }
        String path2 = "/" + path;
        File fileMetadata = null;

        String result = vratiId(path);

        if(!name.contains("/")){
            result = storage.getStoragePath();
        }
        else if(result.equals("noPath")) {

            try {
                result = createPath(path);
            } catch (IOException exception) {
                exception.printStackTrace();
            }

        }

        int brojDece = vratiBrojDece(result);
        int foldersNumb= 0;

        for(NasFolder nasFolder:konfigStorage.getFolders()){
            if(folder.size() == 1){
                try {
                    path2 = "/"+  service.files().get(storage.getStoragePath()).execute().getName();

                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }

            if(nasFolder.getFolderId().equals(storage.getStoragePath()+path2)){        //Proveriti ogranicenja
                foldersNumb = nasFolder.getFileNumber();
                break;
            }
        }
        if(brojDece + 1 > foldersNumb && foldersNumb != 0) {
            return false;
        }
        fileMetadata = new File();
        fileMetadata.setName(folder.get(folder.size() - 1));                               //Kreiraj na postojecoj putanji
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        fileMetadata.setParents(Collections.singletonList(result));
        try {
            service.files().create(fileMetadata).setFields("id, parents, name").execute();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return true;                             //Kreiraj na postojecoj putanji
    }

    @Override
    public boolean moveFile(String filePath, String destinationPath) {
        List<String> folder = Arrays.asList(destinationPath.split("/"));
        String s2 = "/" + destinationPath;
        String fileId = "";
        String folderId = "";
        try {

            fileId = vratiId(filePath);
            folderId = vratiId(destinationPath);
        } catch (Exception exception) {

        }
        if (folderId.equals("noPath") || fileId.equals("noPath")) {
            return false;
        }
        int brojDece = vratiBrojDece(folderId);
        int foldersNumb= 0;
        List<String> ekstenzije = new ArrayList<>();
        for(NasFolder nasFolder:konfigStorage.getFolders()){
            try {
                String s =  service.files().get(storage.getStoragePath()).execute().getName();
                if(destinationPath.equals(s));
                s2 = "/"+  s;

            } catch (IOException exception) {
                exception.printStackTrace();
            }
            if(nasFolder.getFolderId().equals(storage.getStoragePath()+s2)){        //Ucitati ogranicenja ako postoje
                foldersNumb = nasFolder.getFileNumber();
                ekstenzije = nasFolder.getZabranjeneEkstenzije();
                break;
            }
        }
        System.out.println(ekstenzije);
        for (String eks : ekstenzije) {
            if (filePath.endsWith(eks)) {
                return false;
            }
        }
        if(brojDece + 1 > foldersNumb && foldersNumb != 0) {
            return false;
        }
        File file = null;
        try {
            file = service.files().get(fileId)
                    .setFields("parents")
                    .execute();
            StringBuilder previousParents = new StringBuilder();
            for (String parent : file.getParents()) {
                previousParents.append(parent);
                previousParents.append(',');
            }
            file = service.files().update(fileId, null)
                    .setAddParents(folderId)
                    .setRemoveParents(previousParents.toString())
                    .setFields("id, parents")
                    .execute();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean moveDirectory(String dirPath, String destinationPath) {

        if(moveFile(dirPath,destinationPath))return true;
        else return false;
    }

    @Override
    public boolean deleteFile(String deletePath) {
        try {
            String storage = service.files().get(getStorage().getStoragePath()).execute().getName();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        if(deletePath.equals("user.json") || deletePath.equals("konfig.json") || deletePath.equals(storage)){
            return false;
        }
        File file = null;
        try {
            file = service.files().get(getStRoot()).execute();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        String check = vratiId(deletePath);

        if(check.equals("noPath")){
            return false;
        }

        try {
            service.files().delete(check).execute();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean deleteDirectory(String name) {
        if(deleteFile(name))
            return true;
        return false;
    }

    public String vratiId(String path){
        try {
            if(service.files().get(storage.getStoragePath()).execute().getName().equals(path)){
                return storage.getStoragePath();
            }
        } catch (IOException exception) {

        }
        String result = "";
        try {
            int flag = 0;
            String queryChildren = "'" + storage.getStoragePath() + "' in parents and visibility = 'limited' and trashed = false";
            Drive.Files.List decaStorage = service.files().list().setQ(queryChildren);
            List<File> children =  decaStorage.execute().getFiles();
            List<String> putanja = Arrays.asList(path.split("/"));
            if(children.size() == 0) {
                return "noPath";
            }

            for (String s : putanja) {
                flag = 0 ;
                for(File dete:children){
                    if(dete.getName().equals(s)){
                        flag = 1;
                        result = dete.getId();
                        queryChildren = "'" + dete.getId() + "' in parents and visibility = 'limited' and trashed = false";
                        decaStorage = service.files().list().setQ(queryChildren);
                        children =  decaStorage.execute().getFiles();
                        break;
                    }
                }
                if(flag == 0) {
                    return "noPath";
                }
            }
        }
        catch (Exception e){

        }
        return result;
    }

    @Override
    public List listFilesFromDirectory(String directory) {
        List<File>files = new ArrayList<>() ;
        String id = vratiId(directory);
        File file2 = null;
        try {
            file2 = service.files().get(id).execute();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        if(!id.equals("noPath") || !file2.getMimeType().equals("application/vnd.google-apps.folder")) {
            String queryChildren = "' in parents and visibility = 'limited' and trashed = false";
            try {
                String s = "'" + id + queryChildren;
                Drive.Files.List request = service.files().list().setQ(s);
                ArrayList<File> fajlovi = (ArrayList<File>) request.execute().getFiles();
                for(File file:fajlovi){
                    if(!file.getMimeType().endsWith(".folder")){
                        System.out.println(file.getName());
                    }
                }
                return files;
            }
            catch (Exception e){

            }
        }
        return  null;
    }

    @Override
    public List listDirsFromDirectory(String directory) {
        List<File>files = new ArrayList<>() ;
        String id = vratiId(directory);
        File file2 = null;
        try {
            file2 = service.files().get(id).execute();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        if(!id.equals("noPath") || !file2.getMimeType().equals("application/vnd.google-apps.folder")) {
            String queryChildren = "' in parents and visibility = 'limited' and trashed = false";
            try {
                String s = "'" + id + queryChildren;
                Drive.Files.List request = service.files().list().setQ(s);
                ArrayList<File> fajlovi = (ArrayList<File>) request.execute().getFiles();
                for(File file:fajlovi){
                    if(file.getMimeType().endsWith(".folder")){
                        System.out.println(file.getName());
                    }
                }
                return files;
            }
            catch (Exception e){

            }
        }
        return  null;
    }

    @Override
    public List listFilesByNameInStorage(String path) {
        // Drive.Children.List request = service.children().list(storage.getStoragePath());
        return  null;
    }

    @Override
    public List listFilesByExtension(String path, String extenstion) {
        return null;
    }

    @Override
    public List sortFilesByName(String name) {
        return null;
    }

    @Override
    public boolean download(String path) {
        String id = vratiId(path);
        File file;
        OutputStream outputStream = null;
        try {

            if(!id.equals("false")) {
                file = service.files().get(id).execute();
                outputStream = new FileOutputStream(".\\Download"+file.getName());
                service.files().get(id).executeMediaAndDownloadTo(outputStream);
                outputStream.flush();
                outputStream.close();
                return true;
            }
        }
        catch (Exception e){
            try {
                System.out.println("exception");
                service.files().get(id).executeAndDownloadTo(outputStream);
                outputStream.flush();
                outputStream.close();
                return true;
            }
            catch (Exception f){
                f.printStackTrace();
            }
        }
        return false;
    }

    public boolean checkJson(File file) throws IOException {
        String queryChildren = "' in parents and visibility = 'limited' and trashed = false";
        String s = "'" + file.getId() + queryChildren;
        Drive.Files.List request = service.files().list().setQ(s);
        ArrayList<File>children = (ArrayList<File>) request.execute().getFiles();
        for(File f:children){
            if(f.getName().equals("users.json")){
                return true;
            }
        }
        return false;
    }

    @Override
    public String checkStorage(String path) throws IOException {
        int flag = 0;
        String queryParent = "mimeType='application/vnd.google-apps.folder' and name='";
        String queryChildren = "' in parents and visibility = 'limited' and trashed = false";
        List<String>putanja = Arrays.asList(path.split("/"));  //Svi direktorijumi iz putanje
        String s = queryParent+putanja.get(0)+"'";
        Drive.Files.List request = service.files().list().setQ(s);
        ArrayList<File>fajlovi = (ArrayList<File>) request.execute().getFiles(); //Prvi direktorijum u putanji
        if(fajlovi.size() == 0){                  //Ukoliko ne postoji prvi direktorijum u putanju vrati noPath
            return "noPath";
        }
        if(putanja.size() == 1 ){
            boolean v = checkJson(fajlovi.get(0));
            if(v){
                return ".\\UsGdriveImpl\\src\\main\\resources" + putanja.toString()+"Users.json";   //Ukoliko prvi direktorijum ima skladiste vrati putanju
            }
            else {
                return "noJson" + fajlovi.get(0).getId();
            }//Ukoliko prvi direktorijum nema skladiste vrati noJson
        }
        for(File file:fajlovi){
            File file2 = file;
            ArrayList<File>children;
            for(int i = 1; i <= putanja.size() - 1;i++) {
                s = "'" + file2.getId() + queryChildren;
                request = service.files().list().setQ(s);
                children = (ArrayList<File>) request.execute().getFiles();// Deca direktorijuma
                for(File f:children){
                    flag = 0;
                    if(f.getName().equals(putanja.get(i))){
                        file2 = f;
                        flag = 1;
                        break;
                    }
                }
                if(flag == 0){
                    return "noPath";
                }
            }
            if(flag == 1) {
                boolean existJson = checkJson(file2);
                if(existJson)
                    return ".\\UsGdriveImpl\\src\\main\\resources" +putanja.toString()+"Users.json";
                else return "noJson" + file2.getId();
            }
        }
        return "noPath";
    }
}
