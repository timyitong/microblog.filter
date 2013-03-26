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
import java.math.BigInteger;
public class GetFirstRel{
    public static void main(String argv[]){
        try{
            MongoClient mongo=new MongoClient("localhost",27017);
            DB db=mongo.getDB("microblog-filter");
            DBCollection coll=db.getCollection("tweets");

            BufferedReader br=new BufferedReader(new FileReader(new File("../data/filtering-qrels.txt")));
            HashMap <String,String> map=new HashMap<String, String>();
            int index=0;
            String line=null;
            while ((line=br.readLine())!=null){
                StringTokenizer st=new StringTokenizer(line);
                String tag=st.nextToken();
                st.nextToken();
                String id=st.nextToken();
                String rel=st.nextToken();
                BasicDBObject obj=(BasicDBObject)coll.findOne(new BasicDBObject("tweetid",id));
                if (Integer.parseInt(rel)>0 && obj!=null){
                    map.put(tag,id);
                }
            }
            for (int i=1;i<=49;i++){
                if (i%5==1 || i==18) continue;
                System.out.println(map.get(i+""));
            }
        }
        catch(Exception e){e.printStackTrace();}
    }
}
