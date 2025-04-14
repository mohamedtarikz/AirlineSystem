package airline.utils.Flight;

import java.time.LocalDateTime;

public interface AdminServices {
    void addFlight(FlightSpecs flightSpecs);
    boolean removeFlight(String id);
    boolean modifyFlight(String id, LocalDateTime newDepartureTime);
}
