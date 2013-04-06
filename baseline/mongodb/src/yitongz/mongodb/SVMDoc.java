package yitongz.mongodb;
import java.util.Map;
import java.util.Collections;
import java.util.ArrayList;
public class SVMDoc{
	public DocVector vector;
	public int tag;
	public SVMDoc(){

	}
	public SVMDoc(Tweet t){	
		vector=new DocVector(t.clean_tweet);
	}
	public SVMDoc(DocVector v){
		vector=v;
	}
	public SVMDoc(String w){
		vector=new DocVector(w);
	}
	public String toString(){
		StringBuilder s=new StringBuilder();
		s.append(tag);
		ArrayList <DocElement> elist=new ArrayList <DocElement> ();
		for (Map.Entry<String,Double> entry : vector.map.entrySet() ) {
	        Double f = entry.getValue();
	        String term = entry.getKey();
	        int id=WordIDMap.getInstance().getID(term);
	        elist.add(new DocElement(id," "+id+":"+f+" ") );
		}
		Collections.sort(elist);
		for (DocElement e: elist){
			s.append(e.text);
		}
		return s.toString();
	}
}