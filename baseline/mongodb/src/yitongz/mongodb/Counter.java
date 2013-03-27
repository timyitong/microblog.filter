package yitongz.mongodb;
//The Counter for any L0, L1, L2 stats counting needs
public class Counter{
	int count=0;
	double sum=0;
	double sqr_sum=0;
	void add(double s){
		count++;
		sum+=s;
		sqr_sum+=s*s;
	}
	void remove(double s){
		count--;
		sum-=s;
		sqr_sum-=s*s;
	}
	int count(){
		return count;
	}
	double avg(){
		return sum/count;
	}
	double std(){
		double avg=avg();
		return Math.sqrt( 	(sqr_sum-count*avg*avg)/(count-1) 	);
	}
}