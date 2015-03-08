package model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.HotelService;
import io.ReviewService;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.util.*;

/**
 * Created by vishnu on 7/3/15.
 */
public class Hotel {
    static HttpSolrServer REVIEW_CONNECTOR = new HttpSolrServer("http://localhost:8983/solr/recReview");
    String name = "";
    String website = "";
    String adddress = "";
    String imageurl = "";
    String price = "";
    List<Review> relatedReviews = new ArrayList<Review>();
    long id = 0l;

    public Hotel() {
    }

    public Hotel(SolrDocument entry) {
        this.name = entry.get("HotelName") != null ? (String) entry.get("HotelName") : "";
        this.website = entry.get("HotelUrl") != null ? (String) entry.get("HotelUrl") : "";
        this.adddress = entry.get("HotelAddress") != null ? (String) entry.get("HotelAddress") : "";
        this.imageurl = entry.get("HotelImgUrl") != null ? (String) entry.get("HotelImgUrl") : "";
        this.price = entry.get("Price") != null ? (String) entry.get("Price") : "";
        this.id = entry.get("HotelId") != null ? (Long) entry.get("HotelId") : 0l;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("website", website);
        jsonObject.addProperty("adddress", adddress);
        jsonObject.addProperty("imageurl", imageurl);
        jsonObject.addProperty("price", price);
        jsonObject.addProperty("id", id);
        if (!relatedReviews.isEmpty()) {
            JsonArray reviewsArray = new JsonArray();
            for (Review review : relatedReviews) {
                reviewsArray.add(review.toJsonObject());
            }
            jsonObject.add("reviews", reviewsArray);
        }
        return jsonObject;
    }

    public void getRelatedUserReviews(String userName) {

        try {
            int i = 0;
            for (String relatedName : ReviewService.getRelatedUsers(userName, this.id)) {
                SolrQuery query = new SolrQuery();
                query.set("defType", "dismax"); //setting dismax query parser
                query.set("q.alt", "(HotelId:" + this.id + ") AND (AuthorName:" + relatedName + ")");
                QueryResponse response = REVIEW_CONNECTOR.query(query);
                SolrDocumentList results = response.getResults();
                for (i = 0; i < 3 && i < results.size(); ++i) {
                    relatedReviews.add(new Review(results.get(i)));
                }
            }
            if (this.relatedReviews.size() < 3) {
                SolrQuery query = new SolrQuery();
                query.set("defType", "dismax"); //setting dismax query parser
                query.set("q.alt", "(HotelId:" + this.id + ")"); //setting parameter to search on title and Tags and give more boost to title wise results
                QueryResponse response = REVIEW_CONNECTOR.query(query);
                SolrDocumentList results = response.getResults();
                for (i = 0; i < 3 && i < results.size(); ++i) {
                    this.relatedReviews.add(new Review(results.get(i)));
                }
            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
    }

    public static List<Hotel> getListOfHotelsFor(String keyword) {
        List<Hotel> hotels = new ArrayList<Hotel>();
        Set<Long> hotelIds = new HashSet<Long>();
        try {
            SolrQuery reviewQuery = new SolrQuery();
            reviewQuery.set("defType", "dismax"); //setting dismax query parser
            reviewQuery.set("q.alt", "(ReviewContent:*" + keyword + ")");
            QueryResponse response = REVIEW_CONNECTOR.query(reviewQuery);
            SolrDocumentList results = response.getResults();
            for (int i = 0; i < results.size(); ++i) {
                hotelIds.add((Long) results.get(i).get("HotelId"));
            }
            for (Long id : hotelIds) {
                hotels.add(HotelService.getHotel(String.valueOf(id)));
            }
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        return hotels;
    }
}
