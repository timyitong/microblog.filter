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
			ArrayList<String> tweets_list=TweetForCheckList.getList(i);
			Collections.sort(tweets_list);

			Iterator <String> tweets=tweets_list.iterator();
			
			//CHECK each tweet
			while (tweets.hasNext()){
				String tweet_id=tweets.next();
				//NEW the tweet:
				Tweet tweet=new Tweet(tweet_id); 
				tweet.query_num=query.num;

				//CHECK whether this TWEET relevant to this QUERY
				tweet.relevant=query.checkIn(tweet);

				//ADD tweet to RESULT
				resultStack.add(tweet);
			}
			for (Tweet t: resultStack){
				resultList.add(t);
			}
			//System.out.println(i);
			Printer.printFilterResult(resultList);
		}
		//After all printing close the printer
		Printer.close();
	}	
}