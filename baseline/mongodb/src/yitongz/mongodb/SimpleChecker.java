package yitongz.mongodb;
import java.util.LinkedList;
import java.util.ArrayList;
public class SimpleChecker{
	LinkedList <Centroid> list=new LinkedList <Centroid>();
	private int size=5;
	private double threshold=0.2;
	private Query query;
	public SimpleChecker(Query q){
		query=q;
		ArrayList <Centroid> init_list=IndriSearcher.getTopDocs(q.num,size);
		for (Centroid c: init_list){
			if (c.tweet.vector!=null)
				list.add(c.clone());
		}
	}
	public boolean check(Tweet t){
		DocVector vv=null;
		boolean judge=false;
		for (Centroid c: list){
			if (vv==null)
				vv=c.tweet.vector.clone();
			else
				vv.add(c.tweet.vector);
		}
		if ( (t.score=vv.innerProduct_norm(t.vector)) > threshold ){
			judge=true;
			if (Facts.check(query.num,t.tweetid)){
				list.poll();	
				list.add(new Centroid(t));
			}
		}
		return judge;
	}
}