
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

import static com.mongodb.client.model.Updates.*;

import com.mongodb.client.result.UpdateResult;

import java.util.ArrayList;

import java.util.List;
import java.util.Date;




public class Hello
{


  public static void main(String []args)
  {

    MongoClient mongoClient = new MongoClient();

//    MongoClient mongoClient = new MongoClient("localhost");

//    MongoClient mongoClient = new MongoClient("localhost",27017);

    
    MongoDatabase db = mongoClient.getDatabase("test");
    
    MongoCollection<Document> col = db.getCollection("test");

    
    Document doc = new Document("Hello","World");
   
    doc.append("Date", new Date());

    col.insertOne(doc);
 


    System.out.println("Results:");
    
    Block<Document> printBlock = new Block<Document>()
    { 
    
      @Override
    
      public void apply(final Document document) {
        
        System.out.println(document.toJson());
    
      }

    };

    col.find().forEach(printBlock);
    
    System.out.println("Count of documents: " + col.count());
  }  

}