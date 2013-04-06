package yitongz.mongodb;
import java.util.ArrayList;
import java.io.*;

public class SVMChecker{
	private Query query;
	public static int SVM_IR_LENGTH=1000;
	public static String TRAIN_URL="../../data/svm/train.temp.txt";
	public static String TEST_URL="../../data/svm/test.temp.txt";
	public static String MODEL_URL="../../data/svm/model.txt";
	public static String RESULT_URL="../../data/svm/result.txt";

	private ArrayList <SVMDoc> listPos=new ArrayList <SVMDoc>();
	private ArrayList <SVMDoc> listNeg=new ArrayList <SVMDoc>();
	private ArrayList <SVMDoc> pairs;

	private Counter counter=new Counter();
	private int train_tokens=0;

	public SVMChecker(Query q){
		query=q;
		init();
	}
	private void init(){
		listPos.add(new SVMDoc(new Tweet(query.tweetid)));
		ArrayList <Centroid> list=IndriSearcher.getTopDocs(query.num,SVM_IR_LENGTH);
		int i=0;
		for (Centroid c: list){
			if (c.tweet.clean_tweet!=null){
				if (i<10){
					listPos.add(new SVMDoc(c.tweet));
				}else{
					listNeg.add(new SVMDoc(c.tweet));
				}
				i++;
			}
		}

		wise();
		writeTrainFile();
		learn();
		setCutoff();
	}
	private void setCutoff(){
		for (SVMDoc d: listPos){
			counter.addPos(classify(d));
		}
		for (SVMDoc d: listNeg){
			counter.addNeg(classify(d));
		}
	}
	public boolean judge(Tweet t){
		DocVector v=new DocVector();
		if (t.clean_tweet==null){
			t.score=-1;
			return false;
		}
		v.add(t.vector);
		SVMDoc doc=new SVMDoc(v);
		double score=classify(doc);
		//System.out.println("tweetid:"+t.tweetid+" | score:"+score+"| cut: "+counter.svmcutoff());
		// Give Score:
		t.score=score; 
		if (score < counter.svmcutoff()){
			listNeg.add(doc);
			counter.addNeg(score);
			return false;
		}else{
			if (Facts.check(query.num,t.tweetid)){
				listPos.add(doc);
				counter.addPos(score);
				
				if (train_tokens>0){
					wise();
					writeTrainFile();
					learn();
					train_tokens--;
					setCutoff();
				}
				
			}else{
				listNeg.add(doc);
				counter.addNeg(score);
			}
			return true;
		}
	} 
	//First version, only generate one side training data
	private void wise(){
		pairs=new ArrayList <SVMDoc>();
		for (SVMDoc pos: listPos){
			for(SVMDoc neg: listNeg){
				DocVector v=new DocVector();
				v.add(pos.vector);
				v.minus(neg.vector);
				SVMDoc p=new SVMDoc(v);
				p.tag=1;
				pairs.add(p);
			}
		}
	}
	private void learn(){
		try{
			String command="../../tools/svm_light/./svm_learn"
							+" "+"-b 0"
							+" "+TRAIN_URL
							+" "+MODEL_URL;
			//System.out.println(command);			
			Process process=Runtime.getRuntime().exec(command);
			BufferedReader br = new BufferedReader(new InputStreamReader(
		                    process.getInputStream()));
			process.waitFor();
			/*
			while (br.ready()){
				System.out.println(br.readLine());
			}*/
		}catch(Exception e){e.printStackTrace();}
	}
	private double classify(SVMDoc doc){
		double score=-1;
		writeTestFile(doc);
		try{
			String command="../../tools/svm_light/./svm_classify"
							+" "+TEST_URL
							+" "+MODEL_URL
							+" "+RESULT_URL;
			//System.out.println(command);			
			Process process=Runtime.getRuntime().exec(command);
			BufferedReader br = new BufferedReader(new InputStreamReader(
		                    process.getInputStream()));
			process.waitFor();
			/*
			while (br.ready()){
				System.out.println(br.readLine());
			}*/

			br = new BufferedReader(new FileReader(new File(RESULT_URL)));
			String s=br.readLine();
			if (s!=null)
				score=Double.parseDouble(s);
		}catch(Exception e){e.printStackTrace();}
		return score;
	}
	private void writeTestFile(SVMDoc doc){
		try{
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File(TEST_URL)));
			bw.write(doc.toString());
			bw.newLine();
			bw.close();
		}catch(Exception e){ e.printStackTrace();}
	}
	private void writeTrainFile(){
		try{
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File(TRAIN_URL)));
			for (SVMDoc d: pairs){
				bw.write(d.toString());
				bw.newLine();
			}
			bw.close();
		}catch(Exception e){ e.printStackTrace();}
	}

}