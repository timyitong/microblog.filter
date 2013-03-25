import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.ServerAddress;

import java.util.*;
import java.io.*;
import java.text.NumberFormat;

public class QueryExpand{
	public static String doc_url="../../data/queryresult/";
	public static String index_url="../../data/_indri_inv/";
	private static int expand_level=10;
	private static int k=10;

	public static void main(String argv[]) throws Exception{
		NumberFormat nf=NumberFormat.getInstance();
		nf.setMinimumIntegerDigits(3);

		MongoClient mongo=new MongoClient("localhost",27017);
		DB db=mongo.getDB("microblog-filter");
		DBCollection coll=db.getCollection("queries");
		DBCollection tweets=db.getCollection("tweets");

		for (int i=2;i<=49;i++){
			if (i%5==1) continue;
			String tag="MB"+nf.format(i);

			BasicDBObject query=new BasicDBObject("num",tag);
			query=(BasicDBObject)coll.findOne(query);
			String words=query.getString("words");
			StringTokenizer st=new StringTokenizer(words);
			StringBuilder words_expand=new StringBuilder();
			while (st.hasMoreTokens()){
				String w=st.nextToken();
				//writeQueryFile(w);

				String command="sh runquery.sh "+w+" "+index_url+tag;
				System.out.println(command);
				Process process=Runtime.getRuntime().exec(command);
				BufferedReader br = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
				process.waitFor();
				int j=0;
				WordCounter counter=new WordCounter(words);
				/*When the search is finished, get the result*/
				while (br.ready() && j<k){
					j++;
					String line=br.readLine();
					StringTokenizer st2=new StringTokenizer(line);
					st2.nextToken();
					st2.nextToken();
					String tweetid=st2.nextToken();
					BasicDBObject tweet=(BasicDBObject) tweets.findOne(new BasicDBObject("tweetid",tweetid));
					if (tweet!=null)
						counter.add(tweet.getString("clean_tweet"));
				}
				words_expand.append(counter.getTopWords(expand_level));
			}
			query.append("words_expand",words_expand.toString());
			if (argv.length>0 && argv[0].equals("save"))
				coll.save(query);
			System.out.println(i+": "+words_expand.toString());
		}
	}
	public static void writeQueryFile(String s) throws Exception{
		BufferedWriter bw=new BufferedWriter(new FileWriter(new File("query.txt")));
		bw.write("");
		bw.write("<parameters><query><number>000</number><text>");
		bw.newLine();
		bw.write(s);
		bw.newLine();
		bw.write("</text></query></parameters>");
		bw.newLine();

		bw.close();
	}
}