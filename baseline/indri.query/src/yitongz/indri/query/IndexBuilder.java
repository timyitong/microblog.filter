package yitongz.indri.query;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.math.BigInteger;
import java.io.*;
import java.util.*;
public class IndexBuilder{
	private String src_url;
	private String target_url;
	public IndexBuilder(String src_url, String target_url){
		this.src_url=src_url;
		this.target_url=target_url;
		build();
	}
	private void readin(String store_url, String after_store_url, String timestamp, String neweststamp){
		try{
		BigInteger timestamp_int=new BigInteger(timestamp.replaceAll("\\s",""));
		BigInteger neweststamp_int=new BigInteger(neweststamp.replaceAll("\\s",""));
		String [] files=new File(src_url).list();
		File [] fs=new File(src_url).listFiles();
		Arrays.sort(files);

		BufferedWriter bw=new BufferedWriter(new FileWriter(new File(store_url)));
		BufferedWriter bw2=new BufferedWriter(new FileWriter(new File(after_store_url)));
		for (int i=0;i<files.length;i++){
			if (!files[i].matches(".*DS_Store.*")){
				File xmlFile = fs[i];
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				System.out.println(fs[i].toString());
				Document doc = dBuilder.parse(xmlFile);
				doc.getDocumentElement().normalize();

				NodeList nList = doc.getElementsByTagName("DOC");
				for (int j=0;j<nList.getLength();j++){
					Node d=nList.item(j);
					if (d.getNodeType() == Node.ELEMENT_NODE) {
						Element e=(Element) d;
						String tweetid=e.getElementsByTagName("TWEETID").item(0).getTextContent();
						//System.out.println("tweetid:"+tweetid+";;;;timestamp:"+timestamp);
						//System.out.println(new BigInteger(timestamp.replaceAll("\\s","")).compareTo(timestamp_int) );
						
						/*Tranform Node into String:*/
						StringWriter writer=new StringWriter();
						Transformer t=TransformerFactory.newInstance().newTransformer();
						t.transform(new DOMSource(d),new StreamResult(writer));
						String content=writer.toString();
						//Get rid of the <?xml=xxx?> header
						content=content.replaceAll("<\\?.*\\?>","");

						BigInteger tid=new BigInteger(tweetid.replaceAll("\\s",""));
						if (tid.compareTo(timestamp_int) <0	){
							bw.write(content);
							bw.newLine();
						}else if (tid.compareTo(neweststamp_int) <=0){
							bw2.write(content);
							bw2.newLine();
						}

					}
				}
			}
		}	
		bw.close();
		bw2.close();
		}catch(Exception e){e.printStackTrace();}
	}
	private void build(){
		Iterator <Query> it=QueryList.iterator();
		while(it.hasNext()){
			Query q=it.next();
			String timestamp=q.tweetid;
			String store_url=target_url+"/"+q.num;
			String after_store_url=target_url+"_after"+"/"+q.num;
			String neweststamp=q.newesttweet;
			readin(store_url,after_store_url,timestamp,neweststamp);
		}
	}
}