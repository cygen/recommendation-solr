/**
 * Created by root on 3/19/15.
 */
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SolrjPopulator {
    private static final Logger LOG = Logger.getLogger(SolrjPopulator.class.getName());
    public static void main(String[] args) throws IOException, SolrServerException {
        HttpSolrServer server = new HttpSolrServer("http://localhost:8983/solr/arduino_core");
            SAXBuilder saxBuilder = new SAXBuilder();
            File xmlFile = new File("Posts.xml"); //change path if needed
            try {
                Document document = (Document) saxBuilder.build(xmlFile);
                Element rootNode = document.getRootElement();
                List<Element> posts = rootNode.getChildren("row");
                for(int i=0;i<posts.size();i++){
                    Element post = posts.get(i);
                    try {
                        if(post.getAttributeValue("Title")==null || post.getAttributeValue("Title")=="") // for indexing only questions
                            continue;
                        String tagsString = post.getAttributeValue("Tags");
                        List<String> tags = new ArrayList<String>();
                        if(tagsString!=null) {
                            tagsString = tagsString.replaceAll("<", "");
                            for (String tag : tagsString.split(">")) {
                                if (!"".equals(tag)) {
                                    tags.add(tag);
                                }
                            }
                        }
                        SolrInputDocument doc = new SolrInputDocument();
                        doc.addField("id",i);
                        doc.addField("PostId", post.getAttributeValue("Id"));
                        doc.addField("Body", post.getAttributeValue("Body"));
                        doc.addField("ViewCount", post.getAttributeValue("ViewCount"));
                        doc.addField("Score", post.getAttributeValue("Score"));
                        doc.addField("LastActivityDate", post.getAttributeValue("LastActivityDate")+"Z"); //z for utc according to solr doc
                        doc.addField("AnswerCount", post.getAttributeValue("AnswerCount"));
                        doc.addField("Title", post.getAttributeValue("Title"));
                        doc.addField("Tags", tags);
                        server.add(doc);
                        if(i%100==0) server.commit();   //expensive operation

                    } catch (Exception e){
                        LOG.warning(e.getMessage());
                    }
                }

            } catch (IOException io) {
                LOG.severe(io.getMessage());
            } catch (JDOMException jdomex) {
                LOG.severe(jdomex.getMessage());
            }

    }
}
