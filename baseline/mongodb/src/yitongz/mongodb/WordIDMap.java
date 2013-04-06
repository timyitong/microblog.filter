package yitongz.mongodb;
import java.util.HashMap;
public class WordIDMap{
	private static WordIDMap instance=new WordIDMap();

	private static HashMap<String,Integer> map=new HashMap<String,Integer>();
	private static int index=1;
	private WordIDMap(){

	}
	public static WordIDMap getInstance(){
		return instance;
	}
	public static int getID(String word){
		Integer id=map.get(word);
		if (id==null){
			id=new Integer(index);
			map.put(word,id);
			index++;
		}
		return id.intValue();
	}
} 