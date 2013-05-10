package yitongz.mongodb;

import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.File;

public class TweetChecking{
	public TweetChecking(){
		check();
	}
	private void check(){
		LinkedList <Tweet> list=new LinkedList <Tweet> ();
		int rc=0;
		int irc=0;
		int rc_m=0;
		int irc_m=0;
		try{
			BufferedReader br=new BufferedReader(new FileReader(new File(Configure.TRUE_RESULT_FILE)));
			String line=null;
			while (br.ready()){
				line=br.readLine();
				StringTokenizer st=new StringTokenizer(line);
				st.nextToken();
				st.nextToken();
				Tweet t=new Tweet(st.nextToken());
				if (Integer.parseInt(st.nextToken())>0 ){ // relevant
					rc++;
					if (t==null || t.user==null)
						rc_m++;
				}else{ //irrelevant
					irc++;
					if (t==null || t.user==null)
						irc_m++;
				}
			}
			Collections.sort(list);
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("r:"+rc_m+" "+rc);
		System.out.println("ir:"+irc_m+" "+irc);
	}
}