package yitongz.mongodb;
import java.io.*;
import java.util.*;

public class TweetExpansion{
	private String html_folder="../../data/_indri_tweet_links";
	private static Hashtable <String,String> table=new Hashtable <String,String>();
	public TweetExpansion(){
		prepare();
	}
	public void expand(){
		try{
			String [] filenames=new File(html_folder).list();
			File [] files=new File(html_folder).listFiles();

			LinkedList <Tweet> list=getList();
			Tweet t=list.pollFirst();

			for (i=0;i<filenames.length && t!=null ;i++){
				if (filenames.matches(".*DS_Store.*"))
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
						Tweet temp=new Tweet(line.replaceAll("[^0-9]",""));
						if (temp==null || temp.tweetid==null || t.compareTo(temp) > 0 ){
							useful=false;
						}else if (t.compareTo(temp)==0){
							useful=true;
						}else if (t.compareTo(temp) < 0){
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
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void register(){

	}
	private void prepare(){
		expand();
	}
	private void addExpand(Tweet t,String body, String meta, String title){
		try{
			table.put(t.tweetid,meta);
		}catch(Exception e){
			e.printStackTrace();
		}
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
				if (t!=null && t.tweetid!=null && t.hasurls.equals("true")){
					list.add(t);
				}
			}
			Collections.sort(list);
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
}