package airline.utils.User;

import airline.Flight.Flight;
import airline.Flight.FlightSystem;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Cart implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final HashMap<Flight, Integer> cart = new HashMap<>();
    private Double totalPrice = 0.0;

    public void bookFlight(Flight flight, int numTickets) {
        if (cart.keySet().stream().anyMatch(f -> f.getId().equals(flight.getId()))) {
            cart.keySet().stream()
                    .filter(f -> f.getId().equals(flight.getId()))
                    .findFirst()
                    .ifPresent(f -> totalPrice -= f.getFlightSpecs().getPrice() * cart.get(f));
        }
        cart.put(flight, numTickets);
        totalPrice += flight.getFlightSpecs().getPrice() * numTickets;
    }

    public void cancelFlight(String id) {
        totalPrice -= cart.entrySet().stream()
                .filter(entry -> entry.getKey().getId().equals(id))
                .mapToDouble(entry -> entry.getKey().getFlightSpecs().getPrice() * entry.getValue())
                .sum();
        cart.keySet().removeIf(flight -> flight.getId().equals(id));
    }

    public Integer getNumTickets(String id) {
        return cart.entrySet().stream()
                .filter(entry -> entry.getKey().getId().equals(id))
                .mapToInt(Map.Entry::getValue)
                .findFirst().orElse(0);
    }

    @Override
    public String toString() {
        StringBuilder cartDetails = new StringBuilder();
        cartDetails.append("Cart Details: \n");
        for(Flight flight : cart.keySet()) {
            cartDetails.append("Flight ID: ").append(flight.getId()).append(", Number of Tickets: ").append(cart.get(flight)).append(", Tickets' Price: ").append(cart.get(flight) * flight.getFlightSpecs().getPrice()).append("\n");
        }
        cartDetails.append("\nTotal Price: ").append(totalPrice);
        return cartDetails.toString();
    }

}
