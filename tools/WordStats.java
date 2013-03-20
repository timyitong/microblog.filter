import java.util.*;
import java.io.*;
public class WordStats{
  public static void main(String argv[]){
    if (argv[0].equals("-h")){
      System.out.println("[textfile_src]");
    }
    ArrayList <Integer> count_list=new ArrayList <Integer> ();
    LinkedList <String> name_list = new LinkedList <String> ();
    Hashtable <String,Integer> map=new Hashtable <String, Integer>();
    Integer index=0;
    try{
      BufferedReader br=new BufferedReader(new FileReader(new File(argv[0] ) ) );
      String line=null;
      while ((line=br.readLine())!=null){
        StringTokenizer st=new StringTokenizer(line);
        while (st.hasMoreTokens()){
          String word=st.nextToken();
          Integer i=map.get(word);
          if (i==null){	
            map.put(word,index);
            count_list.add(new Integer("1"));
            name_list.add(word);
            index++;
          }else{
            Integer num=count_list.get(i);
            num++;
            count_list.set(i,num);
          }
        }

      }
      for (int k=0;k<name_list.size();k++){
      System.out.println(name_list.get(k)+" "+count_list.get(k));
      } 
    }catch(Exception e){e.printStackTrace();} 
  }
}
