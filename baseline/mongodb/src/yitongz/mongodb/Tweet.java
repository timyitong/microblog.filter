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

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.lang.Math;

public class Tweet{
	String tweetid;
	String docno;
	String clean_tweet;
	String notags_tweet;
	String expand;
	String expand_tweet;
	String hashtags;
	String date;
	String user;
	String retweet;
	String hasurls;
	String onlyenglish;

	public double score;
	public boolean relevant;
	public String query_num;

	public Tweet(){

	}
	public Tweet(String tweet_id){
		DBCollection coll=DBCon.getTable("tweets");
		DBCursor cursor;
		try{
			BasicDBObject query= new BasicDBObject("tweetid",tweet_id.replaceAll("\\s",""));
			cursor= coll.find(query);
			BasicDBObject obj=(BasicDBObject)cursor.next();
			
			tweetid=obj.getString("tweetid");
			docno=obj.getString("docno");
			clean_tweet=obj.getString("clean_tweet");
			notags_tweet=obj.getString("notags_tweet");
			expand=obj.getString("expand");
			expand_tweet=obj.getString("expand_tweet");
			hashtags=obj.getString("hashtags");
			date=obj.getString("date");
			user=obj.getString("user");
			retweet=obj.getString("retweet");
			hasurls=obj.getString("hasurls");
			onlyenglish=obj.getString("onlyenglish");

			cursor.close();
		}catch(Exception e){
			tweetid=tweet_id;
			docno=tweet_id;
			clean_tweet=null;
			//System.out.println("NOT FOUND tweetid:"+tweet_id);
			//e.printStackTrace();
		}
	}
	public String toString(){
		return "tweetid:"+tweetid+" clean_tweet:"+clean_tweet;
	}

	public double simScore(Tweet t){
		if (t.clean_tweet==null || this.clean_tweet==null)
			return 0;

		Hashtable <String,Integer> tf_map1=new Hashtable <String,Integer>();
		Hashtable <String,Integer> tf_map2=new Hashtable <String,Integer>();
		LinkedList <String> l1=new LinkedList <String>();
		LinkedList <String> l2=new LinkedList <String>();

		StringTokenizer st=new StringTokenizer(this.clean_tweet);
		while (st.hasMoreTokens()){
			String word=st.nextToken();
			Integer tf=tf_map1.get(word);
			if (tf==null){
				tf_map1.put(word,new Integer(1));
				l1.add(word);
			}else{
				tf++;
			}
		}

		st=new StringTokenizer(t.clean_tweet);
		while (st.hasMoreTokens()){
			String word=st.nextToken();
			Integer tf=tf_map2.get(word);
			if (tf==null){
				tf_map2.put(word,new Integer(1));
				l2.add(word);
			}else{
				tf++;
			}
		}

		double sim=0;
		double mod1=0;
		double mod2=0;
		for (String w : l1){
			Integer tf1=tf_map1.get(w);
			mod1+=tf1*tf1;

			Integer tf2=tf_map2.get(w);
			if (tf2==null)
				tf2=new Integer(0);
			sim+=tf1*tf2;
		}
		for (String w: l2){
			Integer tf2=tf_map2.get(w);
			mod2+=tf2*tf2;
		}
		mod1=Math.sqrt(mod1);
		mod2=Math.sqrt(mod2);

		sim=sim/(mod1*mod2);

		return sim;
	}
}