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
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;

public class Fetcher{

  public static void main(String []arsgs){
    MongoClient mongoClient = new MongoClient();

    MongoDatabase db = mongoClient.getDatabase("test");
    MongoCollection<Document> ev = db.getCollection("ev");
    MongoCollection<Document> events = db.getCollection("events");

    MongoCursor<Document> cur = events.find().iterator();
    try
    {
      while (cur.hasNext())
      {
        Document cdoc = cur.next();
        Document ndoc = new Document();
        Document repo = (Document) cdoc.get("repo");
        Document actor = (Document) cdoc.get("actor");
        ndoc.append("event",cdoc.get("type"));
        ndoc.append("repo_name",repo.get("name"));
        ndoc.append("actor",actor.get("login"));
        ev.insertOne(ndoc);
        System.out.println(cdoc.get("created_at"));
      }
    }
    finally
    {
      cur.close();
    }
    
  }
}