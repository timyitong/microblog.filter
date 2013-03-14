package yitongz.mongodb;
public interface RelevantScore{
	public double getScore(Tweet t, Query q);
	public double getCutoff(Tweet t, Query q);
}