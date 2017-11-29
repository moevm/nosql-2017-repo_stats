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
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;

public class Anal{
  
  private static MongoDatabase db;
  private static MongoCollection <Document> col;

  private static int[] getStats(String lang)
  {
    //ret[0]:Repos count ret[1]:Events Count ret[2]:Actors Count
    int[] ret = new int[3];


    ret[0] = (int)col.count(eq("langs",lang));
    Document ndoc = col.aggregate(Arrays.asList(
            Aggregates.match(eq("langs",lang)),
            Aggregates.group("all",Accumulators.sum("actors","$actors"),Accumulators.sum("events","$events"))
            )).first();
    ret[1] = (int)ndoc.get("events");
    ret[2] = (int)ndoc.get("actors");
    return ret;

  }



  public static void main(String []arsgs){
    MongoClient mongoClient = new MongoClient();

    db = mongoClient.getDatabase("test");
    col = db.getCollection("res");

    String[] Langs = {"JavaScript","Python","Ruby","Java","PHP","C","C++","Objective-C","C#","Perl"};
    System.out.println("R - repositories, E - events, A - actors");
    for (int i=0;i<Langs.length;i++)
    {
      int[] stat = getStats(Langs[i]);
//      System.out.println(Langs[i]+":\t R-" + stat[0] + ", E-" + stat[1] + ",A-" + stat[2] + ",E/R-" + stat[1]/stat[0] + ",A/R-" + stat[2]/stat[0] + ",E/A-" + stat[1]/stat[2]);
      System.out.format("%12s: R-%6d, E-%6d, A-%6d, E/R-%2.2f, A/R-%2.2f, E/A-%2.2f;\n",Langs[i],stat[0] ,stat[1],stat[2], (float) stat[1]/stat[0],(float) stat[2]/stat[0],(float) stat[1]/stat[2]);
    }
  }
}