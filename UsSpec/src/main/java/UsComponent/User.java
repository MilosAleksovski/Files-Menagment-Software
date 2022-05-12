package UsComponent;

public class User {
    private String username;
    private String password;
    private Privileges privilege;

    public User(String username, String password, Privileges privilege) {
        this.username = username;
        this.password = password;
        this.privilege = privilege;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", privilege=" + privilege +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public Privileges getPrivilege() {
        return privilege;
    }

    public String getPassword() {
        return password;
    }

}
