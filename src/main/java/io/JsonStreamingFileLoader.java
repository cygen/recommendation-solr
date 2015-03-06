package io;

import com.google.gson.stream.JsonReader;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by vishnu on 6/3/15.
 */
public class JsonStreamingFileLoader {
    public static boolean readFromFile(String filePath){
        try {
            JsonReader reader = new JsonReader(new FileReader(filePath));
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("age")) {
                } else if (name.equals("Reviews")) {
                    // read array
                    reader.beginArray();
                    while (reader.hasNext()) {
                        reader.beginObject();
                        while (reader.hasNext()) {
                            String reviewName=reader.nextName();
                            System.out.println(reviewName);
                        }
                    }
                    reader.endArray();
                } else {
                    reader.skipValue(); //avoid some unhandle events
                }
            }
            reader.endObject();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }        
}
