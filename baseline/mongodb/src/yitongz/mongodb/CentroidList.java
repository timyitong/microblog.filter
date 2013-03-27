package yitongz.mongodb;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.math.BigInteger;
import java.io.*;

public class CentroidList{
	public ArrayList <ArrayList<Centroid>> collections=new ArrayList <ArrayList<Centroid>> ();

	private Query query;

	private static double pace=2.5;
	//used to calculate the positive cutoff
	public HashMap <Integer,Counter> pos_counters = new HashMap <Integer,Counter>();
	//used to calculate the negative cutoff
	public HashMap <Integer,Counter> neg_counters = new HashMap <Integer,Counter>();

	public CentroidList (Query q){
		this.query=q;
		
		CentroidFactory factory=new CentroidFactory(this);
		
		ArrayList<Centroid> n_list=IndriSearcher.getTopDocs(this.query.num,1);
		factory.addNeg(n_list);
		factory.submit();
	}
	public double getScore(Centroid c){
		double score=0;
		for (int i=0;i<collections.size();i++){
			ArrayList <Centroid> list=collections.get(i);
			//getSimScore: Tweet t, Query q, ArrayList<Centroid> list
			score+=Calculator.getInstance().sim.getSimScore(c.tweet,this,list);
		}
		//bagging the scores:
		score=score/collections.size();
		return score;
	}
	public boolean add(Centroid c){
		double diff=9999;
		int possible_index=-1;
		double score=0;
		for (int i=0;i<collections.size();i++){
			double cutoff=avg_cutoff(i);
			score+=cutoff;
			double new_diff=Math.abs(c.tweet.score-cutoff);
			if (new_diff<diff){
				diff=new_diff;
				possible_index=i;
			}
		}
		//bagging the  cutoff scores
		/*NOTICE: maybe not necessarily we need to bag the cutoff*/
		score=score/collections.size();
		boolean judge= (c.tweet.score>score);

		int check=first_pos_id.compareTo(new BigInteger(c.tweet.tweetid));
		if (check>0){
			//it is before the first rel tweet, definitely is negative one
			addNeg(c,possible_index);
			return false;
		}else if (check==0){
			//This is VERY SPECIAL SITUATION BECAUSE THIS TWEET is already added
			return true;
		}else{
			if (judge){
			//RELEVANT:
				addPos(c,possible_index);
				return true;
			}else{
			//IRRELEVANT:
				addNeg(c,possible_index);
				return false;
			}
		}
	}
	private void addPos(Centroid c, int index){
		c.relevant=true;
		ArrayList<Centroid> list=collections.get(index);
		list.add(c);

		Counter counter=pos_counters.get(index);
		counter.add(c.tweet.score);
	}
	private void addNeg(Centroid c, int index){
		c.relevant=false;
		ArrayList<Centroid> list=collections.get(index);
		list.add(c);

		Counter counter=neg_counters.get(index);
		counter.add(c.tweet.score);
	}
	private double avg_cutoff(int index){
		return (pos_cutoff(index)+neg_cutoff(index))/2;
	}
	private double neg_cutoff(int index){
		Counter counter=neg_counters.get(index);
		int count=couter.count()+pos_counters.get(index).count();
		double avg=counter.avg();
		double cutoff=0;
		if (counter.count()==1)
			cutoff=avg;
		else{
			double ratio=pace*(counter.count()-1.0)/count;
			cutoff=avg+(ratio*counter.std());
		}
		return cutoff;
	}
	private double pos_cutoff(int index){
		Counter counter=pos_counters.get(index);
		int count=couter.count()+neg_counters.get(index).count();
		double avg=counter.avg();
		double cutoff=0;
		if (counter.count()==1)
			cutoff=avg;
		else{
			double ratio=pace*(counter.count()-1.0)/count;
			cutoff=avg-(ratio*counter.std());
		}
		return cutoff;
	}
}