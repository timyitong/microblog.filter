package yitongz.mongodb;
public interface VectorCalculator{
	public void add(DocVector d);
	public void minus(DocVector d);
	public void multiply(double c);
	public double innerProduct(DocVector d);
	public double mod();
}