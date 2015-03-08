package tripadvisor.io;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by vishnu on 6/3/15.
 */
public class ReviewService {
    private static final Logger LOG = LoggerFactory.getLogger(ReviewService.class);
    
    static HttpSolrServer REVIEW_CONNECTOR = new HttpSolrServer("http://localhost:8983/solr/recReview");

    public static HashMap<Long, Double> getRatingsAndHotelFor(String userName) {
        HashMap<Long, Double> resultsToReturn = new HashMap<Long, Double>();
        try {
            SolrQuery query = new SolrQuery();
            query.set("defType", "dismax"); //setting dismax query parser
            query.set("q.alt", "(AuthorName:" + userName + ")"); //setting parameter to search on title and Tags and give more boost to title wise results
            query.setFields("HotelId", "Overall");
            QueryResponse response = REVIEW_CONNECTOR.query(query);
            SolrDocumentList results = response.getResults();
            for (int i = 0; i < results.size(); ++i) {
                resultsToReturn.put((Long) results.get(i).get("HotelId"), (Double) results.get(i).get("Overall"));
            }
        } catch (Exception e) {
            LOG.debug(e.getMessage());
        }
        return resultsToReturn;
    }

    public static LinkedHashSet<String> getAuthorsOf(long HotelId) {
        LinkedHashSet<String> resultsToReturn = new LinkedHashSet<String>();
        try {
            SolrQuery query = new SolrQuery();
            query.set("defType", "dismax"); //setting dismax query parser
            query.set("q.alt", "(HotelId:" + HotelId + ")"); //setting parameter to search on title and Tags and give more boost to title wise results
            query.setFields("AuthorName");
            QueryResponse response = REVIEW_CONNECTOR.query(query);
            SolrDocumentList results = response.getResults();
            for (int i = 0; i < results.size(); ++i) {
                resultsToReturn.add((String) results.get(i).get("AuthorName"));
            }
        } catch (Exception e) {
            LOG.debug(e.getMessage());
        }
        return resultsToReturn;
    }

    public static void fillRelationScoreOfUser(String member, HashMap<Long, Double> userRatings, HashMap<Long, Double> memberRatings, HashMap<String, Long> finalRelScore) {
        for (Map.Entry<Long, Double> entry : userRatings.entrySet()) {
            try {
                Long key = entry.getKey();
                Double value = entry.getValue();
                if (memberRatings.get(key) != null) {
                    if (memberRatings.get(key).equals(value)) {
                        if (finalRelScore.get(member) == null)
                            finalRelScore.put(member, 1l);
                        else
                            finalRelScore.put(member, finalRelScore.get(member));
                    }
                }

            } catch (Exception e) {
                LOG.debug(e.getMessage());
            }
        }
    }

    public static LinkedHashSet<String> getRelatedUsers(String userName, Long hotelId) throws SolrServerException {
        HashMap<Long, Double> userRatings = getRatingsAndHotelFor(userName);
        HashMap<String, Long> finalRelScore = new HashMap<String, Long>();
        for (String member : getAuthorsOf(hotelId)) {
            if (getRatingsAndHotelFor(member) != null)
                fillRelationScoreOfUser(member, userRatings, getRatingsAndHotelFor(member), finalRelScore);
        }
        TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>();
        try {
            for (Map.Entry entry : finalRelScore.entrySet()) {
                sortedMap.put((String) entry.getValue(), (Integer) entry.getKey());
            }
        } catch (Exception e) {
            LOG.debug(e.getMessage());
        }


        LinkedHashSet<String> returnValue = new LinkedHashSet<String>();
        returnValue.addAll(sortedMap.keySet());
        returnValue.remove(userName);
        return returnValue;
    }
}
