package yitongz.mongodb;

public class Centroid implements Comparable <Centroid>{
	public Tweet tweet;
	public boolean relevant;
	public double score;
	public double query_score;
	public Centroid (Tweet t){
		tweet=t;
	}	
	public Centroid clone(){
		return new Centroid(new Tweet(tweet.tweetid));
	}
	public int compareTo(Centroid c){
		if (this.score > c.score)
			return -1;
		else if (this.score == c.score)
			return 0;
		else 
			return 1;
	}
}