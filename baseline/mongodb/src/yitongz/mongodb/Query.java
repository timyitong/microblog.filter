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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.lang.Math;
import java.util.StringTokenizer;
import java.lang.StringBuilder;

import java.math.BigInteger;

public class Query{
	public String tweetid;
	public String newesttweet;
	public String num;
	public String words;
	public DocVector vector;
	public String words_expand;

	/*These are for checkIn calculation*/
	public CentroidList centroid_list;
	public Centroid first_one;
	public Centroid first_neg_centroid=null;

	public double rel_score_sum=0;
	public double rel_score_sqr_sum=0;
	public int rel_count=0;
	public int count=0;

	public static int k=2;

	public static double alpha=0.3;

	/********/

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
			words_expand=obj.getString("words_expand");
			/*HERE do a little query expansion*/
			//words=expand(words);
			vector=new DocVector(words);
			if (words_expand!=null){
				DocVector vector_expand=new DocVector(words_expand);
				vector_expand.multiply(0.1);
				vector.multiply(0.9);
				vector.add(vector_expand);
			}
			cursor.close();

			centroid_list=new CentroidList(this);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public String expand(String words){
		StringBuilder build=new StringBuilder();
		build.append(words);
		WordStats stat=WordStats.getInstance();
		StringTokenizer st=new StringTokenizer(words);
		int count=-1;
		String exp_word=null;
		while (st.hasMoreTokens()){
			String w=st.nextToken();
			int tmp_count=stat.getTF(w);
			if (tmp_count<count){
				count=tmp_count;
				exp_word=w;
			}
		}
		if (exp_word!=null){
			for (int i=0;i<5;i++)
				build.append(" "+exp_word);
		}
		return build.toString();
	}

	
	public boolean checkIn(Tweet t){
		Centroid new_cent=new Centroid(t);	
		//Get the score of this tweet with this query
		new_cent.tweet.score=t.simScore(this);
		boolean judge=centroid_list.add(new_cent);
		
		//If want to check the fact:
		if (Configure.CHECK_FACT && judge==true)
				new_cent.relevant=Facts.check(this.num,t.tweetid);

		return judge;
		
	}

	public String toString(){
		return "tweetid:"+tweetid+" newesttweet:"+newesttweet+" num:"+num+"\n words:"+words;
	}
}

/* 	This block is commented out because I adopt rachhio
		it belongs to checkIn method
		//K nearst neighbour:
		Collections.sort(centroid_list);
		int top_len=Math.min(centroid_list.size(),k);
		ArrayList <Centroid> top_k_list=new ArrayList <Centroid> (centroid_list.subList(0,top_len));
		
		//K nearst neighbour:voting
		
		double sim_sum=0;

		int rel=0;
		for (Centroid cent : top_k_list){
			vote+=cent.score*cent.query_score;
			sim_sum+=cent.score;
			//System.out.println("score:"+cent.score+" hostid:"+cent.tweet.tweetid+" vote:"+vote);
		}
		if (sim_sum!=0)
			vote=vote/sim_sum;
		else
			vote=0;

		//Punish the vote score with similarity with query
		vote=alpha*vote+(1-alpha)*t.simScore(this);
*/
