/*
Main Class of this tool
*/
package yitongz.indri.query;
public class Main{
	public static void main(String argv[]){
		QueryList.QUERY_FILE="../../data/2012.topics.MB1-50.filtering-wrapped.txt";
		//test();
		run();
	}
	public static void test(){

	}
	public static void run(){
		/*BUILD the INDEX:
		 *src_url, target_url*/
		new IndexBuilder("../../data/_indri_xml_wrapped","../../data/_indri_xml_topic");
	}
}