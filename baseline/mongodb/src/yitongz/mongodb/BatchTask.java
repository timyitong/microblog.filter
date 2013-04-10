package yitongz.mongodb;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class BatchTask{
	public BatchTask(){
		run();
	}
	private void run(){
		Printer.newPrinter();
		for (int i=Configure.QUERY_START;i<=Configure.QUERY_END ;i++){
			ArrayList<Tweet> resultList=new ArrayList<Tweet> ();
			Stack<Tweet> resultStack=new Stack<Tweet>();

			//CHECK MODE decide which to skip
			if (Configure.TEST_MODE){
				if (i%5==1 || i==18) continue; // the 18 is missing all needed to run tweets
			}else{
				if (i%5!=1) continue;
			}

			//SET Word Statistics Dictionary
			WordStats.setCurrentQueryID(i);
			//GET the query:
			Query query=new Query(i);

			// GET tweets list
			// The original readin list is in Descending time order
			// we need to sort them:
			ArrayList<String> tweets_list=new TweetForCheckList().getList(i);
			Collections.sort(tweets_list);

			Iterator <String> tweets=tweets_list.iterator();
			
			//CHECK each tweet
			while (tweets.hasNext()){
				String tweet_id=tweets.next();
				//NEW the tweet:
				Tweet tweet=new Tweet(tweet_id); 
				tweet.query_num=query.num;
				
				//Ignore tweets do not have URL
				//Only improve precisions, do not help recall very much
				//if (tweet==null || tweet.hasurls==null || tweet.hasurls.trim().equals("false")){
				//	tweet.relevant=false;
				//	tweet.score=-10;
				//}else{
					//CHECK whether this TWEET relevant to this QUERY
					if (tweet.vector==null){
						tweet.relevant=false;
						tweet.score=-10;
					}else{
						tweet.relevant=query.checkIn(tweet);
					}
				//}
				//ADD tweet to RESULT
				resultStack.add(tweet);
			}
			for (Tweet t: resultStack){
				resultList.add(t);
			}
			//System.out.println(i);
			Printer.printFilterResult(resultList);

			//if (i==Configure.QUERY_START)
			//	System.out.println(query.centroid_list);
		}
		//After all printing close the printer
		Printer.close();
	}	
}