package yitongz.indri.query;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.*;
import java.util.*;
public class QueryList{
	private static ArrayList <Query> list=null;
	public static String QUERY_FILE="~/Dev/microblog.filter/data/2012.topics.MB1-50.filtering.txt";
	private QueryList(){

	}
	private static void init(){
		try{
			list=new ArrayList <Query>();

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new File(QUERY_FILE));
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("top");
			for (int i=0;i<nList.getLength();i++){
				Node d=nList.item(i);

				Query q=new Query();
				if (d.getNodeType() == Node.ELEMENT_NODE) {
					Element e=(Element)d;
					q.num=parseNum(	e.getElementsByTagName("num").item(0).getTextContent()	);
					q.words=e.getElementsByTagName("title").item(0).getTextContent();
					q.tweetid=e.getElementsByTagName("querytweettime").item(0).getTextContent();
					q.newesttweet=e.getElementsByTagName("querynewesttweet").item(0).getTextContent();
					list.add(q);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static Iterator <Query> iterator(){
		if (list==null)
			init();
		return list.iterator();
	}
	private static String parseNum(String s){
		int begin=s.indexOf(":")+2;
		return s.substring(begin,s.length());
	}
}