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

import java.io.*;
import java.util.*;
import java.text.NumberFormat;

public class WordStats{
	private String folder=Configure.TOPIC_TWEET_FOLDER;
	private static WordStats stat=new WordStats();
	private Hashtable <String,Integer> map=null;
	private Hashtable <Integer,Integer> map_tc=null; 
	public static int current_query=2;
	private String tc_url=Configure.TOTAL_WORD_STATS;
	private WordStats(){
		map=new Hashtable<String,Integer>();
	}
	public static void setCurrentQueryID(int i){
		WordStats.current_query=i;
	}
	public static WordStats getInstance(){
		return stat;
	}
	public int getTC(){
		return getTC(current_query);
	}
	public int getTC(int query_id){
		if (map_tc==null){
			map_tc=new Hashtable<Integer,Integer>();
			try{
				BufferedReader br=new BufferedReader(new FileReader(new File(tc_url)));
				String line=null;
				while ((line=br.readLine())!=null){
					StringTokenizer st=new StringTokenizer(line);
					Integer qid=Integer.parseInt(st.nextToken());
					Integer count=Integer.parseInt(st.nextToken());
					map_tc.put(qid,count);
				}
			}catch(Exception e){e.printStackTrace();}
		}
		Integer tc=map_tc.get(query_id);
		if (tc==null)
			return 0;
		else
			return tc.intValue();
	}
	public int getTF(String word){
		return getTF(current_query,word);
	}
	public int getTF(int query_id,String word){
		Integer tf=map.get(query_id+word);
		if (tf==null){
			try{
				DBCollection coll=DBCon.getTable("words");
				BasicDBObject one=new BasicDBObject("query_id",query_id).append("word",word);
				BasicDBObject obj=(BasicDBObject)coll.findOne(one);
				if (obj!=null){
					tf=new Integer( Integer.parseInt(obj.getString("count")) );
				}else{	
					tf=new Integer(0);
				}
				map.put(query_id+word,tf);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return tf.intValue();
	}
	/*Load all stats into database*/
	public void run(){
		try{
		NumberFormat nf=NumberFormat.getInstance();
		nf.setMinimumIntegerDigits(3);
		String tag;

		DBCollection coll=DBCon.getTable("words");

		for (int i=1;i<=49;i++){
			//CHECK MODE decide which to skip
			if (Configure.TEST_MODE){
				if (i%5==1 || i==18) continue; // the 18 is missing all needed to run tweets
			}else{
				if (i%5!=1) continue;
			}

			tag="MB"+nf.format(i);
			TreeMap <String,Integer> map=new TreeMap<String,Integer>();
			BufferedReader br=new BufferedReader(new FileReader(new File(folder+tag+" ")));
			String line=null;
			int total_count=0;
			while ((line=br.readLine())!=null){
				if (line.matches(".*CLEANTWEET.*CLEANTWEET.*")){
					line=line.substring(0+"<CLEANTWEET>".length(),line.length()-"<CLEANTWEET>".length()-1);

					StringTokenizer st=new StringTokenizer(line);
					while (st.hasMoreTokens()){
						String word=st.nextToken();
						word=word.replaceAll("[^0-9a-zA-Z]","");
						if (word.length()>0){
							total_count++;
							Integer count=map.get(word);
							if (count==null){
								count=new Integer(1);
								map.put(word,count);
							}else{
								count+=1;
								map.put(word,count);
							}
						}
					}
				}
			}
			for (Map.Entry<String,Integer> entry : map.entrySet() ) {
		        Integer count = entry.getValue();
		        String word = entry.getKey();

		        BasicDBObject tmp=new BasicDBObject("query_id",i).append("word",word).append("count",count.intValue());
		        coll.save(tmp);
   			}
   			System.out.println(i+" "+total_count);
		}
		}catch(Exception e){e.printStackTrace();}
	}
}