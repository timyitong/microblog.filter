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
	public ArrayList <DocVector> vectors=new ArrayList <DocVector>();
	public ArrayList <Counter> counter_list=new ArrayList <Counter>();
	public double INIT_WEIGHT=Configure.INIT_WEIGHT;
	public int SLAVE_NUM=Configure.SLAVE_NUM;
	public double AUGUMENT=1;
	public int right=0;
	public int wrong=0;

	private Query query;
	private Centroid queryCentroid;

	public CentroidList (Query q){
		this.query=q;
		this.queryCentroid=new Centroid(new Tweet(q.tweetid));
		//System.out.println(q.num);
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
		if (Configure.ONLINE_TF){
			for (Centroid c: n_list){
				if (c.tweet!=null && c.tweet.clean_tweet!=null){
					OnlineWordStats.getInstance().register(c.tweet.clean_tweet);
				}
			}
		}

		//First init master:
		ArrayList<Centroid> l1=new ArrayList<Centroid>();
		collections.add(l1); // add the centroid list
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
		
		//add the DocVector
		DocVector vec=query.vector.clone();
		vec.multiply(Configure.ROCHHIO_A);
		DocVector v2=t.vector.clone();
		v2.multiply(Configure.ROCHHIO_B);
		vec.add(v2);
		vectors.add(vec);

		if (n_list!=null && n_list.size()>0){
			Centroid first_n=n_list.get(0);
			l1.add(first_n);
			first_n.relevant=false;
			first_n.tweet.score=Calculator.getInstance().sim.getSimScore(first_n.tweet,query,l1);
			first_n.tweet.score=Configure.FIXED_CUTOFF;
			counter.addNeg(first_n.tweet.score);
		}
		//Then init slaves:
		for (int i=0;i<SLAVE_NUM;i++){
			ArrayList<Centroid> l=new ArrayList<Centroid>();
			collections.add(l);
			Counter slave_counter=new Counter();
			counter_list.add(slave_counter);
			slave_counter.initWeight(0.1);

			DocVector vec1=query.vector.clone();
			vec1.multiply(Configure.ROCHHIO_A);
			if (n_list!=null && n_list.size()-i-1>0){
				Centroid slave_n=n_list.get(i+1);

				l.add(slave_n);
				slave_n.relevant=false;
				slave_n.tweet.score=slave_n.tweet.simScore(query);
				slave_counter.addNeg(slave_n.tweet.score);

				if (slave_n.tweet.clean_tweet!=null){
					//add DocVector
					DocVector vec2=slave_n.tweet.vector.clone();
					vec2.multiply(Configure.ROCHHIO_C);
					vec1.minus(vec2);
				}
			}
			vectors.add(vec1);
		}
	}
	public boolean checkIn(Centroid c){
		int count=0;
		int count_init=0;
		double score=0;
		double score_init=0;
		double w=0;
		double w_init=0;
		boolean [] adds=new boolean[collections.size()];
		//bagging the scores:
		for (int i=0;i<collections.size();i++){
			adds[i]=true;
			ArrayList <Centroid> list=collections.get(i);
			Counter counter=counter_list.get(i);
			//getSimScore: Tweet t, Query q, ArrayList<Centroid> list
			double ss=Calculator.getInstance().sim.getSimScore(c.tweet,query,list);
				//double ss=vectors.get(i).innerProduct_norm(c.tweet.vector);
			double ww=counter.cutoff();
			if (ss>ww)
				adds[i]=false;
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
		
		c.tweet.score=final_score;

		return add(c,adds);
	}
	public boolean add(Centroid c, boolean [] adds){
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
		
		//--finished bagging, final step, augment:
		score=score*AUGUMENT;
		//fixed score:
		if (score==0 || Configure.USE_FIXED_CUTOFF)
			score=Configure.FIXED_CUTOFF;
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
				/*
				for (int j=0;j<collections.size();j++){
					if (adds[j])
						train(j);
				}*/
				for (int j=0;j<collections.size();j++){
					int step=0;
					while (adds[j] && step<100){
						addPos(c,j);
						ArrayList <Centroid> list=collections.get(j);
						Counter counter=counter_list.get(j);
						//getSimScore: Tweet t, Query q, ArrayList<Centroid> list
						double ss=Calculator.getInstance().sim.getSimScore(c.tweet,query,list);
						double sq=Calculator.getInstance().sim.getSimScore(queryCentroid.tweet,query,list);
							//double ss=vectors.get(j).innerProduct_norm(c.tweet.vector);
							//double sq=vectors.get(j).innerProduct_norm(queryCentroid.tweet.vector);
						double ww=counter.cutoff();
						//System.out.println(ss-ww);
						if (ss>ww)
							adds[j]=false;
						step++;

						if (sq<=ww) // if query itself has been bended
							addPos(queryCentroid,j);
					}
				}

				// System.out.println(score);
				//Check facts and augment the pace
				if (Facts.check(query.num,c.tweet.tweetid)){
					right++;
					
					//perceptron training:
					// adds[] false means ss >  ww
					//		  true  means ss <= ww
					/*
					for (int j=0;j<collections.size();j++){
						int step=0;
						while (adds[j] && step<100){
							addPos(c,j);
							ArrayList <Centroid> list=collections.get(j);
							Counter counter=counter_list.get(j);
							//getSimScore: Tweet t, Query q, ArrayList<Centroid> list
							double ss=Calculator.getInstance().sim.getSimScore(c.tweet,query,list);
							double sq=Calculator.getInstance().sim.getSimScore(queryCentroid.tweet,query,list);
								//double ss=vectors.get(j).innerProduct_norm(c.tweet.vector);
								//double sq=vectors.get(j).innerProduct_norm(queryCentroid.tweet.vector);
							double ww=counter.cutoff();
							//System.out.println(ss-ww);
							if (ss>ww)
								adds[j]=false;
							step++;

							if (sq<=ww) // if query itself has been bended
								addPos(queryCentroid,j);
						}
					}
					*/
					
					
				}else{
					wrong++;
					//perceptron training:
					// adds[] false means ss >  ww
					//		  true  means ss <= ww
					/*
					for (int j=0;j<collections.size();j++){
						int step=0;
						while (!adds[j] && step<100){
							addPos(c,j);
							ArrayList <Centroid> list=collections.get(j);
							Counter counter=counter_list.get(j);
							//getSimScore: Tweet t, Query q, ArrayList<Centroid> list
							double ss=Calculator.getInstance().sim.getSimScore(c.tweet,query,list);
							double sq=Calculator.getInstance().sim.getSimScore(queryCentroid.tweet,query,list);
								//double ss=vectors.get(j).innerProduct_norm(c.tweet.vector);
								//double sq=vectors.get(j).innerProduct_norm(queryCentroid.tweet.vector);
							double ww=counter.cutoff();
							//System.out.println(ss-ww);
							if (ss<=ww)
								adds[j]=true;
							step++;

							if (sq<=ww) // if query itself has been bended
								addPos(queryCentroid,j);
						}
					}*/
					
				}
				double prec=right*1.0/(right+wrong);
				//System.out.println(prec);
				if ( (right+wrong)>=5 && prec< Configure.PREC_LIMIT ){
					AUGUMENT=AUGUMENT*Configure.AUGUMENT_PACE;
					//right=0;
					//wrong=0;
				} 

				return true;
			}else{
			//IRRELEVANT:
				addNeg(c);
				return false;
			}
		}
	}
	private void train(int index){
		ArrayList <Centroid> list=collections.get(index);
		DocVector vector=vectors.get(index);
		Counter counter=counter_list.get(index);

		for (int i=0;i<list.size();i++){
			Centroid c=list.get(i);
			boolean in=true;
			int step=0;
			while (in && step<20 && c.tweet.vector!=null){
				double ss=vector.innerProduct_norm(c.tweet.vector);
				double ww=counter.cutoff();
				in=false;
				if (c.relevant && ss<ww){
					in=true;
					addPos(c,index);
				}
				step++;
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
		else if (k>=0)
			addNeg(c,k);
	}
	private void addPos(Centroid c, int index){
		c.relevant=true;
		ArrayList<Centroid> list=collections.get(index);
		list.add(c);

		Counter counter=counter_list.get(index);
		counter.addPos(c.tweet.score);

		DocVector vec=vectors.get(index);
		DocVector new_v=c.tweet.vector.clone();
		new_v.multiply(Configure.ROCHHIO_B);
		vec.add(new_v);
	}
	private void addNeg(Centroid c, int index){
		c.relevant=false;
		ArrayList<Centroid> list=collections.get(index);
		list.add(c);

		Counter counter=counter_list.get(index);
		counter.addNeg(c.tweet.score);

		DocVector vec=vectors.get(index);
		DocVector new_v=c.tweet.vector.clone();
		new_v.multiply(Configure.ROCHHIO_C);
		vec.minus(new_v);
	}
}