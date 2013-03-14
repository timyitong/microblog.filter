package yitongz.mongodb;

import java.util.Iterator;
import java.util.ArrayList;

public class BatchTask{
	public BatchTask(){
		run();
	}
	private void run(){
		ArrayList<Tweet> resultList=new ArrayList<Tweet> ();
		for (int i=2;i<=49;i++){
			if (i%5==1 || i==18) continue;

			/*GET the query:*/
			Query query=new Query(i);

			Iterator <String> tweets=TweetForCheckList.iterator(i);
			RelevantScore scorer=new BaselineScore();
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
				tweet.relevant= s>cutoff ? true : false;
				resultList.add(tweet);
			}
		}

		Printer.printFilterResult(resultList);
	}	
}