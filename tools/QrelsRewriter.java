/*The tag of the qrels seem not be 100% correct, so I rewrite it to binary*/
import java.io.*;
import java.util.*;

public class QrelsRewriter{
	public static void main(String argv[]) throws Exception{
		BufferedReader br=new BufferedReader(new FileReader(new File("../data/filtering-qrels.txt.bk")));
		BufferedWriter bw=new BufferedWriter(new FileWriter(new File("../data/filtering-qrels.txt")));
		String line=null;
		while ((line=br.readLine())!=null){
			StringTokenizer st=new StringTokenizer(line);
			String [] w=new String[4];
			for (int i=0;i<4;i++)
				w[i]=st.nextToken();
			if (Integer.parseInt(w[3])>0)
				w[3]="1";
			else
				w[3]="0";

			bw.write(w[0]+" "+w[1]+" "+w[2]+" "+w[3]);
			bw.newLine();
		}
		bw.close();
	}
}
