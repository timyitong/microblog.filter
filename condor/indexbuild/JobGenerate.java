import java.io.*;
import java.util.*;
import java.text.NumberFormat;
public class JobGenerate{
	public static void main(String argv[]) throws Exception{
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumIntegerDigits(3);
		BufferedWriter w=new BufferedWriter(new FileWriter(new File("submit.sh")));

		for (int i=1;i<=50;i++){
			if (i%5!=1)
				continue;
			String s=nf.format(i);
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File("MB"+s+".job")));
			bw.write("Universe = vanilla");
			bw.newLine();
			bw.write("executable = /bos/usr0/yitongz/indri-5.4/buildindex/IndriBuildIndex");
			bw.newLine();
			bw.write("arguments = -corpus.path=/bos/tmp13/yitongz/data/_indri_xml_topic/MB"+s);
			bw.write(" -corpus.class=trectext -field.name=CLEANTWEET -index=/bos/tmp13/yitongz/data/_indri_inv/MB"+s+" -indexType=indri");
			bw.newLine();
			bw.write("output = /bos/usr0/yitongz/microblog.filter/condor/indexbuild/MB"+s+".job.out");
			bw.newLine();
			bw.write("log = /bos/usr0/yitongz/microblog.filter/condor/indexbuild/MB"+s+".job.log");
			bw.newLine();
			bw.write("error = /bos/usr0/yitongz/microblog.filter/condor/indexbuild/MB"+s+".job.err");
			bw.newLine();
			bw.write("queue");
			bw.newLine();
			bw.close();

			w.write("condor_submit MB"+s+".job");
			w.newLine();
            if (i%15==0){
                w.write("sleep 3m");
                w.newLine();
            }

		}
		w.close();
        w=new BufferedWriter(new FileWriter(new File("rename.sh")));
        for (int i=2;i<=49;i++){
            if (i%5==1)
                continue;
            String s=nf.format(i);
            w.write("mv /bos/tmp13/yitongz/data/_indri_xml_topic/MB"+s+"\\  /bos/tmp13/yitongz/data/_indri_xml_topic/MB"+s);
            w.newLine();
        }
        w.close();
	}
}
