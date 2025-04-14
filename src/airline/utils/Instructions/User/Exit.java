package airline.utils.Instructions.User;

import airline.utils.IOSystem.UserOptions;
import airline.utils.User.User;

public class Exit extends UserInstruction {
    private final User user;
    public Exit(User user) {
        super(UserOptions.EXIT);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
