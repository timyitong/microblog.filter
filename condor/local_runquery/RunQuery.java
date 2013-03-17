import java.io.*;
import java.text.NumberFormat;
import java.util.*;
public class RunQuery{
    public static void main(String argv[]) throws Exception{
        NumberFormat nf=NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(3);
        String s;
        Iterator <Query> it=QueryList.iterator();
        BufferedWriter writer=new BufferedWriter(new FileWriter(new File("runquery.sh")));
        while (it.hasNext()){
            Query q=it.next();
            s=q.num;
            BufferedWriter bw=new BufferedWriter(new FileWriter(new File(s+"_query.txt")));
            bw.write("<parameters>");
            bw.newLine();

            bw.write("<query>");
            bw.newLine();

            bw.write("<number>"+s+"</number>");
            bw.newLine();

            bw.write("<text>"+q.words.replaceAll("[\"|,|\']","")+"</text>");
            bw.newLine();
            
            bw.write("</query>");
            bw.newLine();

            bw.write("</parameters>");
            bw.newLine();

            bw.close();

            writer.write("/Users/timyitong/Dev/indri-5.4/runquery/./IndriRunQuery "
             +s+"_query.txt"
             +" -count=1000 -index=/Users/timyitong/Dev/microblog.filter/data/_indri_inv/"
             +s+" -trecFormat=1 > /Users/timyitong/Dev/microblog.filter/data/queryresult/"
             +s+".out");
            writer.newLine();
        }
        writer.close();
    }
}