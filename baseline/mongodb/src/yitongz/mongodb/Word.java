package yitongz.mongodb;
public class Word implements Comparable <Word>{
	String word;
	Integer tf;
	Word (String word, Integer tf){
		this.word=word;
		this.tf=tf;
	}
	public int compareTo(Word w){
		if (tf>w.tf)
			return -1;
		else if (tf==w.tf)
			return 0;
		else
			return 1;
	}
}