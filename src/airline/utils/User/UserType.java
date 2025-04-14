package airline.utils.User;

import java.io.Serial;
import java.io.Serializable;

public enum UserType implements Serializable {
    PASSENGER,
    ADMIN;

    @Serial
    private static final long serialVersionUID = 1L;
}
