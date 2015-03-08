package tripadvisor.api;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.restexpress.RestExpress;

/**
 * Created by vishnu on 7/3/15.
 */
public class BaseRoute {
    public static void define(RestExpress server) {
        server.uri("/test", new BaseController()).action("testing", HttpMethod.GET).noSerialization();
        server.uri("/suggestion/get", new BaseController()).action("getSuggestions", HttpMethod.GET).noSerialization();
    }
}
