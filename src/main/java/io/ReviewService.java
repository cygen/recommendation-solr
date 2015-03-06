package io;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import java.net.MalformedURLException;

/**
 * Created by vishnu on 6/3/15.
 */
public class ReviewService {
    static HttpSolrServer REVIEW_CONNECTOR = new HttpSolrServer("http://localhost:8983/solr/recReview");

    public static void main(String[] args) throws MalformedURLException, SolrServerException {
        String QUERY="seattle"; //query to search
        SolrQuery query = new SolrQuery();
        
        query.set("defType", "dismax"); //setting dismax query parser
        
        query.set("q.alt", "(ReviewContent:*"+QUERY+"*)"); //setting parameter to search on title and Tags and give more boost to title wise results

        QueryResponse response = REVIEW_CONNECTOR.query(query);

        SolrDocumentList results = response.getResults();

        for (int i = 0; i < results.size(); ++i) {
            System.out.println(results.get(i));
        }
    }
}
