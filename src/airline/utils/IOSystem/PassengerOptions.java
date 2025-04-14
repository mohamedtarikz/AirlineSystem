package airline.utils.IOSystem;

import java.io.Serial;
import java.util.Optional;

public enum PassengerOptions implements Option {
    BOOK_FLIGHT("book"),
    MODIFY_BOOKING("edit"),
    VIEW_CART("cart"),
    CANCEL_FLIGHT("cancel");

    private final String option;
    @Serial
    private static final long serialVersionUID = 1L;
    PassengerOptions(String option) {
        this.option = option;
    }

    public String getOption() {
        return option;
    }

    public static PassengerOptions getOption(String option) {
        for(PassengerOptions passengerOption : PassengerOptions.values()) {
            if(passengerOption.getOption().equals(option)) {
                return passengerOption;
            }
        }
        return null;
    }
}
