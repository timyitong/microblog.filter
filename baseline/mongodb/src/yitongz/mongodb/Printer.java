package yitongz.mongodb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.*;

public class Printer{
	public static String output_url=Configure.OUTPUT_FOLDER;
	public static String run_name=Configure.RUN_NAME;
	private static BufferedWriter bw=null;
	public static void printFilterResult(ArrayList <Tweet> list){
		try{
			if (bw==null) bw=new BufferedWriter(new FileWriter(new File(output_url+System.currentTimeMillis()+".txt")));
			Collections.reverse(list);
			for (Tweet t: list){
				bw.write(t.query_num
						+" "+t.tweetid
						+" "+t.score
						+" "+(t.relevant ? "yes" : "no")
						+" "+run_name);
				bw.newLine();
			}
		}catch(Exception e){e.printStackTrace();}
	}
	public static void newPrinter(){
		try{
			bw=new BufferedWriter(new FileWriter(new File(output_url+System.currentTimeMillis()+".txt")));
		}catch(Exception e){e.printStackTrace();}
	}
	public static void close(){
		try{
			bw.close();
		}catch(Exception e){e.printStackTrace();}
	}
}