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
		ArrayList<Tweet> resultList=new ArrayList<Tweet> ();
		for (int i=2;i<=49 ;i++){
			Stack<Tweet> resultStack=new Stack<Tweet>();

			if (i%5==1 || i==18) continue;

			/*GET the query:*/
			Query query=new Query(i);

			/*The original readin list is in Descending time order
			 * we need to sort them:
			 */
			ArrayList<String> tweets_list=TweetForCheckList.getList(i);
			Collections.sort(tweets_list);

			Iterator <String> tweets=tweets_list.iterator();
			
			//RelevantScore scorer=new BaselineScore();
			/*
			 * Notice this kNN method, can retrieve a very high recall collection
			 * The tweet should first pass this test
			 */
			RelevantScore scorer=new IndriPRFScore();

			double cutoff;
			double s;
			//double cutoff=scorer.getCutoff();
			while (tweets.hasNext()){
				String tweet_id=tweets.next();
				/*GET the tweet:*/
				Tweet tweet=new Tweet(tweet_id); 
				tweet.query_num=query.num;

				cutoff=scorer.getCutoff(tweet,query);
				s=scorer.getScore(tweet,query);
				tweet.score=s;
				/*Here we pass the first high recall filtering*/
				tweet.relevant= s>cutoff ? true : false;
				//if (tweet.relevant){
					/* Let us see whether the query centroid will accept it or not
					 * Only these tweets pass this test could be the true tweet.
					 */
					tweet.relevant=query.checkIn(tweet);
				//}

				resultStack.add(tweet);
				//System.out.println(tweet.relevant);
			}
			for (Tweet t: resultStack){
				resultList.add(t);
			}
		}

		Printer.printFilterResult(resultList);
	}	
}