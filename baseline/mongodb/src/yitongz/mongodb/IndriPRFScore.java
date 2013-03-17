package yitongz.mongodb;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Collections;
import java.util.StringTokenizer;
import java.io.*;
import java.lang.Math;

public class IndriPRFScore extends BaselineScore {
	public static String indri_result="../../data/queryresult_prf/";
	public static int doc_len=1000;

	public ArrayList <Document> init(Query q){
		ArrayList <Document> list=new ArrayList <Document> ();
		try{
		BufferedReader br=new BufferedReader(new FileReader(new File(indri_result+q.num+".out")));
		String line=null;

		map.put(q.num,list);

		int i=0;

		//here instead of read in top k documents, I read in all
		while ((line=br.readLine())!=null && i<doc_len){
			StringTokenizer st=new StringTokenizer(line);
			st.nextToken();
			st.nextToken();

			Tweet tweet=new Tweet(st.nextToken());
			Document doc=new Document();
			doc.tweet=tweet;

			st.nextToken();
			doc.indri_score=Double.parseDouble(st.nextToken());

			list.add(doc);
			i++;
		}

		}catch(Exception e){e.printStackTrace();}

		return list;
	}

	public double getScore(Tweet t, Query q){
		ArrayList<Document> list=map.get(q.num);
		if (list==null)
			list=init(q);

		double score=0;
		double sum=0;

		for (Document doc : list){
			doc.sim_score=t.simScore(doc.tweet);
		}

		Collections.sort(list);

		int i=0;
		for (Document doc : list){
			if (i>=k)
				break;
			score+=doc.sim_score*Math.exp(doc.indri_score);
			sum+=Math.exp(doc.indri_score);
			i++;
		}
		score=score/sum;
		//System.out.println(score);

		return score;
	}
	public double getCutoff(Tweet t, Query q){
		ArrayList<Document> list=map.get(q.num);
		if (list==null)
			list=init(q);

		Double cuttoff=cuttoff_map.get(q.num);
		if (cuttoff==null){
			//@overide
			int end=Math.min(k,list.size());
			list=new ArrayList <Document>(list.subList(0,end));

			double sum=0;
			for (Document doc : list){
				double score=0;
				double tmp_sum=0;

				for (Document d : list){
					//DO NOT CALCULATE THE SIM WITH ITSELF
					if (d.tweet.tweetid.equals(doc.tweet.tweetid))
						continue;
					score+=doc.tweet.simScore(d.tweet)*Math.exp(d.indri_score);
					tmp_sum+=Math.exp(d.indri_score);
				}
				score=score/tmp_sum;
				sum+=score;
			}
			/* Remember current configuration: length-1, not using sum*1.5
			 * We can get a recal of 0.77
			 */
			//DO NOT CALCULATE THE SIM WITH ITSELF
			sum=sum/(list.size()-1);

			//Try to figure out the weight, how much it should contain itself:
			//sum=(sum+0.7)/(list.size());	

			//sum=sum*1.5;

			cuttoff=new Double(sum);
			cuttoff_map.put(q.num,cuttoff);
		}

		return (double) cuttoff;
	}
}