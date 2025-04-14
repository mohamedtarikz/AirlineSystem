package airline.utils.IOSystem;

import java.io.Serial;
import java.util.Optional;
public enum UserOptions implements Option {

    SIGN_UP("sign-up"),
    SIGN_IN("sign-in"),
    VIEW_FLIGHTS("all-flights"),
    EXIT("exit"),
    HELP("help");

    private final String option;
    @Serial
    private static final long serialVersionUID = 1L;
    UserOptions(String option) {
        this.option = option;
    }

    public String getOption() {
        return option;
    }

    public static UserOptions getOption(String option) {
        for(UserOptions userOption : UserOptions.values()) {
            if(userOption.getOption().equals(option)) {
                return userOption;
            }
        }
        return null;
    }
}
