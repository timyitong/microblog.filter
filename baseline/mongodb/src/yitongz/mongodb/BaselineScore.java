package yitongz.mongodb;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.io.*;
import java.lang.Math;

public class BaselineScore implements RelevantScore{
	public static String indri_result="../../data/queryresult_default/";
	public static int k=10;

	protected Hashtable <String,ArrayList<Document> > map=new Hashtable <String, ArrayList<Document> >();
	protected Hashtable <String,Double> cuttoff_map=new Hashtable <String,Double>();

	public BaselineScore(){

	}

	public ArrayList <Document> init(Query q){
		ArrayList <Document> list=new ArrayList <Document> ();
		try{
		BufferedReader br=new BufferedReader(new FileReader(new File(indri_result+q.num+".out")));
		String line=null;

		map.put(q.num,list);

		int i=0;
		while ((line=br.readLine())!=null && i<k){
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
			score+=t.simScore(doc.tweet)*Math.exp(doc.indri_score);
			sum+=Math.exp(doc.indri_score);
		}
		score=score/sum;

		return score;
	}
	public double getCutoff(Tweet t, Query q){
		ArrayList<Document> list=map.get(q.num);
		if (list==null)
			list=init(q);

		Double cuttoff=cuttoff_map.get(q.num);
		if (cuttoff==null){
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
			//DO NOT CALCULATE THE SIM WITH ITSELF
			//sum=sum/(list.size()-1);

			//Try to figure out the weight, how much it should contain itself:
			sum=(sum+0.7)/(list.size());

			cuttoff=new Double(sum);
			cuttoff_map.put(q.num,cuttoff);
		}

		return (double) cuttoff;
	}

	class Document implements Comparable <Document> {
		double indri_score;
		Tweet tweet;
		double sim_score;
		/*Document will rank higher will higher sim score*/
		public int compareTo(Document d){
			if (this.sim_score>d.sim_score)
				return -1;
			else if (this.sim_score==d.sim_score)
				return 0;
			else
				return 1;
		}	
	}
}