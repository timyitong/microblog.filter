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
import java.io.*;
import java.util.*;

public class QueryReader{
	public static String doc_url="../../data/queryresult_prf/";
	public static String out_url="../../data/query_reader/";

	public static int K=10;
	public static void main(String argv[]) throws Exception{
		MongoClient mongo=new MongoClient("localhost",27017);
		DB db=mongo.getDB("microblog-filter");
		DBCollection coll=db.getCollection("tweets");

		NumberFormat nf=NumberFormat.getInstance();
		nf.setMinimumIntegerDigits(3);


		for (int i=2;i<=49;i++){
			if (i%5==1) continue;

			String tag="MB"+nf.format(i);

			BufferedReader br=new BufferedReader(new FileReader(new File(doc_url+tag+".out")));
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File(out_url+tag+".txt")));


			String line=null;
			int j=0;
			while ((line=br.readLine())!=null && j<K){
				StringTokenizer st=new StringTokenizer(line);
				st.nextToken();
				st.nextToken();
				String tweet_id=st.nextToken();

				BasicDBObject query= new BasicDBObject("tweetid",tweet_id.replaceAll("\\s",""));
				DBCursor cursor= coll.find(query);
				BasicDBObject obj=(BasicDBObject)cursor.next();

				String clean_tweet=obj.getString("clean_tweet");
				bw.write(clean_tweet);
				bw.newLine();

				j++;
			}

			bw.close();
		}

	}
}