package airline.User;
import airline.utils.User.*;

import java.io.Serial;
import java.io.Serializable;

public class Passenger extends User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Cart cart = new Cart();
    public Passenger(String username, String password) {
        super(username, password, UserType.PASSENGER);
    }

    public Cart getCart() {
        return cart;
    }
}
