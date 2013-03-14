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

public class DBCon{
	private static MongoClient mongo=null;
	private static DB db=null;

	private DBCon(){}
	private static void init(){
		try{
		mongo=new MongoClient("localhost",27017);
		db=mongo.getDB("microblog-filter");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static DBCollection getTable(String table_name){
		if (mongo==null)
			init();
		return db.getCollection(table_name);
	}
}