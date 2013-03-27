package yitongz.mongodb;
public class CentroidFactory{
	private ArrayList <Centroid> list=new ArrayList <Centroid> ();
	private CentroidList target;
	private Counter pos_counter=new Counter();
	private Counter neg_counter=new Counter();
	public CentroidFactory(CentroidList l){
		target=l;
	}
	public void addPos(Centroid c){

	}
	public void addNeg(Centroid c){

	}
	public void addPos(ArrayList<Centroid> l){
		for (Centroid c:l){
			addPos(c);
		}
	}
	public void addNeg(ArrayList<Centroid> l){
		for (Centroid c:l){
			addNeg(c);
		}
	}
	public void submit(){
		int index=target.collections.size();
		target.collections.add(list);
		
		target.pos_counters.put(index,pos_counter);
		target.neg_counters.put(index,neg_counter);
	}
}