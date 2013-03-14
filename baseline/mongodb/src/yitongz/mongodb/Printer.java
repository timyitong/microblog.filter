package yitongz.mongodb;

import java.util.ArrayList;
import java.io.*;

public class Printer{
	public static String output_url="../../data/result/";
	public static String run_name="yitongz-baseline";
	public static void printFilterResult(ArrayList <Tweet> list){
		try{
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File(output_url+System.currentTimeMillis()+".txt")));
			for (Tweet t: list){
				bw.write(t.query_num
						+" "+t.tweetid
						+" "+t.score
						+" "+(t.relevant ? "yes" : "no")
						+" "+run_name);
				bw.newLine();
			}
			bw.close();
		}catch(Exception e){e.printStackTrace();}
	}
}