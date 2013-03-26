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
public class SaveExpand{
	public static void main(String argv[]) throws Exception{
		NumberFormat nf=NumberFormat.getInstance();
		nf.setMinimumIntegerDigits(3);

		MongoClient mongo=new MongoClient("localhost",27017);
		DB db=mongo.getDB("microblog-filter");
		DBCollection coll=db.getCollection("queries");

		for (int i=1;i<=49;i++){
			if (i%5!=1) continue;

			String tag="MB"+nf.format(i);
			BufferedReader br=new BufferedReader(new FileReader(new File("top_words_ner/"+tag+".txt")));
			String line=null;
			StringBuilder sb=new StringBuilder();
			while ((line=br.readLine())!=null){
				sb.append(line.trim()+" ");
			}
			BasicDBObject query=(BasicDBObject)coll.findOne(new BasicDBObject("num",tag));
			query.append("words_expand",sb.toString());
			coll.save(query);
		}
	}
}