import java.io.*;
import java.util.*;
public class ResultReader{
	public static void main(String argv[]) throws Exception{
		BufferedWriter bw=new BufferedWriter(new FileWriter(new File("sorted.txt")));
		BufferedReader br=new BufferedReader(new FileReader(new File("result.txt")));
		String line=null;
		String num=null;
		String prec=null;
		String recall=null;
		String f=null;
		String t=null;

		while (br.ready()){
			line=br.readLine();
			if (line.matches(".*BEND.*")){
				num=line.substring(line.indexOf(":")+1,line.length());
			}else if (line.matches(".*prec.*")){
				prec=getNum(line);
			}else if (line.matches(".*recl.*")){
				recall=getNum(line);
			}else if (line.matches(".*F.*")){
				f=getNum(line);
			}else if (line.matches(".*t11su.*")){
				t=getNum(line);
			}
			else if (num!=null && line.matches(".*missing.*")){
				bw.write(num+"\t"+prec+"\t"+recall+"\t"+f+"\t"+t);
				bw.newLine();
			}
		}
		bw.write(num+"\t"+prec+"\t"+recall+"\t"+f+"\t"+t);
		bw.newLine();
		br.close();
		bw.close();
	}
	public static String getNum(String s){
		StringTokenizer st=new StringTokenizer(s);
		String ss=null;
		while (st.hasMoreTokens()){
			ss=st.nextToken();
		}
		return ss;
	}
}