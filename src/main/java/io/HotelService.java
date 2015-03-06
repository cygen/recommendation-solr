package io;

import org.apache.solr.client.solrj.impl.HttpSolrServer;

/**
 * Created by vishnu on 6/3/15.
 */
public class HotelService {
    static HttpSolrServer HOTEL_CONNECTOR = new HttpSolrServer("http://localhost:8983/solr/recHotel");

}
