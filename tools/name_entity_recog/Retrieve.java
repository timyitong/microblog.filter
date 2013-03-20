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
	int k=20;

	public static void main(String argv[]) throws Exception{
		NumberFormat nf=NumberFormat.getInstance();
		nf.setMinimumIntegerDigits(3);

		MongoClient mongo=new MongoClient("localhost",27017);
		DB db=mongo.getDB("microblog-filter");
		DBCollection coll=db.getCollection("queries");

		BufferedReader br=new BufferedReader(new FileReader(new File("queries_tagged.txt")));
		StringTokenizer st=new StringTokenizer(br.readLine());
		br.close();

		for (int i=2;i<=49;i++){
			if (i%5==1) continue;
			String tag="MB"+nf.format(i);

			BasicDBObject query=new BasicDBObject("num",tag);
			query=(BasicDBObject) coll.findOne(query);
			String words=query.getString("words").trim().replaceAll("[^A-Za-z0-9\\s]","");

			StringTokenizer tmp=new StringTokenizer(words);
			while (tmp.hasNextToken()){
				String w=tmp.nextToken();

				String tag=st.nextToken();
				tag=tag.substring(tag.indexOf('/')+1,tag.length());
				if (!tag.equals("0")){
				
					Process process=Runtime.getRuntime().exec(command);

					br = new BufferedReader(new InputStreamReader(
	                    process.getInputStream()));
					process.waitFor();
					int j=0;
					WordCounter counter=new WordCounter(words);
					/*When the search is finished, get the result*/
					while (br.ready() j<k){

						//TODO
						//TODO
					}
				}
			}
		}
		bw.close();
	}
}