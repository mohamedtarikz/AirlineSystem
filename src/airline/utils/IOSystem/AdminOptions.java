package airline.utils.IOSystem;

import java.io.Serial;
import java.util.Optional;

public enum AdminOptions implements Option {
    ADD_FLIGHT("add"),
    REMOVE_FLIGHT("delete"),
    MODIFY_FLIGHT("modify");

    private final String option;
    @Serial
    private static final long serialVersionUID = 1L;

    AdminOptions(String option) {
        this.option = option;
    }

    public String getOption() {
        return option;
    }

    public static AdminOptions getOption(String option) {
        for(AdminOptions adminOption : AdminOptions.values()) {
            if(adminOption.getOption().equals(option)) {
                return adminOption;
            }
        }
        return null;
    }
}
