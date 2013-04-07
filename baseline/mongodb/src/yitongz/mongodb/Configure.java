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
	public static String MODE="Centroid"; // | "Centroid" "SVM"

	public static boolean CHECK_FACT=false; //whether I check the fact, if make a relevant tweet decision
	public static boolean USE_TWEET_EXPAND=false; // use expanded tweet or not
	public static boolean TEST_MODE=false; //whether we are using the test dataset
	
	public static int QUERY_START=1;
	public static int QUERY_END=49;
	public static double INIT_WEIGHT=0.1;
	public static int SLAVE_NUM=42;
	public static int TOP_IR_START_POINT=0;//350;

	public static double PREC_LIMIT=0.15;
	public static double AUGUMENT_PACE=1.01;

	public static String RUN_NAME="yitongz-baseline"; // The run name
	public static String VECTOR_MODE="BM25"; // "BM25" "Normal" "KL"

	//Rochhio Pseudo Relevance Feedback
	public static double ROCHHIO_A=1.3;
	public static double ROCHHIO_B=3.4;
	public static double ROCHHIO_C=0.6;
	public static double EXPAND_QUERY_WEIGHT=1.1;
	public static double EXPAND_QUERY2_WEIGHT=0.09;

	//BM25
	public static int BM25_AVG_LENGTH=20;
	public static double BM25_K1=2.95;
	public static double BM25_B=0.75;
	public static double BM25_K3=0;


	public static void switch_to_test(){
		TEST_MODE=true;
		QUERY_FILE="../../data/2012.topics.MB1-50.filtering-wrapped.txt";
		TRUE_RESULT_FILE="../../data/filtering-qrels.txt";
		TOTAL_WORD_STATS="../../data/word_stats.txt";
		INV_LIST_FOLDER="../../data/_indri_inv/";
		TOPIREL_FILE="../../data/test.top_irels.txt";
	}
}