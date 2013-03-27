package yitongz.mongodb;
import java.io.*;
public class TuneTask{
	private int times=100;
	public TuneTask(){
		for (int i=0;i<times;i++){
			new BatchTask();
			readResult();
			config();
		}
	}
	private void config(){
		Configure.SLAVE_NUM++;
	}
	private void readResult(){
		try{
			String command="sh eval.sh temp.txt";
			
			Process process=Runtime.getRuntime().exec(command);
			BufferedReader br = new BufferedReader(new FileReader(new File("../../data/result/temp.txt.eval")));
			process.waitFor();
			System.out.println("======"+Configure.SLAVE_NUM);
			while (br.ready()){
				String line=br.readLine();
				if (line.matches(".*all.*"))
					System.out.println(line);
			}
		}catch(Exception e){e.printStackTrace();}
	}
}