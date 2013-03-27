package yitongz.mongodb;
import java.util.ArrayList;
public interface SimScoreCalculator{
	public double getSimScore(Tweet t1, Tweet t2);
	public double getSimScore(Tweet t, Query q, ArrayList <Centroid> list);
}