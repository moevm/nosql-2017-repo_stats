import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import java.util.Arrays;
import com.mongodb.Block;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Accumulators;
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;

public class Shaper{

  public static void main(String []arsgs){
    MongoClient mongoClient = new MongoClient();
//    MongoClient mongoClient = new MongoClient("localhost");
//    MongoClient mongoClient = new MongoClient("localhost",27017);

    MongoDatabase db = mongoClient.getDatabase("test");
    MongoCollection<Document> col = db.getCollection("res");
    MongoCollection<Document> lang = db.getCollection("lang");
    MongoCollection<Document> events = db.getCollection("ev");

    Document cdoc = new Document();
    MongoCursor<Document> cur = lang.find().sort(ascending("repository")).iterator();
    cdoc.append("repository", lang.find().sort(ascending("repository")).first().get("repository"));
    ArrayList<String> clang = new ArrayList<String>(); 
    try
    {
      while (cur.hasNext())
      {
        Document ndoc = cur.next();
        if (!cdoc.get("repository").equals(ndoc.get("repository")))
        {
          cdoc.append("events",(int)events.count(eq("repo_name",cdoc.get("repository"))));
          cdoc.append("actors",events.aggregate(Arrays.asList(
            Aggregates.match(eq("repo_name",cdoc.get("repository"))),
            Aggregates.group("$actor",Accumulators.sum("count",1)),
            Aggregates.group("all",Accumulators.sum("count",1))
            )).first().get("count"));
          cdoc.append("langs",clang);
          System.out.println(events.aggregate(Arrays.asList(
            Aggregates.match(eq("repo_name",cdoc.get("repository"))),
            Aggregates.group("$actor",Accumulators.sum("count",1))
            )).first().toJson());
 
          System.out.println(cdoc.get("repository")+" - actors:"+cdoc.get("actors")+", events:"+cdoc.get("events"));
          col.insertOne(cdoc);
          cdoc = new Document();
          cdoc.append("repository",ndoc.get("repository"));
          clang.clear();
        }
        clang.add((String)ndoc.get("lang"));
      }
    }
    finally
    {
      cur.close();
    }
    
  }
}