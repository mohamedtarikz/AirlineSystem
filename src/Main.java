import airline.IOSystem.InputParser;
import airline.User.Passenger;
import airline.IOSystem.*;
import airline.utils.Instructions.*;
import airline.utils.IOSystem.*;

import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.Scanner;

/**
 * The entry point for the 'Console' Airport Management System.
 * - Handles user input
 * - Parses commands
 * - Communicates with the server via `AirportClient`
 */
public class Main {
    public static void main(String[] args) {
        // Display welcome message and basic instructions
        System.out.println("Welcome to the 'Console' Airport Management System!");
        System.out.println("WRITE 'help' TO SEE THE LIST OF COMMANDS");

        // Create a client instance to connect to the server
        AirportClient client = new AirportClient();
        Scanner scanner = new Scanner(System.in); // Scanner for reading user input
        String input; // Stores user input

        while (true) {
            System.out.println("\nAwaiting input...");
            input = scanner.nextLine(); // Read user input

            Optional<Instruction> optionalInstruction;
            try {
                // Parse user input into an Instruction object
                optionalInstruction = InputParser.parseInput(input, client.getUser());
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage()); // Handle invalid argument errors
                continue;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use 'dd-MM-yyyy, HH:mm'"); // Handle invalid date format
                continue;
            }

            // If the parsed instruction is valid, process it
            if (optionalInstruction.isPresent()) {
                Instruction instruction = optionalInstruction.orElseThrow(); // Get the instruction safely

                switch (instruction.getInstruction()) {
                    case UserOptions.HELP:
                        // Show help menu based on user role
                        if (client.getUser() != null) {
                            OutputSystem.Help(client.getUser().getRole());
                        } else {
                            OutputSystem.Help(null);
                        }
                        break;

                    case UserOptions.EXIT:
                        System.out.println("Exiting the system..."); // Exit the system
                        System.exit(0);
                        break;

                    default:
                        // Send the parsed instruction to the server
                        client.sendMessage(instruction);
                }
            } else {
                System.out.println("Invalid command. Please try again."); // Handle unrecognized commands
            }

            try {
                Thread.sleep(100); // Slight delay to prevent rapid looping
            } catch (InterruptedException ignored) {}
        }
    }
}
