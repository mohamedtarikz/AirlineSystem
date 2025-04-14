package airline.utils.Instructions.Admin;
import airline.User.Admin;
import airline.utils.IOSystem.AdminOptions;
import airline.utils.Instructions.Instruction;

public class AdminInstruction extends Instruction {
    protected Admin admin;
    public AdminInstruction(AdminOptions options, Admin admin) {
        super(options);
        this.admin = admin;
    }

    public Admin getAdmin() {
        return admin;
    }
}
