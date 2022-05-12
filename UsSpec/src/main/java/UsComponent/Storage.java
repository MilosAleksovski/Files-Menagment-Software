package UsComponent;

import java.util.ArrayList;
import java.util.List;

public class Storage {
    private String storagePath;
    private User currentUser;
    private String jsonPath;
    private String kofnigPath;
    private List<User> users;

    public Storage(String stRoot) {
        users=new ArrayList<User>();
        this.storagePath = stRoot;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void addUsers(User user) {
        users.add(user);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public void setKofnigPath(String kofnigPath) {
        this.kofnigPath = kofnigPath;
    }

    public String getKofnigPath() {
        return kofnigPath;
    }
}
