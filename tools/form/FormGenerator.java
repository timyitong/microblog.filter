import java.io.*;
import java.util.*;
public class FormGenerator{
	public static LinkedList result=new LinkedList();
	public static void main(String argv[]) throws Exception{
		LinkedList <String> list=getFileList();
		for (String file : list){
			LinkedList <Performance> l=new LinkedList <Performance>();

			file="../../data/result/"+file;
			BufferedReader br=new BufferedReader(new FileReader(new File(file)));
			String line=null;

			int i=1;
			Performance p=new Performance(i);
			while ((line=br.readLine())!=null){
				StringTokenizer st=new StringTokenizer(line);
				String tag=st.nextToken();
				int num;
				if (tag.equals("all"))
					num=0;
				else
					num=Integer.parseInt(tag);
				String header="";
				if (!st.hasMoreTokens()) 
					continue;
				else
					header=st.nextToken();
				double value=Double.parseDouble(st.nextToken());
				
				if (num!=i){
					l.add(p);
					p=new Performance(num);
					i=num;
				}

				if (header.matches(".*prec.*")){
					p.prec=value;
				}else if (header.matches(".*recl.*")){
					p.recl=value;
				}else if (header.matches(".*F.*")){
					p.f=value;
				}else if (header.matches(".*t11su.*")){
					p.t=value;
				}else{

				}

			}
			l.add(p);
			result.add(l);
		}
		output();
	} 
	public static void output(){
		try{
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File("output.txt")));
			LinkedList <Performance> list;

			bw.write("Prec:");
			bw.newLine();
			boolean header=false;
			for (Object o: result){
				list=(LinkedList <Performance>)o;
				if (!header){
					for (Performance p : list)
						bw.write(p.query+"\t");
					bw.newLine();
					header=true;
				}
				for (Performance p: list){
					bw.write(p.prec+"\t");
				}
				bw.newLine();
			}
			bw.newLine();

			bw.write("Recl:");
			bw.newLine();
			 header=false;
			for (Object o: result){
				list=(LinkedList <Performance>)o;
				if (!header){
					for (Performance p : list)
						bw.write(p.query+"\t");
					bw.newLine();
					header=true;
				}
				for (Performance p: list){
					bw.write(p.recl+"\t");
				}
				bw.newLine();
			}
			bw.newLine();

			bw.write("F0.5:");
			bw.newLine();
			 header=false;
			for (Object o: result){
				list=(LinkedList <Performance>)o;
				if (!header){
					for (Performance p : list)
						bw.write(p.query+"\t");
					bw.newLine();
					header=true;					
				}
				for (Performance p: list){
					bw.write(p.f+"\t");
				}
				bw.newLine();
			}
			bw.newLine();

			bw.write("t11su:");
			bw.newLine();
			 header=false;
			for (Object o: result){
				list=(LinkedList <Performance>)o;
				if (!header){
					for (Performance p : list)
						bw.write(p.query+"\t");
					bw.newLine();
					header=true;					
				}
				for (Performance p: list){
					bw.write(p.t+"\t");
				}
				bw.newLine();
			}
			bw.newLine();

			bw.close();
		}catch(Exception e){e.printStackTrace();}
	}
	public static LinkedList <String> getFileList(){
		LinkedList <String>list=new LinkedList <String>();
		try{
			BufferedReader br=new BufferedReader(new FileReader(new File("input.txt")));
			String line=null;
			while(br.ready()){
				line=br.readLine();
				list.add(line);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
}