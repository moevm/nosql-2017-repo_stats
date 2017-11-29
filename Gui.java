import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.*;
 
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
 
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
 
import org.bson.Document;
import com.mongodb.Block;
 
import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
 
 
public class Gui extends JFrame {
    private JButton r_button = new JButton("Repo's");
    private JButton e_button = new JButton("Events");
    private JButton a_button = new JButton("Actors");
    private static nPanel graph = new nPanel();
 
    private static MongoDatabase db;
    private static MongoCollection <Document> col;

    public static Gui app;
 
    static String[] Langs = {"JavaScript","Python","Ruby","Java","PHP","C","C++","Objective-C","C#","Perl"};
   
    public Gui() {
        super("Guthub Statizier");
        this.setBounds(100,100,900,600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container container = this.getContentPane();
            container.add(graph,BorderLayout.CENTER);
            JPanel jPane = new JPanel();
            container.add(jPane,BorderLayout.PAGE_END);
        jPane.add(r_button);
        jPane.add(e_button);
        jPane.add(a_button);
        r_button.addActionListener(new ButtonEventListener());
	r_button.setActionCommand("0");
	e_button.addActionListener(new ButtonEventListener());
	e_button.setActionCommand("1");
 	a_button.addActionListener(new ButtonEventListener());
	a_button.setActionCommand("2");
    }
 
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
   
    class ButtonEventListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
	  String action = e.getActionCommand();
	  //System.out.println("Button clicked! Command is " + action);
            try{
		  graph.mode = Integer.parseInt(action);
		} catch (Throwable t) {System.out.println("Parse Error!"+action);}
	    app.getContentPane().validate();
	    app.getContentPane().repaint();  
        }
    }
 
public static void main(String[] args) {
  MongoClient mongoClient = new MongoClient();
 
  db = mongoClient.getDatabase("test");
  col = db.getCollection("res");
  graph.keys = Langs;
  for (int i=0;i<Langs.length;i++)
    {
      int[] stat = getStats(Langs[i]);
      graph.map.put(Langs[i],stat);
    }
  app = new Gui();
  app.setVisible(true);
  }
}
 
class nPanel extends JPanel {
    public Map<String, int[]> map = new HashMap<String, int[]>();
    public String[] keys;
    public Integer mode;
    private Integer[] norms = {25,100,40};
    private String[] text = {"Repositoreies count on github for 01.01.2016",
			"Events performed count on github for 01.01.2016",
			"User's count, used github for 01.01.2016"};
 
    public nPanel() {
        setBorder(BorderFactory.createLineBorder(Color.black));
	mode = -1;
    }
 
    public Dimension getPreferredSize() {
        return new Dimension(640,480);
    }
 
    public void paintComponent(Graphics g) {
        super.paintComponent(g);    
        // System.out.println(getWidth()+","+getHeight());  
        // Draw Text
        if ((mode>=0) && (mode<=2)){
        for (int i=0;i<keys.length;i++){
          int y = 100+i*40;
          int wid = 10;
          int val = map.get(keys[i])[mode];
	  g.drawString(text[mode],200,60);
          g.drawString(keys[i],100,y);
          g.drawString(Integer.toString(val),250,y);
          g.setColor(Color.RED);
          g.fillRect(350,y-wid,val / norms[mode],wid*2);
          g.setColor(Color.BLACK);
          g.drawRect(350,y-wid,val / norms[mode],wid*2);
        }
	}
    }  
}