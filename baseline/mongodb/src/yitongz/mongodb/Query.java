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

public class Query{
	public String tweetid;
	public String newesttweet;
	public String num;
	public String words;
	public String words_expand;

	/*These are for checkIn calculation*/
	public ArrayList <Centroid> centroid_list=new ArrayList <Centroid>();
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
			words=expand(words);

			cursor.close();

			//Initilize the centroid
			Tweet tmp=new Tweet(tweetid);
			first_one=new Centroid(tmp);
			first_one.relevant=true;


			/*add default score with query*/
			double score=tmp.simScore(this);
			rel_score_sum+=score;
			rel_score_sqr_sum+=score*score;
			rel_count+=1;

			centroid_list.add(first_one);

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
		this.count++;
		int check=t.tweetid.compareTo(first_one.tweet.tweetid);

		if (check < 0){ // not reach the first element yet
			Centroid new_cent=new Centroid(t);
			new_cent.query_score=t.simScore(this);

			//Here choose the farthest one:

			//here choose the closet neg one as the first neg:
			if (first_neg_centroid==null || new_cent.query_score > first_neg_centroid.query_score){
				first_neg_centroid=new_cent;
			}

			return false;
		}else if (check == 0){
			first_neg_centroid.relevant=false;
			centroid_list.add(first_neg_centroid);
			System.out.println(t.tweetid);
			return true;
		}

		Centroid new_cent=new Centroid(t);
		for (Centroid cent : centroid_list){
			cent.score=cent.tweet.simScore(new_cent.tweet);
		}
		/*K nearst neighbour:*/
		Collections.sort(centroid_list);
		int top_len=Math.min(centroid_list.size(),k);
		ArrayList <Centroid> top_k_list=new ArrayList <Centroid> (centroid_list.subList(0,top_len));
		
		/*K nearst neighbour:voting*/
		double vote=0;
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

		/*Punish the vote score with similarity with query*/
		vote=alpha*vote+(1-alpha)*t.simScore(this);

		//System.out.println("result:"+t.tweetid+" "+vote);
		double cutoff;
		double avg=rel_score_sum/rel_count;
		if (rel_count==1)
			cutoff=avg;
		else{
			double ratio=(rel_count-1.0)/count;
			cutoff=avg-0.3*Math.sqrt(    (rel_score_sqr_sum-avg*avg*rel_count) /  (rel_count-1)   );
		}

		if (t.tweetid.equals("29093916263321600")){
			System.out.println(num+": "+cutoff+"   "+vote);
		}

		if ( vote > cutoff){
			new_cent.relevant=true;
			rel_score_sum+=vote;
			rel_score_sqr_sum+=vote*vote;
			rel_count++;
			//System.out.println(num+": "+cutoff);
		}else
			new_cent.relevant=false;
		//use vote score as the new score
		new_cent.query_score=vote;
		//use sim score as the new score
			//new_cent.query_score=t.simScore(this);
		centroid_list.add(new_cent);


		//System.out.println("judge:"+new_cent.relevant+"\n");
		
		return new_cent.relevant;
	}

	public String toString(){
		return "tweetid:"+tweetid+" newesttweet:"+newesttweet+" num:"+num+"\n words:"+words;
	}
}