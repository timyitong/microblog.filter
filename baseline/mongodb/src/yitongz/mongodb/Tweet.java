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
import java.math.BigInteger;

public class Tweet implements Comparable <Tweet>{
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
			String url_expand=TweetExpansion.getExpand(tweetid);
			
			notags_tweet=obj.getString("notags_tweet");
			expand=obj.getString("expand");
			expand_tweet=obj.getString("expand_tweet");
			hashtags=obj.getString("hashtags");
			date=obj.getString("date");
			user=obj.getString("user");
			//Handle new crawled tweets:
			if (user==null && clean_tweet!=null){
				clean_tweet=clean_tweet.replaceAll("http[^\\s]*","");
				clean_tweet=clean_tweet.replaceAll("[^A-Za-z0-9\\s]","");
				if (clean_tweet.trim().length()==0)
					clean_tweet=null;
				//else
				//	System.out.println(clean_tweet);
			}

			retweet=obj.getString("retweet");
			hasurls=obj.getString("hasurls");
			onlyenglish=obj.getString("onlyenglish");

			//whether I use the expanded tweet
			if (Configure.USE_TWEET_EXPAND)
				vector=new DocVector(expand_tweet);
			else
				vector=new DocVector(clean_tweet);

			if (Configure.USE_URL_EXPAND && url_expand!=null){
				DocVector d=new DocVector(url_expand);
				d.multiply(Configure.URL_WEIGHT);
				vector.add(d);
			}

			cursor.close();
		}catch(Exception e){
			tweetid=tweet_id;
			docno=tweet_id;
			clean_tweet=null;
			//System.out.println("NOT_FOUND tweetid: "+tweet_id);
			//e.printStackTrace();
		}
	}
	public void save(){
		try{
		DBCollection coll=DBCon.getTable("tweets");
		BasicDBObject obj=new BasicDBObject("tweetid",tweetid);
		obj.append("docno",docno)
		 	.append("clean_tweet",clean_tweet)
		 	.append("notags_tweet",notags_tweet)
		 	.append("expand",expand)
		 	.append("expand_tweet",expand_tweet)
		 	.append("hashtags",hashtags)
		 	.append("date",date)
		 	.append("user",user)
		 	.append("retweet",retweet)
		 	.append("hasurls",hasurls)
		 	.append("onlyenglish",onlyenglish);

		coll.save(obj);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public String toString(){
		return "tweetid:"+tweetid+" clean_tweet:"+clean_tweet;
	}

	public double simScore(Query q){

		return Calculator.getInstance().sim.getSimScore(this,q,null);
	}

	public double simScore(Tweet t){
		return Calculator.getInstance().sim.getSimScore(this,t);
	}

	public int compareTo(Tweet t){
		if (this.tweetid==null)
			return -1;
		if (t.tweetid==null)
			return 1;
		return new BigInteger(this.tweetid).compareTo(new BigInteger(t.tweetid));
	}
}