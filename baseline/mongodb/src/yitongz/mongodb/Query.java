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
			if (new Tweet(tweetid).clean_tweet==null)
				System.out.println("missing 1st tweet:query-"+num);
			newesttweet=obj.getString("newesttweet");
			num=obj.getString("num");
			if (num.equals("MB041"))
				tweetid="29208662060830721";

			words=obj.getString("words");
			words_expand=obj.getString("words_expand");
			/*HERE do a little query expansion*/
			//words=expand(words);
			vector=new DocVector(words);
			
			if (Configure.QUERY_EXPAND && words_expand!=null && words_expand.trim().length()>0){
				DocVector vector_expand=new DocVector(words_expand);
				vector_expand.multiply(Configure.EXPAND_QUERY_WEIGHT);
				vector.multiply(1);
				vector.add(vector_expand);
			}
			//add the expand two:
			vector.add(IndriSearcher.getExpandedVector(num,words));
			
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

	private SVMChecker svmChecker=null;
	private SimpleChecker simpleChecker=null;

	public boolean checkIn(Tweet t){
		if (Configure.MODE.equals("SVM")){
			if (svmChecker==null) svmChecker=new SVMChecker(this);

			boolean judge=svmChecker.judge(t);

			if (judge==false){
				t.score=-1;
				return false;
			}else{
				//====This is the Centroid part
				/*
				Centroid new_cent=new Centroid(t);	
				
				//Ask centroid the score
				double score=centroid_list.getScore(new_cent);
				new_cent.tweet.score=score;
				//Add into centroid
				judge=centroid_list.add(new_cent);
				*/
				return judge;
			}
		}else if (Configure.MODE.equals("Simple")){
			if (simpleChecker==null) simpleChecker=new SimpleChecker(this);

			return simpleChecker.check(t);
		}else{

			//====This is the Centroid part

			Centroid new_cent=new Centroid(t);	
			
			//Check into centroid
			boolean judge=centroid_list.checkIn(new_cent);
			
			return judge;
		
		}
	}

	public String toString(){
		return "tweetid:"+tweetid+" newesttweet:"+newesttweet+" num:"+num+"\n words:"+words;
	}
}
