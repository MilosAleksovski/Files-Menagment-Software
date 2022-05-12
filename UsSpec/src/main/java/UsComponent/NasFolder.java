package UsComponent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NasFolder {
    private String folderId;
    private double storageSize;
    private List<String> zabranjeneEkstenzije = new ArrayList<>();
    private int fileNumber;


    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public void setStorageSize(double folderSize) {
        this.storageSize = folderSize;
    }
    public void dodajEkstenziju(String ext){
        zabranjeneEkstenzije.add(ext);
    }

    public void setFileNumber(int fileNumber) {
        this.fileNumber = fileNumber;
    }

    public String getFolderId() {
        return folderId;
    }

    public int getFileNumber() {
        return fileNumber;
    }

    public double getStorageSize() {
        return storageSize;
    }

    public List<String> getZabranjeneEkstenzije() {
        return zabranjeneEkstenzije;
    }
}
