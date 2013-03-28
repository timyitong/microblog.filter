package yitongz.mongodb;
import java.util.HashMap;
public class WordIDMap{
	private WordIDDictionary dic=new WordIDDictionary();

	private HashMap<String,Integer> map=new HashMap<String,Integer>();
	private static int index=1;
	private WordIDDictionary(){

	}
	public static WordIDDictionary getInstance(){
		return dic;
	}
	public static int getID(String word){
		int id=map.get(word);
		if (id==null){
			id=index;
			map.put(word,id);
			index++;
		}
		return id;
	}
} 