package yitongz.mongodb;

import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.File;

import java.util.logging.Logger;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TweetCrawler{
	private final Logger logger = Logger.getLogger(TweetCrawler.class.getName());
	private Twitter twitter=null;
	public TweetCrawler(){
		//crawl();
	}
	private void crawl(){
		try{
			LinkedList <Tweet> list=getList();
			for (Tweet t: list){
				String id=t.tweetid;
				t.clean_tweet=fetchTweet(id);
				t.save();
				System.out.println(t.toString());
				Thread.sleep(5010L);
			}
		}catch(Exception e){e.printStackTrace();}
	}
	public void output(){
		LinkedList <Tweet> list=getList();
		try{
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File("../../data/tweets_crawled.txt")));
			for (Tweet t: list){
				if (t.clean_tweet!=null && t.user==null){
					bw.write(t.tweetid+" "+t.clean_tweet);
					bw.newLine();
				}
			}
			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void getTwitter(){
		String message="Twitter application using Java http://www.java-tutorial.ch/architecture/twitter-with-java-tutorial";
		try{
			twitter = new TwitterFactory().getInstance();
			try {
				RequestToken requestToken = twitter.getOAuthRequestToken();
				AccessToken accessToken = null;
				while (null == accessToken) {
					logger.fine("Open the following URL and grant access to your account:");
					logger.fine(requestToken.getAuthorizationURL());
					try {
						accessToken = twitter.getOAuthAccessToken(requestToken);
					} catch (TwitterException te) {
					if (401 == te.getStatusCode()) {
							logger.severe("Unable to get the access token.");
						} else {
							te.printStackTrace();
						}
					}
				}
				logger.info("Got access token.");
				logger.info("Access token: " + accessToken.getToken());
				logger.info("Access token secret: " + accessToken.getTokenSecret());
			} catch (IllegalStateException ie) {
				// access token is already available, or consumer key/secret is not set.
				if (!twitter.getAuthorization().isEnabled()) {
					logger.severe("OAuth consumer key/secret is not set.");
				}
			}
		}catch(TwitterException te){
			te.printStackTrace();
			logger.severe("Failed to get timeline: " + te.getMessage());
		}
	}
	private String fetchTweet(String id){
		String result=null;
		if (twitter==null)
			getTwitter();

		try {
			Status status = twitter.showStatus(new Long(id));
			result=status.getText();
		} catch (TwitterException te) {
			te.printStackTrace();
			logger.severe("Failed to get timeline: " + te.getMessage());
		}

		return result;
	}
	private LinkedList <Tweet> getList(){
		LinkedList <Tweet> list=new LinkedList <Tweet> ();
		try{
			BufferedReader br=new BufferedReader(new FileReader(new File(Configure.TRUE_RESULT_FILE)));
			String line=null;
			while (br.ready()){
				line=br.readLine();
				StringTokenizer st=new StringTokenizer(line);
				st.nextToken();
				st.nextToken();
				Tweet t=new Tweet(st.nextToken());
				if (Integer.parseInt(st.nextToken())>0 && t!=null && t.tweetid!=null && t.user==null ){
					list.add(t);
				}
			}
			Collections.sort(list);
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("list ready");
		return list;
	}
}