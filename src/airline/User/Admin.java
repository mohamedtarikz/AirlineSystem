package airline.User;
import airline.utils.User.*;

import java.io.Serial;
import java.io.Serializable;

public class Admin extends User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public Admin(String username, String password) {
        super(username, password, UserType.ADMIN);
    }

}
