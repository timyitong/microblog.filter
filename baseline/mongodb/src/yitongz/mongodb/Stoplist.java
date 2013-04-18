package yitongz.mongodb;
import java.util.HashSet;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
public class Stoplist{
	private HashSet<String> stop;
	private static Stoplist ins=new Stoplist();
	private Stoplist(){
		stop=new HashSet<String>();


		try{
			BufferedReader br=new BufferedReader(new FileReader(new File("../../data/stoplist.txt")));
			String line=null;
			while ((line=br.readLine())!= null){
				stop.add(line.trim());
			}
			br.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static Stoplist getInstance(){
		return ins;
	}
	public boolean has(String w){
		return stop.contains(w.toLowerCase());
	}
}