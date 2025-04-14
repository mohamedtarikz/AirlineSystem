package airline.utils.Instructions.User;

import airline.utils.IOSystem.UserOptions;

public class SignIn extends UserInstruction {
    private final String username;
    private final String password;

    public SignIn(String username, String password) {
        super(UserOptions.SIGN_IN);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
