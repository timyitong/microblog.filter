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

public class GetQueryText{

	public static void main(String argv[]) throws Exception{
		NumberFormat nf=NumberFormat.getInstance();
		nf.setMinimumIntegerDigits(3);

		MongoClient mongo=new MongoClient("localhost",27017);
		DB db=mongo.getDB("microblog-filter");
		DBCollection coll=db.getCollection("queries");

		BufferedWriter bw=new BufferedWriter(new FileWriter(new File("queries.txt")));
		for (int i=2;i<=49;i++){
			if (i%5==1) continue;
			String tag="MB"+nf.format(i);

			BasicDBObject query=new BasicDBObject("num",tag);
			query=(BasicDBObject) coll.findOne(query);
			bw.write(query.getString("words").trim().replaceAll("[^A-Za-z0-9\\s]",""));
			bw.newLine();
		}
		bw.close();
	}
}