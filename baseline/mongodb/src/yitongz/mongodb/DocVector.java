package yitongz.mongodb;
import java.util.TreeMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.lang.Math;

public class DocVector implements VectorCalculator{
	public TreeMap <String,Double> map=new TreeMap <String,Double>();
	private double modValue=-1;
	public DocVector(){

	}
	public DocVector(String w){
		w=w.trim().replaceAll("[^A-Za-z0-9\\s]","");
		StringTokenizer st=new StringTokenizer(w);
		while (st.hasMoreTokens()){
			String term=st.nextToken();
			Double f=map.get(term);
			if (f==null){
				map.put(term, new Double(1));
			}else{
				f=f+1;
			}
		}

	}
	public void add(DocVector d){
		for (Map.Entry<String,Double> entry : d.map.entrySet() ) {
	        Double f = entry.getValue();
	        String term = entry.getKey();

	        Double new_f=map.get(term);
	        if (new_f==null){
	        	map.put(term,f);
	        }else{
	        	new_f=new_f+f;
	        }
		}
	}
	public void minus(DocVector d){
		for (Map.Entry<String,Double> entry : d.map.entrySet() ) {
	        Double f = entry.getValue();
	        String term = entry.getKey();

	        Double new_f=map.get(term);
	        if (new_f==null){
	        	map.put(term,0-f);
	        }else{
	        	new_f=new_f-f;
	        }
		}	
	}
	public void multiply(double c){
		for (Map.Entry<String,Double> entry : this.map.entrySet() ) {
	        Double f = entry.getValue();
	        String term = entry.getKey();

	        f=f*c;
		}
	}
	public double innerProduct(DocVector d){
		double score=0;
		for (Map.Entry<String,Double> entry : d.map.entrySet() ) {
	        Double f = entry.getValue();
	        String term = entry.getKey();

	        Double ff=map.get(term);
	        if (ff!=null){
	        	score+=ff*f;
	        }
		}	
		return score;
	}
	public double mod(){
		if (modValue!=-1)
			return modValue;

		double mod=0;
		for (Map.Entry<String,Double> entry : map.entrySet() ) {
	        Double f = entry.getValue();
	        String term = entry.getKey();

	        mod+=f*f;
		}
		return Math.sqrt(mod);
	}
}