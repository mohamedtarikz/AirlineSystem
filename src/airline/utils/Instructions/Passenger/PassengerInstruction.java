package airline.utils.Instructions.Passenger;
import airline.User.Passenger;
import airline.utils.IOSystem.PassengerOptions;
import airline.utils.Instructions.Instruction;

public class PassengerInstruction extends Instruction {

    protected Passenger passenger;
    public PassengerInstruction(PassengerOptions option, Passenger passenger) {
        super(option);
        this.passenger = passenger;
    }

    public Passenger getPassenger() {
        return passenger;
    }
}
