package io;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by vishnu on 6/3/15.
 */
public class ReviewService {
    static HttpSolrServer REVIEW_CONNECTOR = new HttpSolrServer("http://localhost:8983/solr/recReview");
    public static HashMap<Long,Long> getRatingsAndHotelFor(String userName) throws SolrServerException {
        HashMap<Long,Long> resultsToReturn = new HashMap<Long, Long>();
        SolrQuery query = new SolrQuery();
        query.set("defType", "dismax"); //setting dismax query parser
        query.set("q.alt", "(AuthorName:"+userName+")"); //setting parameter to search on title and Tags and give more boost to title wise results
        query.setFields("HotelId","Overall");
        QueryResponse response = REVIEW_CONNECTOR.query(query);
        SolrDocumentList results = response.getResults();
        for (int i = 0; i < results.size(); ++i) {
            resultsToReturn.put((Long)results.get(i).get("HotelId"),(Long)results.get(i).get("Overall"));
        }
        return resultsToReturn;
    }

    public static ArrayList<String> getAuthorsOf(long HotelId) throws SolrServerException {
        ArrayList<String> resultsToReturn = new ArrayList<String>();
        SolrQuery query = new SolrQuery();
        query.set("defType", "dismax"); //setting dismax query parser
        query.set("q.alt", "(HotelId:"+HotelId+")"); //setting parameter to search on title and Tags and give more boost to title wise results
        query.setFields("AuthorName");
        QueryResponse response = REVIEW_CONNECTOR.query(query);
        SolrDocumentList results = response.getResults();
        for (int i = 0; i < results.size(); ++i) {
            resultsToReturn.add((String) results.get(i).get("AuthorName"));
        }
        return resultsToReturn;
    }
    public static void fillRelationScoreOfUser(String member,HashMap<Long,Long> userRatings, HashMap<Long,Long> memberRatings,HashMap<String,Long> finalRelScore){
        for (Map.Entry<Long, Long> entry : userRatings.entrySet()) {
            Long key = entry.getKey();
            Long value = entry.getValue();
            if(memberRatings.get(key)==value){
                if(finalRelScore.get(member)==null)
                    finalRelScore.put(member,1l);
                else
                    finalRelScore.put(member,finalRelScore.get(member));
            }
        }
    }
    public static ArrayList<String> getRelatedUsers(String userName,Long hotelId) throws SolrServerException {
        HashMap<Long, Long> userRatings = getRatingsAndHotelFor(userName);
        HashMap<String,Long> finalRelScore = new HashMap<String, Long>();
        for(String member:getAuthorsOf(hotelId)){
            fillRelationScoreOfUser(member,userRatings,getRatingsAndHotelFor(member),finalRelScore);
        }
        TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>();
        for (Map.Entry entry : finalRelScore.entrySet()) {
            sortedMap.put((String) entry.getValue(), (Integer)entry.getKey());
        }

        ArrayList<String> returnValue = new ArrayList<String>();
        returnValue.addAll(sortedMap.keySet());
        returnValue.remove(userName);
        return returnValue;
    }
    public static ArrayList<String> getRelatedUserReviews(String userName,Long hotelId) throws SolrServerException {
        ArrayList<String> resultsToReturn = new ArrayList<String>();
        for (String relatedName:getRelatedUsers(userName,hotelId)){
            SolrQuery query = new SolrQuery();
            query.set("defType", "dismax"); //setting dismax query parser
            query.set("q.alt", "(HotelId:"+hotelId+") AND (AuthorName:"+relatedName+")"); //setting parameter to search on title and Tags and give more boost to title wise results
            query.setFields("ReviewContent");
            QueryResponse response = REVIEW_CONNECTOR.query(query);
            SolrDocumentList results = response.getResults();
            for (int i = 0; i < 3 && i<results.size(); ++i) {
                resultsToReturn.add((String) results.get(i).get("ReviewContent"));
            }
        }

        if (resultsToReturn.size() < 3){
            SolrQuery query = new SolrQuery();
            query.set("defType", "dismax"); //setting dismax query parser
            query.set("q.alt", "(HotelId:"+hotelId+")"); //setting parameter to search on title and Tags and give more boost to title wise results
            query.setFields("ReviewContent");
            QueryResponse response = REVIEW_CONNECTOR.query(query);
            SolrDocumentList results = response.getResults();
            for (int i = 0; i < 3 && i<results.size(); ++i) {
                resultsToReturn.add((String) results.get(i).get("ReviewContent"));
            }
        }
        return resultsToReturn;
    }

    public static ArrayList<Long> getHotelIdsForKeyWord(String keyword) throws SolrServerException {
        ArrayList<Long> resultsToReturn = new ArrayList<Long>();
        SolrQuery query = new SolrQuery();
        query.set("defType", "dismax"); //setting dismax query parser
        query.set("q.alt", "(ReviewContent:*"+keyword+"*)");
        query.setFields("HotelId");
        QueryResponse response = REVIEW_CONNECTOR.query(query);
        SolrDocumentList results = response.getResults();
        for (int i = 0; i < results.size(); ++i) {
            resultsToReturn.add((Long) results.get(i).get("HotelId"));
        }
        return resultsToReturn;
    }

    public static HashMap<Long,ArrayList<String>>getSuggestionsFor(String userName,String keyword) throws SolrServerException {
        HashMap<Long,ArrayList<String>> resultToReturn = new HashMap<Long,ArrayList<String>>();
        for(Long hotelId:getHotelIdsForKeyWord(keyword)){
            resultToReturn.put(hotelId,getRelatedUserReviews(userName,hotelId));
        }
        return resultToReturn;
    }
    public static void main(String[] args) throws MalformedURLException, SolrServerException {
        String username="";
        String keyword="";
        // to get suggestions
        getSuggestionsFor(username,keyword); // username is AuthorName and keyword is a word on reviews

    }
}
