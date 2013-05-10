package yitongz.mongodb;
//The Counter for any L0, L1, L2 stats counting needs
public class Counter{
	public double PACE=Configure.PACE;

	int count=0;

	int rel_count=0;
	double rel_sum=0;
	double rel_sqr_sum=0;

	int ir_count=0;
	double ir_sum=0;
	double ir_sqr_sum=0;

	String firstPos=null;
	double init_weight=0;

	public String toString(){
		String s="count: "+count+" rel:"+rel_count+" ir:"+ir_count+"\n"
				+"sum: rel "+rel_sum+" ir "+ir_sum+"\n"
				+"sqr: rel "+rel_sqr_sum+" ir "+ir_sqr_sum;
		return s;
	}
	void merge(Counter c){
		count+=c.count;
		
		rel_count+=c.rel_count;
		rel_sum+=c.rel_sum;
		rel_sqr_sum+=c.rel_sqr_sum;

		ir_count+=c.ir_count;
		ir_sum+=c.ir_sum;
		ir_sqr_sum+=c.ir_sqr_sum;
		
	}
	void firstPos(String s){
		firstPos=s;
	}
	String firstPos(){
		return firstPos;
	}
	void addPos(double s){
		count++;
		
		rel_count++;
		rel_sum+=s;
		rel_sqr_sum+=s*s;
	}
	void addNeg(double s){
		//if (ir_count*1.0/count <=0.9){
		count++;

		ir_count++;
		ir_sum+=s;
		ir_sqr_sum+=s*s;
		//}
	}
	void removePos(double s){
		count--;
		rel_count--;
		rel_sum-=s;
		rel_sqr_sum-=s*s;
	}
	void removeNeg(double s){
		count--;
		ir_count--;
		ir_sum-=s;
		ir_sqr_sum-=s*s;
	}
	int count(){
		return count;
	}
	int countPos(){
		return rel_count;
	}
	int countNeg(){
		return ir_count;
	}
	double avgPos(){
		return rel_sum/rel_count;
	}
	double avgNeg(){
		return ir_sum/ir_count;
	}
	double std(){
		//TODO average std may not be the best way
		return (0.4125*(rel_count-1)*stdPos()/count+0.8625*(ir_count-1)*stdNeg()/count)/2;
	}
	double stdPos(){
		double avg=avgPos();
		return Math.sqrt( 	(rel_sqr_sum-rel_count*avg*avg)/(rel_count-1) 	);
	}
	double stdNeg(){
		double avg=avgNeg();
		return Math.sqrt( 	(ir_sqr_sum-ir_count*avg*avg)/(ir_count-1) 	);
	}
	double stdDiffPos(double s){
		if (rel_count<=2)
			return 999;

		double old_s=stdPos();
		double old_u=avgPos();
		addPos(s);
		double new_s=stdPos();
		double new_u=avgPos();
		removePos(s);
		return rel_count/count*(old_s-new_s);
	}
	double stdDiffNeg(double s){
		if (ir_count<=2)
			return 999;

		double old_s=stdNeg();
		double old_u=avgNeg();
		addNeg(s);
		double new_s=stdNeg();
		double new_u=avgNeg();
		removeNeg(s);
		return ir_count/count*(old_s-new_s);
	}
	double cutoff(){
		double cut;
		double s1=stdPos();
		double s2=stdNeg();
		double u1=avgPos();
		double u2=avgNeg();
		if (false){

			cut = (u1/s1+u2*ir_count/s2)/(1/s1+ir_count/s2);
		}else{
			cut= (cutoffPos()+cutoffNeg())/2;

			//avg threshold
			/*
			if (rel_count==0)
				cut=u2;
			else if (ir_count==0)
				cut=u1;
			else
				cut=(u1+u2)/2;
			*/
		}
		return cut;
	}
	double cutoffNeg(){
		if (countNeg()==0)
			return 0;

		double cutoff;
		if (countNeg()==1)
			cutoff=avgNeg();
		else{
			double ratio=0.55*PACE*(countNeg()-1.0)/count;
			cutoff=avgNeg()+(ratio*stdNeg());
		}
		return cutoff;
	}
	double cutoffPos(){
		if (countPos()==0)
			return 0;

		double cutoff;
		if (countPos()==1)
			cutoff=avgPos();
		else{
			double ratio=-1.15*PACE*(countPos()-1.0)/count;
			cutoff=avgPos()+(ratio*stdPos());
		}
		return cutoff;
	}
	boolean hasStd(){
		if (rel_count>=2 && ir_count>=2)
			return true;
		else
			return false;
	}
	void initWeight(double w){
		this.init_weight=w;
	}
	double initWeight(){
		//double r=0.1;
		//return (1-r)*avgPos()+r*avgNeg();
		return this.init_weight;
	}
	double svmcutoff(){
		double score;
		return 0;
	}
}