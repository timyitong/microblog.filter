package yitongz.mongodb;

import java.util.LinkedList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.lang.Math;

public class RochhioSimScore implements SimScoreCalculator{
	private WordStats wordstats=WordStats.getInstance();

	public double getSimScore(Tweet t1, Tweet t2){
		if (t2.clean_tweet==null || t1.clean_tweet==null)
			return 0;

		int len1=t1.clean_tweet.length();
		int len2=t2.clean_tweet.length();

		Hashtable <String,Integer> tf_map1=new Hashtable <String,Integer>();
		Hashtable <String,Integer> tf_map2=new Hashtable <String,Integer>();
		LinkedList <String> l1=new LinkedList <String>();
		LinkedList <String> l2=new LinkedList <String>();

		/*Counting the term frequency*/
		StringTokenizer st=new StringTokenizer(t1.clean_tweet);
		while (st.hasMoreTokens()){
			String word=st.nextToken();
			Integer tf=tf_map1.get(word);
			if (tf==null){
				tf_map1.put(word,new Integer(1));
				l1.add(word);
			}else{
				tf++;
			}
		}

		st=new StringTokenizer(t2.clean_tweet);
		while (st.hasMoreTokens()){
			String word=st.nextToken();
			Integer tf=tf_map2.get(word);
			if (tf==null){
				tf_map2.put(word,new Integer(1));
				l2.add(word);
			}else{
				tf++;
			}
		}

		/*Calculate the cosine similarity score*/
		double sim=0;
		double mod1=0;
		double mod2=0;
		for (String w : l1){
			Integer tf1=tf_map1.get(w);
			double wtf1=getWTF(tf1,len1,w);

			mod1+=wtf1*wtf1;

			Integer tf2=tf_map2.get(w);
			double wtf2=0;
			if (tf2!=null)
				wtf2=getWTF(tf2,len2,w);
			sim+=wtf1*wtf2;
		}
		for (String w: l2){
			Integer tf2=tf_map2.get(w);

			double wtf2=0;
			if (tf2!=null)
				wtf2=getWTF(tf2,len2,w);

			mod2+=wtf2*wtf2;
		}
		mod1=Math.sqrt(mod1);
		mod2=Math.sqrt(mod2);

		sim=sim/(mod1*mod2);

		return sim;
	}
	private double getWTF(int tf, int doc_len, String word ){
		double miu=20;
		double lambda=0.9;

		double ctf=(double)wordstats.getTF(word);

		double c_len=wordstats.getTC();

		double idf=ctf/c_len;
		double score=lambda*(tf+miu*idf)/(doc_len+miu)+(1-lambda)*idf;
		return score;
	}
	public double getSimScore(Tweet t, Query q, ArrayList <Centroid> list){
		if (t.clean_tweet==null)
			return 0;

		double a=1,b=0.8,c=0.1;
		DocVector d2_rel=new DocVector();
		DocVector d2_ir=new DocVector();
		int count_rel=0;
		int count_ir=0;
		if (list!=null){
			for (Centroid cent :list){
				if (cent.tweet.vector==null)
					continue;
				if (cent.relevant){
					d2_rel.add(cent.tweet.vector);
					count_rel++;
				}else{
					d2_ir.add(cent.tweet.vector);
					count_ir++;
				}	
			}
		}
		//We do not use the typical Rochhio para setting
		//d2_rel.multiply(b/count_rel);
		//d2_ir.multiply(c/count_ir);
		d2_rel.multiply(1);
		d2_ir.multiply(1);

		DocVector d2=new DocVector();
		d2.add(q.vector);
		d2.multiply(a);
		d2.add(d2_rel);
		d2.minus(d2_ir);

		return d2.innerProduct(t.vector)/(d2.mod()*t.vector.mod());
	}
}