package yitongz.mongodb;
//The Counter for any L0, L1, L2 stats counting needs
public class Counter{
	private static double PACE=2.5;

	int count=0;
	double rel_count=0;
	double rel_sum=0;
	double rel_sqr_sum=0;

	double ir_count=0;
	double ir_sum=0;
	double ir_sqr_sum=0;

	double init_weight=0;
	void addPos(double s){
		count++;
		
		rel_count++;
		rel_sum+=s;
		rel_sqr_sum+=s*s;
	}
	void addNeg(double s){
		count++;

		ir_count++;
		ir_sum+=s;
		ir_sqr_sum+=s*s;
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
		return (stdPos()+stdNeg())/2;
	}
	double stdPos(){
		double avg=avgPos();
		return Math.sqrt( 	(rel_sqr_sum-rel_count*avg*avg)/(rel_count-1) 	);
	}
	double stdNeg(){
		double avg=avgPos();
		return Math.sqrt( 	(rel_sqr_sum-rel_count*avg*avg)/(rel_count-1) 	);
	}
	double stdDiffPos(double s){
		if (rel_count<=1)
			return 999;

		double old_s=stdPos();
		addPos(s);
		double new_s=stdPos();
		removePos(s);
		return old_s-new_s;
	}
	double stdDiffNeg(double s){
		if (ir_count<=1)
			return 999;

		double old_s=stdNeg();
		addNeg(s);
		double new_s=stdNeg();
		removeNeg(s);
		return old_s-new_s;
	}
	double cutoff(){
		return (cutoffPos()+cutoffNeg())/2;
	}
	double cutoffNeg(int index){
		if (countNeg()==0)
			return 0;

		if (countNeg()==1)
			cutoff=avgNeg();
		else{
			double ratio=PACE*(countNeg()-1.0)/count;
			cutoff=avgNeg()+(ratio*stdNeg());
		}
		return cutoff;
	}
	double cutoffPos(int index){
		if (countPos()==0)
			return 0;

		if (countPos()==1)
			cutoff=avgPos();
		else{
			double ratio=PACE*(countPos()-1.0)/count;
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
		return this.init_weight;
	}
}