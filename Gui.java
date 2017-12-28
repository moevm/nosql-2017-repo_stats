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
    private JButton stat_button = new JButton("Statistic");
    private JButton top_button = new JButton("Top Repositories");
    private JButton[] lang_buttons = new JButton[10];
    private static nPanel graph = new nPanel();
    private static JPanel statPane;
    private static JPanel objPane;
    private static JPanel langPane;
 
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
          JPanel buttPane = new JPanel(new BorderLayout());
          container.add(buttPane,BorderLayout.PAGE_END);
          statPane = new JPanel();
          buttPane.add(statPane,BorderLayout.PAGE_START);
          langPane = new JPanel();
          buttPane.add(langPane,BorderLayout.PAGE_END);
          objPane = new JPanel();
          container.add(objPane,BorderLayout.PAGE_START);
        for (int j = 0;j<10;j++)
        {
          lang_buttons[j] = new JButton(Langs[j]);
          langPane.add(lang_buttons[j]);
          lang_buttons[j].addActionListener(new ButtonEventListener());
          lang_buttons[j].setActionCommand(Integer.toString(j+3));
        }
        statPane.add(r_button);
        statPane.add(e_button);
        statPane.add(a_button);
        objPane.add(stat_button);
        objPane.add(top_button);
        stat_button.addActionListener(new ButtonEventListener());
        stat_button.setActionCommand("stat");
        top_button.addActionListener(new ButtonEventListener());
        top_button.setActionCommand("top");
        r_button.addActionListener(new ButtonEventListener());
	r_button.setActionCommand("0");
	e_button.addActionListener(new ButtonEventListener());
	e_button.setActionCommand("1");
 	a_button.addActionListener(new ButtonEventListener());
	a_button.setActionCommand("2");
    }

    private static String[] getTop(String lang, String[] repos, int[] events)
    {
      String[] ret = new String[10];
      MongoCursor<Document> cur = col.find(eq("langs",lang)).sort(descending("events")).iterator();
      for (int i=0;i<10;i++)
      {
        Document doc = cur.next();
        ret[i] = doc.get("repository")+":"+doc.get("events");
        repos[i] = doc.get("repository").toString();
        events[i] = (int)doc.get("events");
      } 
      return ret;
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
                  graph.setVisible(true);
		  graph.mode = Integer.parseInt(action);
		} catch (Throwable t) 
            {
              if (action.equals("stat"))
              {
                graph.setVisible(false);
                langPane.setVisible(false);
                statPane.setVisible(true);
              }
              if (action.equals("top"))
              {
                graph.setVisible(false);
                statPane.setVisible(false);
                langPane.setVisible(true);
              }

            }
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
      int[] events = new int[10];
      String[] repos = new String[10];
      getTop(Langs[i],repos,events);
      graph.topRepo.put(Langs[i],repos);
      graph.topCount.put(Langs[i],events);
    }
  app = new Gui();
  graph.setVisible(false);
  statPane.setVisible(false);
  langPane.setVisible(false);
  app.setVisible(true);
  
  }
}

 
class nPanel extends JPanel {
    public Map<String, int[]> map = new HashMap<String, int[]>();
    public Map<String, String[]> topRepo = new HashMap<String, String[]>();
    public Map<String, int[]> topCount = new HashMap<String, int[]>();
    public String[] keys;
    public Integer mode;
    private Integer[] norms = {25,100,40};
    private String[] text = {
        "Repositoreies count on github for 01.01.2016",
	"Events performed count on github for 01.01.2016",
	"User's count, used github for 01.01.2016",
        "TOP-20 Repositories for "};
 
    public nPanel() {
        //setBorder(BorderFactory.createLineBorder(Color.black));
	mode = -1;
    }
 
    public Dimension getPreferredSize() {
        return new Dimension(640,480);
    }
 
    public void paintComponent(Graphics g) {
        super.paintComponent(g);    
        //System.out.println(mode);
        // System.out.println(getWidth()+","+getHeight());  
        // Draw Text
        int x = getWidth()/32;
        int h = getHeight()/24;
        float f;
        if (x<h) f = x;
        else f = h;
        f = f*3/4;
        g.setFont(g.getFont().deriveFont(f));
        int max = 0;
        if ((mode>=0) && (mode<=2)){
	  g.drawString(text[mode],x*6,h);
          for (int i=0;i<keys.length;i++){
            int y = h*3+i*h*2;
            int val = map.get(keys[i])[mode];
            if (i==0) max = val;
            g.drawString(keys[i],x*4,y);
            g.drawString(Integer.toString(val),x*8,y);
            g.setColor(Color.RED);
            g.fillRect(x*10,y-h,val*20*x / max,h);
            g.setColor(Color.BLACK);
            g.drawRect(x*10,y-h,val*20*x / max,h);
          }
        }
        if ((mode>=3) && (mode <=12))
        {
	  g.drawString("TOP-10 repositories for "+keys[mode-3],x*6,h);
          for (int i=0;i<10;i++)
          {
            int y = h*3+i*h*2;
            g.drawString(Integer.toString(i+1),x*2, y);
            g.drawString(topRepo.get(keys[mode-3])[i],x*4, y);
            g.drawString(Integer.toString(topCount.get(keys[mode-3])[i]),x*28, y);
         
          }
	}
    }  
}
