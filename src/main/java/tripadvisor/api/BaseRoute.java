package tripadvisor.api;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.restexpress.RestExpress;

/**
 * Created by vishnu on 7/3/15.
 */
public class BaseRoute {
    public static void define(RestExpress server) {
        //Routes for the Api Service
        server.uri("/suggestion/get", new BaseController()).action("getSuggestions", HttpMethod.GET).noSerialization();
        server.uri("/", new BaseController()).action("index", HttpMethod.GET).noSerialization();
    }
}
