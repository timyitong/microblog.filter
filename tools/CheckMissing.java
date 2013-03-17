/*
	This class is used to check how many tweets have been missing from the corpus
*/
import java.io.*;
import java.util.*;

public class CheckMissing{
	private String missing_url="../data/missingtweets.txt";
	private String check_url="../data/filtering-qrels.txt";
	public static void main(String argv[]){
		new CheckMissing();
	}
	public CheckMissing(){
		run();
	}
	private void run(){
		try{
			BufferedReader br_missing=new BufferedReader(new FileReader(new File(missing_url)));
			BufferedReader br_check=new BufferedReader(new FileReader(new File(check_url)));

			String tag=null;
			int missing_rel=0;
			int missing_irel=0;
			int total_rel=0;
			int total=0;

			String line=null;
			String current_mis_tweet=getTID(br_missing.readLine());

			while (current_mis_tweet!=null && (line=br_check.readLine())!=null){
				StringTokenizer st=new StringTokenizer(line);
				String new_tag=st.nextToken();
				st.nextToken();
				String tid=st.nextToken();
				boolean rel= Integer.parseInt(st.nextToken()) >0 ? true : false;

				if (tag==null){
					tag=new_tag;
				}else if (!new_tag.equals(tag)){
					System.out.println("tag: "+tag);
					System.out.println("missing_rel: "+missing_rel);
					System.out.println("total_rel:"+total_rel);
					System.out.println("missing_irel: "+missing_irel);
					System.out.println("total_missing: "+(missing_rel+missing_irel));
					System.out.println("total: "+total+"\n");

					tag=new_tag;
					missing_irel=0;
					missing_rel=0;
					total=0;
					total_rel=0;
				}

				if (tid.equals(current_mis_tweet)){
					if (rel){
						missing_rel++;
					}else{
						missing_irel++;
					}
					current_mis_tweet=getTID(br_missing.readLine());
				}
				if (rel)
					total_rel++;

				total++;
			}

			//final tag:
			System.out.println("tag: "+tag);
			System.out.println("missing_rel: "+missing_rel);
			System.out.println("total_rel:"+total_rel);
			System.out.println("missing_irel: "+missing_irel);
			System.out.println("total_missing: "+(missing_rel+missing_irel));
			System.out.println("total: "+total+"\n");

		}catch(Exception e){e.printStackTrace();}
	}
	private String getTID(String s){
		if (s==null)
			return null;

		StringTokenizer st=new StringTokenizer(s);
		st.nextToken();
		st.nextToken();
		return st.nextToken();
	}
}