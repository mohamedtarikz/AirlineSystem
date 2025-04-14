package airline.utils.Location;

public class LocationParser {

    public static Location parseLocation(String location) {
        String[] parts = location.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid location format");
        }
        return new Location(parts[0].trim(), parts[1].trim());
    }

}
