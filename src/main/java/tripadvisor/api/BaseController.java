package tripadvisor.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tripadvisor.model.Hotel;
import org.restexpress.Request;
import org.restexpress.Response;

import java.util.List;

/**
 * Created by vishnu on 7/3/15.
 */
public class BaseController {
    private static final Logger LOG = LoggerFactory.getLogger(BaseController.class);

    public void index(Request request, Response response) {
        String content="Trip Advisor Recommendation Service \n"
                +"Usage \n\n"
                +"http://localhost:9009/suggestion/get?query=Seattle&username=JJTrouble \n"
                +"http://localhost:9009/suggestion/get?query=Seattle&username= \n"
                +"http://localhost:9009/suggestion/get?query=Seattle \n";
        response.setContentType("text/plain");
        response.setBody(content);
    }

    public void getSuggestions(Request request, Response response) {

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
        } catch (Exception e) {
            LOG.debug(e.getMessage());
        }
        jsonObject.addProperty("username", username);
        jsonObject.addProperty("query", query);
        response.setContentType("application/json");
        response.setBody(jsonObject);
    }
}
