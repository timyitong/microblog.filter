package yitongz.mongodb;
public class Prototype{
	DocVector center=new DocVector();
	DocVector r_center=new DocVector();
	DocVector ir_center=new DocVector();

	Counter counter=new Counter();
	Query query;
	ArrayList <Centroid> rel_list=new ArrayList <Centroid> ();
	ArrayList <Centroid> ir_list=new ArrayList <Centroid> ();
	public Prototype(Query q){
		query=q;
	}
	public DocVector getCenter(){
		return center;
	}
	public double sim(Prototype p){
		DocVector c2=p.getCenter();
		return this.center.innerProduct(c2)/(this.center.mod()*c2.mod());
	}
	public void merge(Prototype p){
		//add all vectors into current prototype
		for (Centroid c: p.rel_list){
			rel_list.add(c);
		}
		for (Centroid c: p.ir_list){
			ir_list.add(c);
		}
		//merge the center:
		this.r_center=this.r_center.add(p.r_center);
		this.ir_center=this.ir_center.add(p.ir_center);
		//merge the counter
		this.counter.merge(p.counter);
		//recalculate the original center:
		calculateCenter();
	}
	public void calculateCenter(){
		center=new DocVector();
		DocVector r_vec=new DocVector();
		DocVector ir_vec=new DocVector();
		center.add(query.vector);
		center.multiply(Configure.ROCHHIO_A);
		r_vec.add(r_center);
		r_vec.multiply(Configure.ROCHHIO_B);
		ir_vec.add(ir_center);
		ir_vec.multiply(Configure.ROCHHIO_C);
		center.add(r_vec);
		center.minus(ir_vec);
	}
}