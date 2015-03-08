package api;

import com.google.gson.JsonObject;
import org.restexpress.Request;
import org.restexpress.Response;

/**
 * Created by vishnu on 7/3/15.
 */
public class BaseController {
    public void testing(Request request, Response response){
        response.setBody("testing");
    }
    public void getSuggestions(Request request, Response response){
        String username = request.getQueryStringMap().get("username");
        String query = request.getQueryStringMap().get("query");
        JsonObject jsonObject =new JsonObject();
        
        
        response.setContentType("application/json");
        response.setBody(jsonObject);
    }
}
