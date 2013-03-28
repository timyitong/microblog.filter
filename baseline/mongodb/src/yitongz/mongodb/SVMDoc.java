package yitongz.mongodb;
public class SVMDoc{
	public DocVector vector;
	public int tag;
	public SVMDoc(){

	}
	public SVMDoc(Tweet t){	
		vector=new DocVector();
		vector.add(t.vector);
	}
	public SVMDoc(DocVector v){
		vector=v;
	}
	public String toString(){
		StringBuilder s=new StringBuilder();
		s.append(tag);
		for (Map.Entry<String,Double> entry : vector.map.entrySet() ) {
	        Double f = entry.getValue();
	        String term = entry.getKey();
	        int id=WordIDMap.getInstance().getID(term);
	        s.append(" "+id+":"+f+" ");
		}
		return s.toString();
	}
}