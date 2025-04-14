package airline.utils.Instructions.Passenger;
import airline.User.Passenger;
import airline.utils.IOSystem.PassengerOptions;

public class ViewCart extends PassengerInstruction {

    public ViewCart(Passenger passenger) {
        super(PassengerOptions.VIEW_CART, passenger);
    }

}
