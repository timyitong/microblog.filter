package yitongz.mongodb;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.math.BigInteger;
import java.io.*;

public class CentroidList{
	private ArrayList <Centroid> centroid_list;
	private Query query;
	private String first_pos;
	private BigInteger first_pos_id;
	private int k=10;
	private static boolean RESET_TOP=false;
	private static HashMap < String,ArrayList<Centroid> > irmap=null;
	private static int TOP_IR=1;

	private int count=0;
	private double fixed_ratio=2.25;
	//used to calculate the positive cutoff
	private int rel_count=0;
	private double rel_score_sum=0;
	private double rel_score_sqr_sum=0;

	//used to calculate the negative cutoff
	private int ir_count=0;
	private double ir_score_sum=0;
	private double ir_score_sqr_sum=0;

	public CentroidList (Query q){
		first_pos=q.tweetid;
		first_pos_id=new BigInteger(q.tweetid);
		this.query=q;
		centroid_list=new ArrayList <Centroid>();
		Tweet query_tweet=new Tweet(q.tweetid);
		if (query_tweet.tweetid==null)
			System.out.println(q.tweetid);
		Centroid first_one=new Centroid(query_tweet);
		first_one.score=first_one.tweet.simScore(query);
		addPos(first_one);

		getTopIREL();
	}
	public ArrayList <Centroid> getList(){
		return this.centroid_list;
	}
	private void getTopIREL(){
		try{
			if (RESET_TOP){ // IF we need reset
				String command="sh runquery.sh temp.txt "+TOP_IR+" "+Configure.INV_LIST_FOLDER+query.num;
				writeQueryFile(query.words);
				
				Process process=Runtime.getRuntime().exec(command);
				BufferedReader br = new BufferedReader(new InputStreamReader(
			                    process.getInputStream()));
				process.waitFor();

				int j=0;
				while (br.ready() && j<TOP_IR){
					String s=br.readLine();
					StringTokenizer st=new StringTokenizer(s);
					st.nextToken();
					st.nextToken();
					String tid=st.nextToken(); // tweetid
					if (irmap==null){
						irmap=new HashMap < String,ArrayList<Centroid> > ();
					}
					ArrayList <Centroid> list=irmap.get(query.num);
					if (list==null){
						list=new ArrayList<Centroid>();
						irmap.put(query.num,list);
					}
					list.add(new Centroid(new Tweet(tid)));
					System.out.println(query.num+" "+tid);
					j++;
				}
			}else{ //IF we do not need reset
				if (irmap==null){ // if not read in yet
					irmap=new HashMap < String,ArrayList<Centroid> > ();
					BufferedReader br=new BufferedReader(new FileReader(new File(Configure.TOPIREL_FILE)));
					String line=null;
					while ((line=br.readLine())!=null){
						StringTokenizer st=new StringTokenizer(line);
						String tag=st.nextToken();
						String tweetid=st.nextToken();
						ArrayList<Centroid> list=irmap.get(tag);
						if (list==null){
							list=new ArrayList<Centroid>();
							irmap.put(tag,list);
						}
						list.add(  new Centroid(new Tweet(tweetid)) );
					}
				}
			}
			ArrayList<Centroid> list=irmap.get(query.num);
			if (list!=null){
				for (Centroid c: list){
					c.score=c.tweet.simScore(this.query);
					c.relevant=false;
				}
				Collections.sort(list);
				int i=0;
				for (Centroid c: list){
					if (i>=TOP_IR) break;
					centroid_list.add(c);
					i++;
				}
			}																				
		}catch(Exception e){e.printStackTrace();}
	}
	public boolean add(Centroid c){
		int check=first_pos_id.compareTo(new BigInteger(c.tweet.tweetid));
		if (check>0){
			//it is before the first rel tweet, definitely is negative one
			addNeg(c);
			return false;
		}else if (check==0){
			//during initialization, we have already added the first tweet
			return true;
		}else{
			double pos_cutoff=this.cutoff();
			double neg_cutoff=this.neg_cutoff();
			//System.out.println(pos_cutoff+"::"+neg_cutoff);
			if (c.tweet.score>(pos_cutoff+neg_cutoff)/2){
				addPos(c);
				return true;
			}else{
				addNeg(c);
				return false;
			}
		}
	}
	private void addPos(Centroid c){
		c.relevant=true;
		centroid_list.add(c);
		rel_score_sqr_sum+=c.tweet.score*c.tweet.score;
		rel_score_sum+=c.tweet.score;
		rel_count++;
		count++;
	}
	private void addNeg(Centroid c){
		c.relevant=false;
		centroid_list.add(c);
		ir_score_sqr_sum+=c.tweet.score*c.tweet.score;
		ir_score_sum+=c.tweet.score;
		ir_count++;
		count++;
	}
	private double neg_cutoff(){
		double avg=ir_score_sum/ir_count;
		double cutoff=0;
		if (ir_count==1)
			cutoff=avg;
		else{
			double ratio=fixed_ratio*(ir_count-1.0)/count;
			cutoff=avg+(ratio*Math.sqrt(    (ir_score_sqr_sum-avg*avg*ir_count) /  (ir_count-1)   ) );
		}
		return cutoff;
	}
	private double cutoff(){
		double avg=rel_score_sum/rel_count;
		double cutoff=0;
		if (rel_count==1)
			cutoff=avg;
		else{
			double ratio=fixed_ratio*(rel_count-1.0)/count;
			cutoff=avg-(ratio*Math.sqrt(    (rel_score_sqr_sum-avg*avg*rel_count) /  (rel_count-1)   ) );
		}
		return cutoff;
	}
	private void writeQueryFile(String s){
		try{
			s=s.replaceAll("[^A-Za-z0-9\\s]","").trim();
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File("temp.txt")));
			bw.write("");
			bw.write("<parameters><query><number>000</number><text>");
			bw.newLine();
			bw.write(s);
			bw.newLine();
			bw.write("</text></query></parameters>");
			bw.newLine();

			bw.close();
		}catch(Exception e){e.printStackTrace();}
	}
}