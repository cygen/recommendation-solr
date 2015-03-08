package tripadvisor;

import tripadvisor.io.JacksonJasonLoader;

import java.io.File;

/**
 * Created by vishnu on 6/3/15.
 */
public class TripAdvisorDataLoader {
    public static void main(String[] args) {
        System.out.println("Starting Application");
        String folderPath = "src/main/resources/sampledata/tripadvisor";
        if (args[0]!=null){
            if(!"".equals(args[0])){
                folderPath=args[0];
            }
        }
        
        System.out.println("Reading data from path "+folderPath);
        
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
