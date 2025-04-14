package airline.utils.Location;

import java.io.Serial;
import java.io.Serializable;

public record Location(String city, String country) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return city + ", " + country;
    }
}