package yitongz.mongodb;
public class DocElement implements Comparable <DocElement>{
	int id;
	String text;
	public DocElement(int id, String s){
		this.id=id;
		text=s;
	}
	public int compareTo(DocElement d){
		if (this.id < d.id){
			return -1;
		}else  if (this.id == d.id){
			return 0;
		}else{
			return 1;
		}
	}
}