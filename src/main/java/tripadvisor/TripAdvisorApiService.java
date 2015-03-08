package tripadvisor;

import tripadvisor.api.BaseRoute;
import org.restexpress.RestExpress;
import org.restexpress.pipeline.SimpleConsoleLogMessageObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by vishnu on 7/3/15.
 */
public class TripAdvisorApiService {
    private static final String SERVICE_NAME = "TripAdvisor Recommendation Service";
    private static final int DEFAULT_EXECUTOR_THREAD_POOL_SIZE = 2;
    private static final int SERVER_PORT = 9009;
    private static final Logger LOG = LoggerFactory.getLogger(TripAdvisorApiService.class);

    public static void main(String[] args) {
        System.out.println("Initializing rest api Service");
        RestExpress server = null;
        try {
            server = initializeServer(args);
            server.awaitShutdown();
        } catch (IOException e) {
            LOG.info(e.getMessage());
        }
    }

    public static RestExpress initializeServer(String[] args) throws IOException {
        RestExpress server = new RestExpress()
                .setName(SERVICE_NAME)
                .setBaseUrl("http://localhost:" + SERVER_PORT)
                .setExecutorThreadCount(DEFAULT_EXECUTOR_THREAD_POOL_SIZE)
                .addMessageObserver(new SimpleConsoleLogMessageObserver());
        BaseRoute.define(server);
        server.bind(SERVER_PORT);
        return server;
    }
}
