package api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.Hotel;
import org.restexpress.Request;
import org.restexpress.Response;

import java.util.List;

/**
 * Created by vishnu on 7/3/15.
 */
public class BaseController {
    public void testing(Request request, Response response){
        response.setBody("testing");
    }
    public void getSuggestions(Request request, Response response){

        JsonObject jsonObject = new JsonObject();
        String username = request.getQueryStringMap().get("username");
        String query = request.getQueryStringMap().get("query");
        try {
            if (username == null) username = null;
            if (query != null) {
                JsonArray hotelsArray = new JsonArray();
                List<Hotel> hotels = Hotel.getListOfHotelsFor(query);
                for (Hotel hotel : hotels) {
                    hotel.getRelatedUserReviews(username);
                    hotelsArray.add(hotel.toJson());
                }
                jsonObject.add("hotels", hotelsArray);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        jsonObject.addProperty("username",username);
        jsonObject.addProperty("query",query);
        response.setContentType("application/json");
        response.setBody(jsonObject);
    }
}
