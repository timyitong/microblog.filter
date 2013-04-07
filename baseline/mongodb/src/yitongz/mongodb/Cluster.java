package yitongz.mongodb;
import java.util.ArrayList;
public class Cluster{
	public Query query;
	public ArrayList <Prototype> collectionsPos=new ArrayList <Prototype> ();
	public ArrayList <Prototype> collectionsNeg=new ArrayList <Prototype> ();

	public Cluster (Query q){
		query=q;
	}
}