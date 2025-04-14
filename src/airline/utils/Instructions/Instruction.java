package airline.utils.Instructions;

import airline.utils.IOSystem.Option;

import java.io.Serial;
import java.io.Serializable;
public abstract class Instruction implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    protected final Option option;

    public Instruction(Option option) {
        this.option = option;
    }

    public Option getInstruction() {
        return option;
    }
}
