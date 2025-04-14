package airline.IOSystem;

import airline.User.*;
import airline.utils.Flight.FlightSpecs;
import airline.utils.IOSystem.AdminOptions;
import airline.utils.IOSystem.PassengerOptions;
import airline.utils.IOSystem.UserOptions;
import airline.utils.Instructions.Instruction;
import airline.utils.Instructions.Admin.*;
import airline.utils.Instructions.Passenger.*;
import airline.utils.Instructions.User.*;
import airline.utils.Location.LocationParser;
import airline.utils.User.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

/**
 * Parses user input and converts it into corresponding instruction objects
 * for passengers, admins, or generic users.
 */
public abstract class InputParser {

    /**
     * Parses input for a passenger and returns a corresponding instruction object.
     *
     * @param inputArray The parsed input split into an array of command arguments.
     * @param passenger  The passenger executing the command.
     * @return PassengerInstruction The specific instruction for the passenger.
     */
    private static PassengerInstruction parseForPassenger(String[] inputArray, Passenger passenger) {
        PassengerOptions option = PassengerOptions.getOption(inputArray[0].toLowerCase());

        return switch (option) {
            case BOOK_FLIGHT -> {
                if (inputArray.length != 3) {
                    throw new IllegalArgumentException("Invalid number of arguments");
                }
                yield new BookFlight(inputArray[1], Integer.parseInt(inputArray[2]), passenger);
            }
            case MODIFY_BOOKING -> {
                if (inputArray.length != 3) {
                    throw new IllegalArgumentException("Invalid number of arguments");
                }
                yield new ModifyBooking(inputArray[1], Integer.parseInt(inputArray[2]), passenger);
            }
            case CANCEL_FLIGHT -> {
                if (inputArray.length != 2) {
                    throw new IllegalArgumentException("Invalid number of arguments");
                }
                yield new CancelFlight(inputArray[1], passenger);
            }
            case VIEW_CART -> {
                if (inputArray.length != 1) {
                    throw new IllegalArgumentException("Invalid number of arguments");
                }
                yield new ViewCart(passenger);
            }
        };
    }

    /**
     * Parses input for an admin and returns a corresponding instruction object.
     *
     * @param inputArray The parsed input split into an array of command arguments.
     * @param admin      The admin executing the command.
     * @return AdminInstruction The specific instruction for the admin.
     */
    private static AdminInstruction parseForAdmin(String[] inputArray, Admin admin) {
        AdminOptions option = AdminOptions.getOption(inputArray[0].toLowerCase());

        return switch (option) {
            case ADD_FLIGHT -> {
                if (inputArray.length != 6) {
                    throw new IllegalArgumentException("Invalid number of arguments");
                }
                yield new AddFlight(new FlightSpecs(
                        LocationParser.parseLocation(inputArray[1]),   // Departure location
                        LocationParser.parseLocation(inputArray[2]),   // Destination location
                        LocalDateTime.parse(inputArray[3],
                                DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm")), // Flight time
                        Integer.parseInt(inputArray[5]),   // Number of seats
                        Double.parseDouble(inputArray[4])  // Ticket price
                ), admin);
            }
            case REMOVE_FLIGHT -> {
                if (inputArray.length != 2) {
                    throw new IllegalArgumentException("Invalid number of arguments");
                }
                yield new RemoveFlight(inputArray[1], admin);
            }
            case MODIFY_FLIGHT -> {
                if (inputArray.length != 3) {
                    throw new IllegalArgumentException("Invalid number of arguments");
                }
                yield new ModifyFlight(
                        inputArray[1],   // Flight ID
                        LocalDateTime.parse(inputArray[2],
                                DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm")), // New flight time
                        admin
                );
            }
        };
    }

    /**
     * Parses input for a generic user (before sign-in) and returns a corresponding instruction object.
     *
     * @param inputArray The parsed input split into an array of command arguments.
     * @param user       The user executing the command.
     * @return Instruction The specific instruction for the user.
     */
    private static Instruction parseForUser(String[] inputArray, User user) {
        UserOptions options = UserOptions.getOption(inputArray[0].toLowerCase());

        return switch (options) {
            case SIGN_UP -> {
                if (inputArray.length != 3) {
                    throw new IllegalArgumentException("Invalid number of arguments");
                }
                yield new SignUp(inputArray[1], inputArray[2]);
            }
            case SIGN_IN -> {
                if (inputArray.length != 3) {
                    throw new IllegalArgumentException("Invalid number of arguments");
                }
                yield new SignIn(inputArray[1], inputArray[2]);
            }
            case VIEW_FLIGHTS -> {
                if (inputArray.length != 1) {
                    throw new IllegalArgumentException("Invalid number of arguments");
                }
                yield new ViewFlights();
            }
            case EXIT -> {
                if (inputArray.length != 1) {
                    throw new IllegalArgumentException("Invalid number of arguments");
                }
                yield new Exit(user);
            }
            case HELP -> {
                if (inputArray.length != 1) {
                    throw new IllegalArgumentException("Invalid number of arguments");
                }
                yield new Help();
            }
        };
    }

    /**
     * Parses a raw user input string into an appropriate instruction object.
     *
     * @param input The raw user input as a string.
     * @param user  The user executing the command (can be null if not signed in).
     * @return An Optional containing the parsed instruction, or empty if parsing fails.
     */
    public static Optional<Instruction> parseInput(String input, User user) {
        // Split input by "|" and trim whitespace from each part
        String[] inputArray = input.split("\\|");
        inputArray = Arrays.stream(inputArray).map(String::trim).toArray(String[]::new);

        if (user != null) {
            switch (user.getRole()) {
                case UserType.PASSENGER:
                    try {
                        return Optional.of(parseForPassenger(inputArray, (Passenger) user));
                    } catch (NullPointerException ignored) {}
                    break;
                case UserType.ADMIN:
                    try {
                        return Optional.of(parseForAdmin(inputArray, (Admin) user));
                    } catch (NullPointerException ignored) {}
                    break;
            }
        }

        try {
            return Optional.of(parseForUser(inputArray, user));
        } catch (NullPointerException ignored) {}

        return Optional.empty();
    }
}
