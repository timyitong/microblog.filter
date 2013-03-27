package yitongz.mongodb;
public class Calculator{
	public SimScoreCalculator sim;
	private static Calculator instance=new Calculator();
	private Calculator(){
		sim=new RochhioSimScore();
	}
	public static Calculator getInstance(){
		return instance;
	} 
}