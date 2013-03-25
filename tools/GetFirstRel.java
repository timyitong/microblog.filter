import java.util.*;
import java.io.*;
import java.math.BigInteger;
public class GetFirstRel{
    public static void main(String argv[]){
        try{
            BufferedReader br=new BufferedReader(new FileReader(new File("../data/train.filtering-qrels.txt")));
            HashMap <String,String> map=new HashMap<String, String>();
            int index=0;
            String line=null;
            while ((line=br.readLine())!=null){
                StringTokenizer st=new StringTokenizer(line);
                String tag=st.nextToken();
                st.nextToken();
                String id=st.nextToken();
                String rel=st.nextToken();
                if (Integer.parseInt(rel)>0){
                    map.put(tag,id);
                }
            }
            for (int i=1;i<=49;i++){
                if (i%5!=1) continue;
                System.out.println(map.get(i+""));
            }
        }
        catch(Exception e){e.printStackTrace();}
    }
}
