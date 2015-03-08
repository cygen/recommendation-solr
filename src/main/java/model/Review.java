package model;

import com.google.gson.JsonObject;
import org.apache.solr.common.SolrDocument;

/**
 * Created by vishnu on 7/3/15.
 */
public class Review {
    String title = "";
    String reviewId = "";
    long hotelId = 0l;
    String author = "";
    String content = "";
    String date = "";
    double service = 0d;
    double cleanliness = 0d;
    double overall = 0d;
    double value = 0d;
    double sleepQuality = 0d;
    double rooms = 0d;
    double location = 0d;

    public Review() {
    }

    public Review(SolrDocument entry) {
        try {
            this.content = entry.get("ReviewContent") != null ? (String) entry.get("ReviewContent") : "";
            this.title = entry.get("ReviewTitle") != null ? (String) entry.get("ReviewTitle") : "";
            this.author = entry.get("AuthorName") != null ? (String) entry.get("AuthorName") : "Anonymous";
            this.reviewId = entry.get("ReviewId") != null ? (String) entry.get("ReviewId") : "";
            this.hotelId = entry.get("HotelId") != null ? (Long) entry.get("HotelId") : 0l;
            //Ratings
            this.overall = entry.get("Overall") != null ? (Double) entry.get("Overall") : 0d;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JsonObject toJsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("title", title);
        jsonObject.addProperty("reviewId", reviewId);
        jsonObject.addProperty("hotelId", hotelId);
        jsonObject.addProperty("author", author);
        jsonObject.addProperty("content", content);

        jsonObject.addProperty("overall", overall);
        return jsonObject;
    }
}
