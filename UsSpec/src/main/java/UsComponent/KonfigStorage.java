package UsComponent;

import java.util.ArrayList;
import java.util.List;

public class KonfigStorage {
    private List<NasFolder> folders = new ArrayList<>();
    private Double storageSize = 0.0;

    public void addFolder(NasFolder nasFolder){
        folders.add(nasFolder);
    }

    public List<NasFolder> getFolders() {
        return folders;
    }

    public void setStorageSize(Double storageSize) {
        this.storageSize = storageSize;
    }

    public Double getStorageSize() {
        return storageSize;
    }
}
