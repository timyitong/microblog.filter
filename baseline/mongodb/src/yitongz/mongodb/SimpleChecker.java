package yitongz.mongodb;
import java.util.LinkedList;
import java.util.ArrayList;
public class SimpleChecker{
	LinkedList <DocVector> list=new LinkedList <DocVector>();
	private int size=5;
	private double threshold=0.17;
	private Query query;
	public SimpleChecker(Query q){
		query=q;
		ArrayList <Centroid> init_list=IndriSearcher.getTopDocs(q.num,size-1);
		for (Centroid c: init_list){
			if (c.tweet.vector!=null)
				list.add(c.tweet.vector.clone());
		}
		list.add(query.vector.clone());
	}
	public boolean check(Tweet t){
		DocVector vv=null;
		boolean judge=false;
		for (DocVector v: list){
			if (vv==null)
				vv=v.clone();
			else
				vv.add(v);
		}
		if ( (t.score=vv.innerProduct_norm(t.vector)) > threshold ){
			judge=true;
			if (Facts.check(query.num,t.tweetid)){
				list.poll();	
				list.add(t.vector.clone());
			}
		}
		return judge;
	}
}