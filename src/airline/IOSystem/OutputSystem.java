package airline.IOSystem;

import airline.Flight.Flight;
import airline.Flight.FlightSystem;
import airline.User.Passenger;
import airline.utils.Flight.FlightType;
import airline.utils.User.UserType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Comparator;

/**
 * Utility class responsible for displaying system-related output
 * - Provides help messages for different user roles
 * - Sends flight and cart data to the client
 */
public abstract class OutputSystem {

    /**
     * Displays the help menu for passengers.
     * Shows available commands and their required formats.
     */
    private static void PassengerHelp() {
        System.out.println("""
                ALL MESSAGES SHOULD BE IN THIS FORMAT: <command>|<argument>|<argument>|... WHERE '|' IS A DELIMITER,
                ALL COMMANDS ARE IN LOWER CASE!

                IF STUCK AT ANY POINT, TYPE 'help' TO DISPLAY AVAILABLE COMMANDS
                """);
        System.out.println("help: Displays this message\n");
        System.out.println("all-flights: Displays all available flights");
        System.out.println("cart: Displays all flights in cart\n");
        System.out.println("book|<flight_id>|<ticket_count>: Adds flight to cart");
        System.out.println("edit|<flight_id>|<new_ticket_count>: Changes number of tickets for a specific flight");
        System.out.println("cancel|<flight_id>: Removes flight from cart\n");
        System.out.println("exit: Exits the program\n");
    }

    /**
     * Displays the help menu for admins.
     * Shows available commands, expected input formats, and their descriptions.
     */
    private static void AdminHelp() {
        System.out.println("""
                ALL MESSAGES SHOULD BE IN THIS FORMAT: <command>|<argument>|<argument>|... WHERE '|' IS A DELIMITER,
                ALL COMMANDS ARE IN LOWER CASE!
                ALL (DATES, TIMES) SHOULD BE IN THE FORMAT: DD-MM-YYYY, HH:MM
                ALL CITIES SHOULD BE IN THE FORMAT: CITY, COUNTRY

                IF STUCK AT ANY POINT, TYPE 'help' TO DISPLAY AVAILABLE COMMANDS
                """);
        System.out.println("help: Displays this message\n");
        System.out.println("all-flights: Displays all available flights\n");
        System.out.println("add|<source_city>|<destination_city>|<departure_date_time>|<price>|<seats>: Adds a new flight");
        System.out.println("modify|<flight_id>|<new_departure_date_time>: Modifies the departure time of a specific flight");
        System.out.println("delete|<flight_id>: Deletes a specific flight\n");
        System.out.println("exit: Exits the program\n");
    }

    /**
     * Displays the help menu for users who are not logged in.
     */
    private static void UserHelp() {
        System.out.println("""
                ALL MESSAGES SHOULD BE IN THIS FORMAT: <command>|<argument>|<argument>|... WHERE '|' IS A DELIMITER,
                ALL COMMANDS ARE IN LOWER CASE!
                IF STUCK AT ANY POINT, TYPE 'help' TO DISPLAY AVAILABLE COMMANDS
                """);
        System.out.println("help: Displays this message\n");
        System.out.println("sign-up|<username>|<password>: Creates a new account");
        System.out.println("sign-in|<username>|<password>: Logs into an existing account\n");
        System.out.println("exit: Exits the program\n");
    }

    /**
     * Displays the appropriate help menu based on user type.
     *
     * @param userType The role of the user (PASSENGER, ADMIN, or null for guests)
     */
    public static void Help(UserType userType) {
        try {
            switch (userType) {
                case PASSENGER -> PassengerHelp();
                case ADMIN -> AdminHelp();
            }
        } catch (NullPointerException e) {
            UserHelp(); // If userType is null, display the default help menu
        }
    }

    /**
     * Sends a list of available flights to the client.
     *
     * @param flightSystem The system containing all flight data.
     * @param out          The output stream to send flight information to the client.
     * @throws IOException If an error occurs while writing to the output stream.
     */
    public static void DisplayFlights(FlightSystem flightSystem, ObjectOutputStream out) throws IOException {
        out.writeObject("All available flights:");
        out.writeObject("====================DOMESTIC====================");

        // Retrieve and send all domestic flights sorted by ID
        flightSystem.getFlights(FlightType.DOMESTIC)
                .stream()
                .sorted(Comparator.comparing(Flight::getId))
                .forEach(flight -> {
                    try {
                        out.reset(); // Prevents Java from caching the same object reference
                        out.writeObject(flight);
                        out.flush();
                    } catch (IOException e) {
                        throw new RuntimeException("Error sending flight data: " + e.getMessage(), e);
                    }
                });

        out.writeObject("====================INTERNATIONAL====================");

        // Retrieve and send all international flights sorted by ID
        flightSystem.getFlights(FlightType.INTERNATIONAL)
                .stream()
                .sorted(Comparator.comparing(Flight::getId))
                .forEach(flight -> {
                    try {
                        out.reset(); // Prevents Java from caching the same object reference
                        out.writeObject(flight);
                        out.flush();
                    } catch (IOException e) {
                        throw new RuntimeException("Error sending flight data: " + e.getMessage(), e);
                    }
                });
    }

    /**
     * Sends the contents of a passenger's cart to the client.
     *
     * @param out       The output stream to send cart details to the client.
     * @param passenger The passenger whose cart should be displayed.
     * @throws IOException If an error occurs while writing to the output stream.
     */
    public static void DisplayCart(ObjectOutputStream out, Passenger passenger) throws IOException {
        out.writeObject("============YOUR CART============");
        out.writeObject(passenger.getCart());
    }
}
