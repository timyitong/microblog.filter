package yitongz.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.ServerAddress;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;


import java.util.*;
import java.io.*;
import java.text.NumberFormat;

public class Main{
	private static void addTweets() throws Exception{

		String [] files=new File("../../data/_indri_xml_wrapped").list();
		File [] fs=new File("../../data/_indri_xml_wrapped").listFiles();
		/*Add all tweets into Database*/

		DBCollection tweets=DBCon.getTable("tweets");
		
		for (int i=0;i<files.length;i++){
			if (!files[i].matches(".*DS_Store.*")){
				File xmlFile = fs[i];
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				System.out.println(fs[i].toString());
				Document doc = dBuilder.parse(xmlFile);
				doc.getDocumentElement().normalize();

				NodeList nList = doc.getElementsByTagName("DOC");
				for (int j=0;j<nList.getLength();j++){
					Node d=nList.item(j);
					if (d.getNodeType() == Node.ELEMENT_NODE) {
						Element e=(Element) d;
						String tweetid=e.getElementsByTagName("TWEETID").item(0).getTextContent();
						String docno=e.getElementsByTagName("DOCNO").item(0).getTextContent();
						String clean_tweet=e.getElementsByTagName("CLEANTWEET").item(0).getTextContent();
						String notags_tweet=e.getElementsByTagName("NOTAGSTWEET").item(0).getTextContent();
						String expand=e.getElementsByTagName("EXPAND").item(0).getTextContent();
						String expand_tweet=e.getElementsByTagName("EXPANDTWEET").item(0).getTextContent();
						String hashtags=e.getElementsByTagName("HASHTAGS").item(0).getTextContent();
						String date=e.getElementsByTagName("DATE").item(0).getTextContent();
						String user=e.getElementsByTagName("USER").item(0).getTextContent();
						String retweet=e.getElementsByTagName("RETWEET").item(0).getTextContent();
						String hasurls=e.getElementsByTagName("HASURLS").item(0).getTextContent();
						String onlyenglish=e.getElementsByTagName("ONLYENGLISH").item(0).getTextContent();
					
						BasicDBObject tweet=new BasicDBObject("tweetid",shrink(tweetid)).
												append("docno",shrink(docno)).
												append("clean_tweet",clean_tweet).
												append("notags_tweet",notags_tweet).
												append("expand",expand).
												append("expand_tweet",expand_tweet).
												append("hashtags",hashtags).
												append("date",date).
												append("user",shrink(user)).
												append("retweet",retweet).
												append("hasurls",hasurls).
												append("onlyenglish",onlyenglish);

						tweets.insert(tweet);
					}
				}
			}
		}
	}
	private static String shrink(String s){
		return s.replaceAll("\\s","");
	}
	private static void addQueries() throws Exception{
		/*Add all queries into Database*/
		DBCollection queries=DBCon.getTable("queries");
		Iterator <Query> it = QueryList.iterator();
		while (it.hasNext()){
			Query q=it.next();
			BasicDBObject doc =new BasicDBObject("tweetid",shrink(q.tweetid)).
									append("newesttweet",shrink(q.newesttweet)).
									append("num",shrink(q.num)).
									append("words",q.words);
			queries.insert(doc);
		}
	}
	private static void init() throws Exception{
		/*switch to test data*/
			//Configure.switch_to_test();
		/*Load in the truth files*/
		Facts.load();

		TweetExpansion t=new TweetExpansion();
		/*Insert all queries*/
			//addQueries();
		/*Insert all tweets*/
			//addTweets();
		/*Add WortStats to database*/
			//WordStats st=WordStats.getInstance();
			//st.run_2gram();
	}
	private static void test() throws Exception{

	}
	private static void run_batch() throws Exception {
		new BatchTask();
	}
	private static void run_tune() throws Exception{
		new TuneTask();
	}
	public static void main(String argv[]){
		try{
			init();
			//test();
			
			if (argv[0]==null){
				run_batch();
			}else if (argv[0].equals("TuneTask")){
				run_tune();
			}else{
				run_batch();
			}
		}catch(Exception e){e.printStackTrace();}
	}
}