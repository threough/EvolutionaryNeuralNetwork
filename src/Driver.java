import GeneticAlgorithm.DataSet;
import GeneticAlgorithm.GeneticAlgorithm;
import NeuralNetwork.NeuralNetwork;

public class Driver {
	public static void main(String[] args) {
		DataSet data = new DataSet(2,1);
		data.addData(new double[] {0,0}, new double[] {0});
		data.addData(new double[] {0,1}, new double[] {1});
		data.addData(new double[] {1,0}, new double[] {1});
		data.addData(new double[] {1,1}, new double[] {1});
		
		GeneticAlgorithm GA = new GeneticAlgorithm(2, 1, 2, 2, 30, 0.15, 0.15, data);
		
		GA.go();
	}
	
}
