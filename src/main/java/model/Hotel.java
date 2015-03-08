package model;

import com.google.gson.JsonObject;
import org.apache.solr.common.SolrDocument;

/**
 * Created by vishnu on 7/3/15.
 */
public class Hotel {
    String name="";
    String website="";
    String adddress="";
    String imageurl="";
    String price="";
    long id = 0l;
    public Hotel(){}
    public Hotel(SolrDocument entry){
        this.name =entry.get("HotelName")!=null?(String) entry.get("HotelName"):"";
        this.website =entry.get("HotelUrl")!=null?(String) entry.get("HotelUrl"):"";
        this.adddress =entry.get("HotelAddress")!=null?(String) entry.get("HotelAddress"):"";
        this.imageurl =entry.get("HotelImgUrl")!=null?(String) entry.get("HotelImgUrl"):"";
        this.price =entry.get("Price")!=null?(String) entry.get("Price"):"";
        this.id =entry.get("HotelId")!=null?(Long) entry.get("HotelId"):0l;
    }
    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name",name);
        jsonObject.addProperty("website",website);
        jsonObject.addProperty("adddress",adddress);
        jsonObject.addProperty("imageurl",imageurl);
        jsonObject.addProperty("price",price);
        jsonObject.addProperty("id",id);
        return jsonObject;
    }
}
