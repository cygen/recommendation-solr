import io.JacksonJasonLoader;

import java.io.File;

/**
 * Created by vishnu on 6/3/15.
 */
public class TripAdvisorService {
    public static void main(String[] args) {
        System.out.println("Starting Application");
        String folderPath = "/home/vishnu/Documents/json";
        File folder = new File(folderPath);

        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            System.out.println("Reading File " + file.getName());
            JacksonJasonLoader.readFromFile(file.getAbsolutePath());
        }
        JacksonJasonLoader.commitDocuments();
        System.out.println("Data Loading has been Completed ....");
    }
}
