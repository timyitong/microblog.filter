package yitongz.mongodb;
import java.io.*;
import java.util.*;
import java.math.BigInteger;

public class TweetExpansion{
	private String html_folder="../../data/_indri_tweet_links";
	private static Hashtable <String,String> table=new Hashtable <String,String>();
	private LinkedList <String> raw_list=new LinkedList <String>();
	private String SAVE_URL="../../data/train.raw_url.txt";

	public TweetExpansion(){
		prepare();
	}
	public void expand(){
		try{
			String [] filenames=new File(html_folder).list();
			File [] files=new File(html_folder).listFiles();

			LinkedList <Tweet> list=getList();
			Tweet t=list.pollFirst();

			for (int i=0;i<filenames.length && t!=null ;i++){
				if (filenames[i].matches(".*DS_Store.*"))
					continue;

				File file=files[i];
				BufferedReader br=new BufferedReader(new FileReader(file));
				String line=null;

				boolean useful=false;
				String body=null;
				String meta=null;
				String title=null;

				while (br.ready() && t!=null){
					line=br.readLine();
					if (line.matches(".*TWEETID.*")){
						line=line.replaceAll("[^0-9]","");
						if ( new BigInteger(t.tweetid).compareTo(new BigInteger(line)) >0 ){
							useful=false;
						}else if (new BigInteger(t.tweetid).compareTo(new BigInteger(line)) ==0 ){
							useful=true;
						}else if (new BigInteger(t.tweetid).compareTo(new BigInteger(line)) <0 ){
							useful=false;
							t=list.pollFirst();
						}
					}else if (line.matches(".*BODY.*BODY.*")){
						int l="<BODY>".length();
						body=line.substring(l,line.length()-l-1);
					}else if (line.matches(".*META.*META.*")){
						int l="<META>".length();
						meta=line.substring(l,line.length()-l-1);
					}else if (line.matches(".*TITLE.*TITLE.*")){
						int l="<TITLE>".length();
						title=line.substring(l,line.length()-l-1);
					}else if (line.trim().matches("</DOC>")){
						if (useful){
							addExpand(t,body,meta,title);
							t=list.pollFirst();
						}
						useful=false;
					}
				}
			}
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File(SAVE_URL)));
			for (String s: raw_list){
				bw.write(s);
				bw.newLine();
			}
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void register(){

	}
	public static String getExpand(String id){
		return table.get(id);
	}
	private void prepare(){
		try{
			BufferedReader br=new BufferedReader(new FileReader(new File(SAVE_URL)));
			String line=null;
			while (br.ready()){
				line=br.readLine();
				StringTokenizer st=new StringTokenizer(line);
				String id=st.nextToken();
				TreeMap <String,Integer> map=new TreeMap <String,Integer> ();

				StringBuilder sb=new StringBuilder();
				while (st.hasMoreTokens()){
					String w=st.nextToken();
					if (w.equals("SWAS"))
						continue;
					else{
						Integer tf=map.get(w);
						if (tf==null)
							tf=new Integer(0);
						tf++;
						map.put(w,tf);
						sb.append(w+" ");
					}
				}
				table.put(id,sb.toString());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void addExpand(Tweet t,String body, String meta, String title){
			StringBuilder sb=new StringBuilder();
			meta=meta.replaceAll("[^A-Za-z0-9\\s]","");
			title=title.replaceAll("[^A-Za-z0-9\\s]","");
			
			sb.append(t.tweetid+" ");
			sb.append(meta+" ");
			sb.append(title+" ");
			System.out.println(t.tweetid+" "+meta+" "+title);
			raw_list.add(sb.toString());
	}
	private LinkedList <Tweet> getList(){
		LinkedList <Tweet> list=new LinkedList <Tweet> ();
		try{
			BufferedReader br=new BufferedReader(new FileReader(new File(Configure.TRUE_RESULT_FILE)));
			String line=null;
			while (br.ready()){
				line=br.readLine();
				StringTokenizer st=new StringTokenizer(line);
				st.nextToken();
				st.nextToken();
				Tweet t=new Tweet(st.nextToken());
				if (t!=null && t.tweetid!=null && t.hasurls!=null && t.hasurls.equals("true")){
					list.add(t);
				}
			}
			Collections.sort(list);
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("list ready");
		return list;
	}
}