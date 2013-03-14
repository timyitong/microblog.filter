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

import java.text.NumberFormat;

public class Query{
	public String tweetid;
	public String newesttweet;
	public String num;
	public String words;
	public Query(){
	}
	public Query(int query_id){
		NumberFormat nf=NumberFormat.getInstance();
		nf.setMinimumIntegerDigits(3);
		String s="MB"+nf.format(query_id);

		DBCollection coll=DBCon.getTable("queries");
		DBCursor cursor;
		try{
			BasicDBObject query= new BasicDBObject("num",s);
			cursor= coll.find(query);
			BasicDBObject obj=(BasicDBObject)cursor.next();
			tweetid=obj.getString("tweetid");
			newesttweet=obj.getString("newesttweet");
			num=obj.getString("num");
			words=obj.getString("words");

			cursor.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public String toString(){
		return "tweetid:"+tweetid+" newesttweet:"+newesttweet+" num:"+num+"\n words:"+words;
	}
}