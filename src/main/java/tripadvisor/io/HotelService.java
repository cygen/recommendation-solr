package tripadvisor.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tripadvisor.model.Hotel;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;

/**
 * Created by vishnu on 6/3/15.
 */
public class HotelService {
    private static final Logger LOG = LoggerFactory.getLogger(HotelService.class);
    
    static HttpSolrServer HOTEL_CONNECTOR = new HttpSolrServer("http://localhost:8983/solr/recHotel");

    public static Hotel getHotel(String id) {
        try {
            SolrQuery hotelQuery = new SolrQuery();
            hotelQuery.set("defType", "dismax"); //setting dismax query parser
            hotelQuery.set("q.alt", "(HotelId:" + id + ")");
            QueryResponse hotelResponse = HOTEL_CONNECTOR.query(hotelQuery);
            return new Hotel(hotelResponse.getResults().get(0));
        } catch (Exception e) {
            LOG.debug(e.getMessage());
            return null;
        }
    }
}
