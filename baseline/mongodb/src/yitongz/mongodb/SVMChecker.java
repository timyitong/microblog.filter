package yitongz.mongodb;
import java.util.ArrayList;

public class SVMChecker{
	private Query query;
	public static int SVM_IR_LENGTH=1000;
	public static String TRAIN_URL="../../data/svm/train.temp.txt";
	public static String TEST_URL="../../data/svm/test.temp.txt";
	public static String MODEL_URL="../../data/svm/model.txt";
	public static String RESULT_URL="../../data/svm/RESULT.txt";

	private ArrayList <SVMDoc> listPos=new ArrayList <SVMDoc>();
	private ArrayList <SVMDoc> listNeg=new ArrayList <SVMDoc>();
	private ArrayList <SVMDoc> pairs;

	public SVMChecker(Query q){
		query=q;
		init();
	}
	private void init(){
		listPos.add(new SVMDoc(new Tweet(query.tweetid)));
		ArrayList <Centroid> list=IndriSearcher.getTopDocs(query,SVM_IR_LENGTH);
		for (Centroid c: list){
			listNeg.add(new SVMDoc(c.tweet));
		}

		wise();
		writeTrainFile();
		learn();

		System.out.println("init score:"+classify(listPos.get(0)));
	}
	public boolean judge(Tweet t){
		DocVector v=new DocVector();
		v.add(t.vector);
		SVMDoc doc=new SVMDoc(v);
		double score=classify(doc);
		System.out.println("score:"+score);
		// Give Score:
		t.score=score; 
		if (score < 0){
			listNeg.add(doc);
			return false;
		}else{
			if (Facts.check(query.num,t.tweetid)){
				listPos.add(doc);
			}else{
				listNeg.add(doc);
			}
			wise();
			writeTrainFile();
			learn();
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
							+" "+TRAIN_URL;
							+" "+MODEL_URL;
			System.out.println(command);			
			Process process=Runtime.getRuntime().exec(command);
			BufferedReader br = new BufferedReader(new InputStreamReader(
		                    process.getInputStream()));
			process.waitFor();
			while (br.ready()){
				System.out.println(br.readLine());
			}
		}catch(Exception e){e.printStackTrace();}
	}
	private double classify(SVMDoc doc){
		double score=-1;
		writeTestFile(doc);
		try{
			String command="../../tools/svm_light/./svm_classify"
							+" "+TEST_URL
							+" "+MODEL_URL;
							+" "+RESULT_URL;
			System.out.println(command);			
			Process process=Runtime.getRuntime().exec(command);
			BufferedReader br = new BufferedReader(new InputStreamReader(
		                    process.getInputStream()));
			process.waitFor();
			while (br.ready()){
				System.out.println(br.readLine());
			}

			br = new BufferedReader(new FileReader(new File(RESULT_URL)));
			String s=br.readLine();
			if (s!=null)
				score=Double.parseDouble(s);
		}catch(Exception e){e.printStackTrace();}
		return s;
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