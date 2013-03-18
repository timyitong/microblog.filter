import java.util.*;
import java.io.*;
import java.lang.StringBuilder;

public class WordCounter{
	public static HashSet <String> stop_map=null;
	Hashtable <String,Integer> map=new Hashtable <String,Integer>();
	ArrayList <WTF> list=new ArrayList <WTF>();
	int list_index=0;

	public WordCounter(){
		try{
		if (stop_map==null){
			stop_map=new HashSet <String> ();
			BufferedReader br=new BufferedReader(new FileReader(new File("../../data/stoplist.txt")));
			while (br.ready()){
				stop_map.add(br.readLine());
			}
		}
		}catch(Exception e){e.printStackTrace();}
	}
	public void add(String s){
		StringTokenizer st=new StringTokenizer(s);
		while (st.hasMoreTokens()){
			String w=st.nextToken();
			w=w.replaceAll("[^A-Za-z0-9]","");
			if (w.length()==0 || stop_map.contains(w.toLowerCase()))
				continue;

			Integer index=map.get(w);
			if (index==null){
				WTF wtf=new WTF();
				wtf.count=1;
				wtf.word=w;
				list.add(wtf);
				map.put(w,new Integer(list_index));
				list_index++;
			}else{
				WTF wtf=list.get(index);
				wtf.count++;
			}
		}
	}
	public String getTopWords(int k){
		StringBuilder build=new StringBuilder();
		Collections.sort(list);
		Iterator <WTF> it=list.iterator();
		int i=0;
		while (it.hasNext() && i<k){
			i++;
			build.append(" "+it.next().word);
		}
		return build.toString();
	}
	class WTF implements Comparable <WTF>{
		String word;
		int count=0;
		public int compareTo(WTF word){
			if (this.count>word.count)
				return -1;
			else if (this.count==word.count)
				return 0;
			else
				return 1;
		}
	}
}