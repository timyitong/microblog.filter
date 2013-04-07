package yitongz.mongodb;
import java.util.ArrayList;
import java.io.*;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.Collections;
import java.util.Map;
public class IndriSearcher{
	public static boolean RESET_TOP=false;
	private static HashMap <String,ArrayList<Centroid> > doc_map=null;
	private static int TOP_IR=1000;
	public static DocVector getExpandedVector(String query_num,String words){
		load();

		DocVector result=new DocVector();
		TreeMap <String,Integer> map=new TreeMap <String,Integer> ();
		ArrayList <Centroid> list=doc_map.get(query_num);
		for (int i=0;i<10 && i<list.size(); i++){
			Tweet t=list.get(i).tweet;
			if (t==null || t.clean_tweet==null)
				continue;
			StringTokenizer st=new StringTokenizer(t.clean_tweet);
			while (st.hasMoreTokens()){
				String word=st.nextToken();
				Integer tf=map.get(word);
				if (tf==null)
					tf=new Integer(0);
				map.put(word,tf);
			}
		}
		ArrayList <Word> ll=new ArrayList <Word> ();
		for (Map.Entry<String,Integer> entry : map.entrySet() ) {
			Integer tf=entry.getValue();
			String 	w=entry.getKey();
			Word word=new Word(w,tf);
			ll.add(word);
		}
		Collections.sort(ll);
		StringBuilder sb=new StringBuilder();
		for (int i=0;i<=3;i++){
			sb.append(" "+ll.get(i).word);
		}
		result=new DocVector(sb.toString());
		result.multiply(Configure.EXPAND_QUERY2_WEIGHT);
		return result;
	}
	public static ArrayList<Centroid> getTopDocs(String query_tag,int k){
		ArrayList<Centroid> result_list=new ArrayList<Centroid>();
		try{
			load();

			ArrayList<Centroid> list=doc_map.get(query_tag);
			if (list==null)
				return null;
			
			//Uniformly shuffle the docs:
			//list=new ArrayList<Centroid>(list.subList(0,Math.min(list.size(),(int)(k*2.038) )) );
			//Collections.shuffle(list);
			
			Iterator <Centroid> it=list.iterator();
			int j=0;
			int i=0;
			while (it.hasNext() && j<k){
				Centroid c=it.next();
				if (i>=Configure.TOP_IR_START_POINT){
					result_list.add(c.clone());
					j++;
				}
				i++;
			}
		}catch(Exception e){e.printStackTrace();}
		return result_list;
	}
	private static void load(){
		if (RESET_TOP){ // IF we need reset
			loadDocsFromIndri();
			RESET_TOP=false;
		}else if (doc_map==null) //IF no need reset and not loaded yet
			loadDocsFromFile();
	}
	private static void loadDocsFromFile(){
		try{
			doc_map=new HashMap < String,ArrayList<Centroid> > ();
			BufferedReader br=new BufferedReader(new FileReader(new File(Configure.TOPIREL_FILE)));
			String line=null;
			while ((line=br.readLine())!=null){
				StringTokenizer st=new StringTokenizer(line);
				String tag=st.nextToken();
				String tweetid=st.nextToken();
				ArrayList<Centroid> list=doc_map.get(tag);
				if (list==null){
					list=new ArrayList<Centroid>();
					doc_map.put(tag,list);
				}
				list.add(  new Centroid(new Tweet(tweetid)) );
			}
		}catch(Exception e){e.printStackTrace();}
	}
	private static void loadDocsFromIndri(){
		try{
			Iterator <Query> it=QueryList.iterator();
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File(Configure.TOPIREL_FILE)));
			while (it.hasNext()){
				Query query=it.next();

				String command="sh runquery.sh temp.txt "+TOP_IR+" "+Configure.INV_LIST_FOLDER+query.num;
				System.out.println(command);
				writeQueryFile(query.words);
				
				Process process=Runtime.getRuntime().exec(command);
				BufferedReader br = new BufferedReader(new InputStreamReader(
			                    process.getInputStream()));
				process.waitFor();

				int j=0;
				while (br.ready() && j<TOP_IR){
					String s=br.readLine();

					if (j==0)
						System.out.println(s);

					StringTokenizer st=new StringTokenizer(s);
					st.nextToken();
					st.nextToken();
					String tid=st.nextToken(); // tweetid
					if (doc_map==null){
						doc_map=new HashMap < String,ArrayList<Centroid> > ();
					}
					ArrayList <Centroid> list=doc_map.get(query.num);
					if (list==null){
						list=new ArrayList<Centroid>();
						doc_map.put(query.num,list);
					}
					list.add(new Centroid(new Tweet(tid)));
					bw.write(query.num+" "+tid);
					bw.newLine();
					j++;
				}
			}
			bw.close();
		}catch(Exception e){e.printStackTrace();}
	}
	private static void writeQueryFile(String s){
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