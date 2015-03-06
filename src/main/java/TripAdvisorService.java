import io.JacksonJasonLoader;
import io.JsonStreamingFileLoader;

/**
 * Created by vishnu on 6/3/15.
 */
public class TripAdvisorService {
    public static void main(String[] args){
        System.out.println("Starting Application");

        JacksonJasonLoader.readFromFile("/home/vishnu/projects/rec-engine/src/main/resources/1001.json");
        JacksonJasonLoader.readFromFile("/home/vishnu/projects/rec-engine/src/main/resources/72572.json");
    }
}
