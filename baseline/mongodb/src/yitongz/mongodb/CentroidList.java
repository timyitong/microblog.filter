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
	public ArrayList <Counter> counter_list=new ArrayList <Counter>();
	public double INIT_WEIGHT=Configure.INIT_WEIGHT;
	public int SLAVE_NUM=Configure.SLAVE_NUM;


	private Query query;

	public CentroidList (Query q){
		this.query=q;
		
		init_MasterSlave();
	}
	public String toString(){
		StringBuilder sb=new StringBuilder();
		int i=1;
		sb.append("====");
		for (ArrayList <Centroid> list: collections){
			sb.append("Seed:"+i+"\n");
			for (Centroid c: list){
				sb.append("1:"+c.tweet.tweetid+" score:"+c.tweet.score+" | tweet.relevant: "+c.tweet.relevant+" | cent.relevant: "+c.relevant+"\n");
			}
			i++;
		}
		return sb.toString();
	}
	private void init_MasterSlave(){
		ArrayList<Centroid> n_list=IndriSearcher.getTopDocs(this.query.num,SLAVE_NUM+1);
		
		//First init master:
		ArrayList<Centroid> l1=new ArrayList<Centroid>();
		collections.add(l1);
		Counter counter=new Counter();
		counter_list.add(counter);
		counter.initWeight(0.9);
		
		Tweet t=new Tweet(query.tweetid);
		t.relevant=true;
		Centroid first_p=new Centroid(t);
		l1.add(first_p);
		first_p.relevant=true;
		first_p.tweet.score=first_p.tweet.simScore(query);
		counter.addPos(first_p.tweet.score);
		
		if (n_list!=null && n_list.size()>0){
			Centroid first_n=n_list.get(0);
			l1.add(first_n);
			first_n.relevant=false;
			//first_n.tweet.score=first_n.tweet.simScore(query);
			first_n.tweet.score=Calculator.getInstance().sim.getSimScore(first_n.tweet,query,l1);
			counter.addNeg(first_n.tweet.score);
		}
		//Then init slaves:
		for (int i=0;i<SLAVE_NUM;i++){
			ArrayList<Centroid> l=new ArrayList<Centroid>();
			collections.add(l);
			Counter slave_counter=new Counter();
			counter_list.add(slave_counter);
			slave_counter.initWeight(0.1);

			if (n_list!=null && n_list.size()-i-1>0){
				Centroid slave_n=n_list.get(i+1);
				l.add(slave_n);
				slave_n.relevant=false;
				slave_n.tweet.score=slave_n.tweet.simScore(query);
				slave_counter.addNeg(slave_n.tweet.score);
			}
		}
	}
	public double getScore(Centroid c){
		int count=0;
		int count_init=0;
		double score=0;
		double score_init=0;
		double w=0;
		double w_init=0;
		//bagging the scores:
		for (int i=0;i<collections.size();i++){
			ArrayList <Centroid> list=collections.get(i);
			Counter counter=counter_list.get(i);
			//getSimScore: Tweet t, Query q, ArrayList<Centroid> list
			double ss=Calculator.getInstance().sim.getSimScore(c.tweet,query,list);
			double ww=counter.cutoff();
			w+=ww;
			score+=ww*ss;
			count++;
			/*
			if (counter.hasStd()){
				double ww=counter.cutoff();
				w+=ww;
				score+=ww*ss;
				count++;
			}else{
				double ww=counter.initWeight();
				w_init+=ww;
				score_init+=ww*ss;
				count_init++;
			}*/
		}
		if (w!=0)
			score=score/w;
		if (w_init!=0)
			score_init=score_init/w_init;
		//double final_score=(count*score+count_init*score_init)/(count+count_init);
		double final_score=0;
		if (w!=0 && w_init!=0)
			final_score=(1-INIT_WEIGHT)*score+INIT_WEIGHT*score_init;
		else if (w==0)
			final_score=score_init;
		else if (w_init==0)
			final_score=score;
		
		return final_score;
	}
	public boolean add(Centroid c){
		int count=0;
		int count_init=0;
		double score=0;
		double score_init=0;
		double w=0;
		double w_init=0;
		//bagging the  cutoff scores
		for (int i=0;i<collections.size();i++){
			Counter counter=counter_list.get(i);
			double ss=counter.cutoff();
			if (counter.hasStd()){
				double ww=Math.exp(-counter.std());
				w+=ww;
				score+=ww*ss;
				count++;
			}else{
				double ww=counter.initWeight();
				w_init+=ww;
				score_init+=ww*ss;
				count_init++;
			}
		}
		if (w!=0)
			score=score/w;
		if (w_init!=0)
			score_init=score_init/w_init;
		//double final_cutoff=(count*score+count_init*score_init)/(count+count_init);
		double final_cutoff=(1-INIT_WEIGHT)*score+INIT_WEIGHT*score_init;
		
		//--finished bagging

		//--Get the Judgement
		boolean judge = (c.tweet.score>score);

		//CHECK whether this query is too early
		int check=new BigInteger(query.tweetid).compareTo(new BigInteger(c.tweet.tweetid));
		if (check>0){
			//it is before the first rel tweet, definitely is negative one
			addNeg(c);
			return false;
		}else if (check==0){
			//This is VERY SPECIAL SITUATION BECAUSE THIS TWEET is already added
			return true;
		}else{
			if (judge){
			//RELEVANT:
				addPos(c);
				return true;
			}else{
			//IRRELEVANT:
				addNeg(c);
				return false;
			}
		}
	}
	//Still need to check which one to add
	private void addPos(Centroid c){
		int k_init=-1;
		double diff_init=-999;
		int k=-1;
		double diff=-999;
		for (int i=0;i<collections.size();i++){
			Counter counter=counter_list.get(i);
			double ss=counter.stdDiffPos(c.tweet.score);
			if (counter.hasStd()){
				if (ss>diff){
					k=i;
					diff=ss;
				}
			}else{
				if (ss>diff_init){
					k_init=i;
					diff_init=ss;
				}
			}
		}
		if (k_init>=0)
			addPos(c,k_init);
		else
			addPos(c,k);
	}
	private void addNeg(Centroid c){
		int k_init=-1;
		double diff_init=-999;
		int k=-1;
		double diff=-999;
		for (int i=0;i<collections.size();i++){
			Counter counter=counter_list.get(i);
			double ss=counter.stdDiffNeg(c.tweet.score);
			if (counter.hasStd()){
				if (ss>diff){
					k=i;
					diff=ss;
				}
			}else{
				if (ss>diff_init){
					k_init=i;
					diff_init=ss;
				}
			}
		}
		if (k_init>=0)
			addNeg(c,k_init);
		else
			addNeg(c,k);
	}
	private void addPos(Centroid c, int index){
		c.relevant=true;
		ArrayList<Centroid> list=collections.get(index);
		list.add(c);

		Counter counter=counter_list.get(index);
		counter.addPos(c.tweet.score);
	}
	private void addNeg(Centroid c, int index){
		c.relevant=false;
		ArrayList<Centroid> list=collections.get(index);
		list.add(c);

		Counter counter=counter_list.get(index);
		counter.addNeg(c.tweet.score);
	}
}