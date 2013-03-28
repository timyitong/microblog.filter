package yitongz.mongodb;
import java.io.*;
public class TuneTask{
	private int times=50;
	private String message=null;
	public TuneTask(){
		for (int i=0;i<times;i++){
			new BatchTask();
			config();
			readResult();
		}
	}
	private void config(){
		/*
			message="init weight:"+Configure.INIT_WEIGHT;
			Configure.INIT_WEIGHT+=0.1;
		*/

		
			message="SLAVE_NUM:"+Configure.SLAVE_NUM;
			Configure.SLAVE_NUM+=1;
		
		
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