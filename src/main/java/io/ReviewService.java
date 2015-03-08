package io;

import model.Review;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by vishnu on 6/3/15.
 */
public class ReviewService {
    static HttpSolrServer REVIEW_CONNECTOR = new HttpSolrServer("http://localhost:8983/solr/recReview");
    public static HashMap<Long,Double> getRatingsAndHotelFor(String userName) throws SolrServerException {
        HashMap<Long,Double> resultsToReturn = new HashMap<Long, Double>();
        SolrQuery query = new SolrQuery();
        query.set("defType", "dismax"); //setting dismax query parser
        query.set("q.alt", "(AuthorName:"+userName+")"); //setting parameter to search on title and Tags and give more boost to title wise results
        query.setFields("HotelId","Overall");
        QueryResponse response = REVIEW_CONNECTOR.query(query);
        SolrDocumentList results = response.getResults();
        for (int i = 0; i < results.size(); ++i) {
            resultsToReturn.put((Long)results.get(i).get("HotelId"),(Double)results.get(i).get("Overall"));
        }
        return resultsToReturn;
    }

    public static LinkedHashSet<String> getAuthorsOf(long HotelId) throws SolrServerException {
        LinkedHashSet<String> resultsToReturn = new LinkedHashSet<String>();
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
    public static void fillRelationScoreOfUser(String member,HashMap<Long,Double> userRatings, HashMap<Long,Double> memberRatings,HashMap<String,Long> finalRelScore){
        for (Map.Entry<Long, Double> entry : userRatings.entrySet()) {
            try {
                Long key = entry.getKey();
                Double value = entry.getValue();
                if (memberRatings.get(key)!=null){
                    if (memberRatings.get(key).equals(value)) {
                        if (finalRelScore.get(member) == null)
                            finalRelScore.put(member, 1l);
                        else
                            finalRelScore.put(member, finalRelScore.get(member));
                    }
                }

            }catch (Exception e){e.printStackTrace();}
        }
    }
    public static LinkedHashSet<String> getRelatedUsers(String userName,Long hotelId) throws SolrServerException {
        HashMap<Long, Double> userRatings = getRatingsAndHotelFor(userName);
        HashMap<String,Long> finalRelScore = new HashMap<String, Long>();
        for(String member:getAuthorsOf(hotelId)){
            if(getRatingsAndHotelFor(member)!=null)
                fillRelationScoreOfUser(member,userRatings,getRatingsAndHotelFor(member),finalRelScore);
        }
        TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>();
        try{
            for (Map.Entry entry : finalRelScore.entrySet()) {
                sortedMap.put((String) entry.getValue(), (Integer)entry.getKey());
            }
        }catch (Exception e){}


        LinkedHashSet<String> returnValue = new LinkedHashSet<String>();
        returnValue.addAll(sortedMap.keySet());
        returnValue.remove(userName);
        return returnValue;
    }
    public static LinkedHashSet<String> getRelatedUserReviews(String userName,Long hotelId) throws SolrServerException {
        LinkedHashSet<String> resultsToReturn = new LinkedHashSet<String>();
        int i = 0;
        for (String relatedName:getRelatedUsers(userName,hotelId)){
            SolrQuery query = new SolrQuery();
            query.set("defType", "dismax"); //setting dismax query parser
            query.set("q.alt", "(HotelId:"+hotelId+") AND (AuthorName:"+relatedName+")"); //setting parameter to search on title and Tags and give more boost to title wise results
            query.setFields("ReviewContent");
            QueryResponse response = REVIEW_CONNECTOR.query(query);
            SolrDocumentList results = response.getResults();
            
            for (i=0; i < 3 && i<results.size(); ++i) {
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
            for (i = 0; i < 3 && i<results.size(); ++i) {
                resultsToReturn.add((String) results.get(i).get("ReviewContent"));
            }
        }
        return resultsToReturn;
    }

    public static LinkedHashSet<Long> getHotelIdsForKeyWord(String keyword) throws SolrServerException {
        LinkedHashSet<Long> resultsToReturn = new LinkedHashSet<Long>();
        SolrQuery query = new SolrQuery();
        query.set("defType", "dismax"); //setting dismax query parser
        query.set("q.alt", "(ReviewContent:*"+keyword+")");
        query.setFields("HotelId");
        QueryResponse response = REVIEW_CONNECTOR.query(query);
        SolrDocumentList results = response.getResults();
        for (int i = 0; i < results.size(); ++i) {
            resultsToReturn.add((Long) results.get(i).get("HotelId"));
        }
        return resultsToReturn;
    }

    public static HashMap<Long,LinkedHashSet<String>>getSuggestionsFor(String userName,String keyword) throws SolrServerException {
        HashMap<Long,LinkedHashSet<String>> resultToReturn = new HashMap<Long,LinkedHashSet<String>>();
        for(Long hotelId:getHotelIdsForKeyWord(keyword)){
            resultToReturn.put(hotelId,getRelatedUserReviews(userName,hotelId));
        }
        return resultToReturn;
    }
    public static void main(String[] args) throws MalformedURLException, SolrServerException {
        String username="seeknfind";
        String keyword="and";
        for (Map.Entry<Long, LinkedHashSet<String>> entry : getSuggestionsFor(username, keyword).entrySet()) {
            System.out.println(entry.getKey());
            for (String review : entry.getValue())
                System.out.println(review);
        }
       
            ; // username is AuthorName and keyword is a word on reviews

    }
}
