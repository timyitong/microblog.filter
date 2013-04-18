package yitongz.mongodb;
import java.util.Hashtable;
import java.util.StringTokenizer;


public class OnlineWordStats implements Stats{
	private Hashtable <String, Integer> tf_map=new Hashtable <String, Integer>();
	private int tc=1;
	private static OnlineWordStats instance=new OnlineWordStats();
	private OnlineWordStats(){
	}
	public static OnlineWordStats getInstance(){
		return instance;
	}
	public void register(String w){
		w=w.trim().replaceAll("[^a-zA-Z0-9\\s]","");
		StringTokenizer st=new StringTokenizer(w);
		String old=null;
		while (st.hasMoreTokens()){
			String word=st.nextToken();
			Integer count=tf_map.get(word);
			if (count==null)
				count=new Integer(1);
			else
				count=count+1;
			tf_map.put(word,count);
			tc++;
			//adding two gram stats
			if (old!=null){
				Integer cc=tf_map.get(old+" "+w);
				if (cc==null)
					cc=new Integer(1);
				else
					cc=cc+1;
				tf_map.put(old+" "+w,cc);
			}
			old=w;
		}
	}
	public void refresh(){
		tf_map=new Hashtable <String, Integer>();
		tc=1;
	}
	public int getTC(){
		return tc;
	}
	public int getTF(String w){
		Integer count=tf_map.get(w);
		if (count==null)
			count=new Integer(0);
		return count.intValue();
	}
}