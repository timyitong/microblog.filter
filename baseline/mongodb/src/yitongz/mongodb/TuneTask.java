package yitongz.mongodb;
import java.io.*;
public class TuneTask{
	private int times=10;
	private String message=null;
	public TuneTask(){
		for (int i=0;i<times;i++){
			new BatchTask();
			config();
			readResult();
		}
	}
	private void config(){
		message="Fixed Cutoff:"+Configure.FIXED_CUTOFF;
		Configure.FIXED_CUTOFF+=0.02;
		/*
		message="2GRAM:"+Configure.NGRAM_WEIGHT;
		Configure.NGRAM_WEIGHT+=0.05;
		*/

		/*
		message="PACE:"+Configure.AUGUMENT_PACE;
		Configure.AUGUMENT_PACE+=0.01;
		*/

		/*
		message="expand:"+Configure.EXPAND_QUERY2_WEIGHT;
		Configure.EXPAND_QUERY2_WEIGHT+=0.01;
		*/

		/*
		message="k1:"+Configure.BM25_K1;
		Configure.BM25_K1+=0.05;
		*/

		/*
		message="expand_weight:"+Configure.EXPAND_QUERY_WEIGHT;
		Configure.EXPAND_QUERY_WEIGHT+=0.1;
		*/
		
		/*
		message="c:"+Configure.ROCHHIO_C;
		Configure.ROCHHIO_C+=0.1;
		*/

		/*
			message="init weight:"+Configure.INIT_WEIGHT;
			Configure.INIT_WEIGHT+=0.1;
		*/

		/*
			message="SLAVE_NUM:"+Configure.SLAVE_NUM;
			Configure.SLAVE_NUM+=1;
		*/
		
		/*
			message="PACE: "+Counter.PACE;
			Counter.PACE+=0.05;
		*/
		
		/*
			message="TOP_START_POINT:"+Configure.TOP_IR_START_POINT;
			Configure.TOP_IR_START_POINT+=50;
		*/
	}
	private void readResult(){
		try{
			String command="sh eval.sh temp.txt";
			
			Process process=Runtime.getRuntime().exec(command);
			BufferedReader br = new BufferedReader(new FileReader(new File("../../data/result/temp.txt.eval")));
			process.waitFor();
			System.out.println("======"+message);
			while (br.ready()){
				String line=br.readLine();
				if (line.matches(".*all.*"))
					System.out.println(line);
			}
		}catch(Exception e){e.printStackTrace();}
	}
}