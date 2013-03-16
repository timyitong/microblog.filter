package yitongz.mongodb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Iterator;

/*
This class read in all the tweets needed to be made a decision about and return iterators for operation
*/

public class TweetForCheckList{
	public static String list_url="../../data/filtering-qrels.txt";
	public static Hashtable <Integer, ArrayList<String> > map=null;
	private TweetForCheckList(){}
	private static void init(){
		try{
		map=new Hashtable <Integer, ArrayList<String> >();
		BufferedReader br=new BufferedReader(new FileReader(new File(list_url)));
		String line=null;
		while ((line=br.readLine())!=null){
			StringTokenizer st=new StringTokenizer(line);
			String query_id=st.nextToken();
			st.nextToken();
			String tweet_id=st.nextToken();
			ArrayList <String> list=map.get(new Integer(query_id));
			if (list==null){
				list=new ArrayList <String>(); 
				map.put(new Integer(query_id),list);
			}
			list.add(tweet_id);
		}
		br.close();
		}catch(Exception e){e.printStackTrace();}
	}
	public static Iterator<String> iterator(int query_id){
		if (map==null)
			init();
		if (map.get(query_id)==null)
			System.out.println(query_id+"::shit");
		return map.get(query_id).iterator(); 
	}
}