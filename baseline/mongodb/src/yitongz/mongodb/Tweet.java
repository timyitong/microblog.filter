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
	DocVector vector;
	String notags_tweet;
	String expand;
	String expand_tweet;
	String hashtags;
	String date;
	String user;
	String retweet;
	String hasurls;
	String onlyenglish;

	public double score=0;
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

			//whether I use the expanded tweet
			if (Configure.USE_TWEET_EXPAND)
				vector=new DocVector(expand_tweet);
			else
				vector=new DocVector(clean_tweet);

			cursor.close();
		}catch(Exception e){
			tweetid=tweet_id;
			docno=tweet_id;
			clean_tweet=null;
			//System.out.println("NOT_FOUND tweetid: "+tweet_id);
			//e.printStackTrace();
		}
	}
	public String toString(){
		return "tweetid:"+tweetid+" clean_tweet:"+clean_tweet;
	}

	public double simScore(Tweet t){
		return Calculator.getInstance().sim.getSimScore(this,t);
	}
}