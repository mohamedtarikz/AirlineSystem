package airline.utils.User;

import java.io.Serial;
import java.io.Serializable;

public abstract class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    protected String username;
    protected String password;
    protected final UserType role;

    public User(String username, String password, UserType role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getRole() {
        return role;
    }

}
