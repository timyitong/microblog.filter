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

public class Retrieve{
	public static int k=5;

	public static void main(String argv[]) throws Exception{
		NumberFormat nf=NumberFormat.getInstance();
		nf.setMinimumIntegerDigits(3);

		MongoClient mongo=new MongoClient("localhost",27017);
		DB db=mongo.getDB("microblog-filter");
		DBCollection coll=db.getCollection("queries");
		DBCollection tweets=db.getCollection("tweets");

		BufferedReader br=new BufferedReader(new FileReader(new File("queries_tagged.txt")));
		StringTokenizer st=new StringTokenizer(br.readLine());
		br.close();

		for (int i=2;i<=49;i++){
			if (i%5==1) continue;
			String query_tag="MB"+nf.format(i);

			BasicDBObject query=new BasicDBObject("num",query_tag);
			query=(BasicDBObject) coll.findOne(query);
			String words=query.getString("words").trim().replaceAll("[^A-Za-z0-9\\s]","");

			StringTokenizer tmp=new StringTokenizer(words);

			BufferedWriter bw=new BufferedWriter(new FileWriter(new File("top_words/"+query_tag+".txt")));

			while (tmp.hasMoreTokens()){
				String w=tmp.nextToken();

				String tag=st.nextToken();
				tag=tag.substring(tag.indexOf('/')+1,tag.length());
				if (!tag.trim().equals("O")){
					writeQueryFile(w);
					String command="sh runqueryfile.sh temp.txt ../../data/_indri_inv/"+query_tag;
					System.out.println(command);
					Process process=Runtime.getRuntime().exec(command);

					br = new BufferedReader(new InputStreamReader(
	                    process.getInputStream()));
					process.waitFor();

					WordCounter counter=new WordCounter(words);

					int j=0;
					/*When the search is finished, get the result*/
					while (br.ready() && j<k){
						String s=br.readLine();
						StringTokenizer s2=new StringTokenizer(s);
						s2.nextToken();
						s2.nextToken();
						s=s2.nextToken(); // tweetid

						BasicDBObject tweet=(BasicDBObject)tweets.findOne(new BasicDBObject("tweetid",s)); 
						if (tweet!=null)
							counter.add(tweet.getString("clean_tweet"));

						j++;
					}
					String line=counter.getTopWords(20);
					bw.write(line);
					bw.newLine();
				}
			}
			bw.close();

			getEntity(query_tag);
		}
	}
	public static void getEntity(String query_tag) throws Exception{
		String command="sh ../../../stanford-ner-2012-11-11/ner.sh"
		+" top_words/"+query_tag+".txt";
		Process process=Runtime.getRuntime().exec(command);
		BufferedReader br = new BufferedReader(new InputStreamReader(
	                    process.getInputStream()));
		process.waitFor();

		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("top_words_ner/"+query_tag+".txt")));
		while (br.ready()){
			String s=br.readLine();
			StringTokenizer st=new StringTokenizer(s);
			while (st.hasMoreTokens()){
				String w=st.nextToken();
				String word=w.substring(0,w.indexOf('/'));
				String tag=w.substring(w.indexOf('/')+1,w.length());
				if (!tag.equals("O")){
					bw.write(word);
					bw.newLine();
				}
			}
		}
		bw.close();
	}
	public static void writeQueryFile(String s) throws Exception{
		BufferedWriter bw=new BufferedWriter(new FileWriter(new File("temp.txt")));
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