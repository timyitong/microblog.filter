package yitongz.mongodb;
import java.io.*;
import java.util.*;
import java.text.NumberFormat;
public class Facts{
	private static ArrayList <HashMap <String,Boolean> > list=new ArrayList <HashMap <String,Boolean> > ();
	private static HashMap <String,Integer> id_map=new HashMap <String, Integer>();
	private static int index=0;
	public static boolean check(String tag,String tweetid){
		Integer i=id_map.get(tag);
		if (i==null)
			return false;
		HashMap <String,Boolean > map=list.get(i);
		if (map==null)
			return false;
		Boolean rel=map.get(tweetid);
		if (rel==null)
			return false;
		else if (rel==true)
			return true;
		else
			return false;
	}
	public static void load(){
		try{
			NumberFormat nf=NumberFormat.getInstance();
			nf.setMinimumIntegerDigits(3);

			BufferedReader br=new BufferedReader(new FileReader(new File(Configure.TRUE_RESULT_FILE)));
			String line=null;
			while ((line=br.readLine())!=null){
				StringTokenizer st=new StringTokenizer(line);
				int id=Integer.parseInt(st.nextToken());
				String tag="MB"+nf.format(id);
				st.nextToken();
				String tweetid=st.nextToken();
				Boolean rel=Integer.parseInt(st.nextToken()) > 0 ? true : false;
				Integer i=id_map.get(tag);
				HashMap <String,Boolean> map;
				if (i==null){
					map=new HashMap <String,Boolean>();
					list.add(map);
					id_map.put(tag,index);
					index++;
				}else{
					map=list.get(i);
				}
				map.put(tweetid,rel);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}