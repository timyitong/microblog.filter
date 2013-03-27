package yitongz.mongodb;
/* This is the Configuration Class
 * All constant variables should be put here
 */
public class Configure{
	public static String QUERY_FILE="../../data/train.topics.filtering_ch.txt";
	public static String TRUE_RESULT_FILE="../../data/train.filtering-qrels.txt";
	public static String TOPIC_TWEET_FOLDER="../../data/_indri_xml_topic/";
	public static String TOTAL_WORD_STATS="../../data/train.word_stats.txt";
	public static String OUTPUT_FOLDER="../../data/result/"; // where to store the result
	public static String TOPIREL_FILE="../../data/top_irels.txt"; // store the top irrelevant files
	public static String INV_LIST_FOLDER="../../data/_indri_inv_train/";

	public static boolean CHECK_FACT=false; //whether I check the fact, if make a relevant tweet decision
	public static boolean USE_TWEET_EXPAND=false; // use expanded tweet or not
	public static boolean TEST_MODE=false; //whether we are using the test dataset
	
	public static int QUERY_START=1;
	public static int QUERY_END=49;
	public static double INIT_WEIGHT=0.3;
	public static int SLAVE_NUM=0;

	public static String RUN_NAME="yitongz-baseline"; // The run name
	public static String VECTOR_MODE="BM25"; // "BM25" "Normal" "KL"

	public static void switch_to_test(){
		TEST_MODE=true;
		QUERY_FILE="../../data/2012.topics.MB1-50.filtering-wrapped.txt";
		TRUE_RESULT_FILE="../../data/filtering-qrels.txt";
		TOTAL_WORD_STATS="../../data/word_stats.txt";
		INV_LIST_FOLDER="../../data/_indri_inv/";
	}
}