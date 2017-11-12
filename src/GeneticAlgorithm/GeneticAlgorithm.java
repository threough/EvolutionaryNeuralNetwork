package GeneticAlgorithm;
import java.util.Random;
import NeuralNetwork.NeuralNetwork;

public class GeneticAlgorithm {
	private int chromosomeLength;
	private int popSize;
	private int nInputs;
	private int nOutputs;
	private int nNeurons;
	private int nLayers;
	private double crossoverProbability;
	private double mutationProbability;
	private Random r;
	private double[][] population;
	DataSet learningData;
	
	/**
	 * Genetic algorithm for neural network optimization
	 * @param nInputs Number of inputs
	 * @param nOutputs Number of outputs
	 * @param nLayers Number of hidden layers for the neural networks
	 * @param nNeurons Number of neurons per hidden layer for the neural network
	 * @param popSize The population size 
	 * @param crossoverProbability probability for crossover operator
	 * @param mutationProbability probability for mutation operator
	 * @param learningData DataSet containing data to learn from
	 */
	public GeneticAlgorithm(int nInputs, int nOutputs, int nLayers, int nNeurons, 
			int popSize, double crossoverProbability, double mutationProbability, DataSet learningData) {
		this.chromosomeLength = nNeurons*(nInputs + nNeurons*(nLayers-1) + nOutputs);
		this.r = new Random();
		this.popSize = popSize;
		this.learningData = learningData;
		this.nInputs = nInputs;
		this.nOutputs = nOutputs;
		this.nLayers = nLayers;
		this.nNeurons = nNeurons;
		this.crossoverProbability = crossoverProbability;
		this.mutationProbability = mutationProbability;
		
		// initialize population
		this.population = new double[popSize][];
		for (int i = 0; i < popSize; i++) {
			population[i] = randomChromosome();
		}
		
	} // end constructor
	
	public NeuralNetwork optimize() {
		for (int i = 0; i < 1000; i++) {
			population = iterate(population);
		}
		NeuralNetwork[] results = generateNetworks(population);
		double[] fitness = this.fitnessPop(results, learningData);
		
		// find the best network
		double max = 0;
		int maxIndex = 0;
		for (int i = 0; i < fitness.length; i++) {
			if (fitness[i] > max) {
				maxIndex = i;
				max = fitness[i];
			}
		}
		
		return results[maxIndex];
		
	}
	
	public double[][] iterate(double[][] population) {
		NeuralNetwork[] networks = generateNetworks(population);
		double[] fitness = fitnessPop(networks, learningData);
		double sumFitness = 0;
		double previous = 0;
		double parent1;
		double parent2;
		int parent1Index = 0;
		int parent2Index = 0;
		double[][] newPopulation = new double[population.length][chromosomeLength];
		
		// construct 'roulette ranges'
		for (int i = 0; i < fitness.length; i++) {
			sumFitness += fitness[i];
		}
		
		for (int i = 0; i < fitness.length; i++) {
			fitness[i] = (fitness[i] / sumFitness) + previous;
			previous = fitness[i];
		}
		
		int index = 0;
		while (index < popSize) {
			// select two parents based on fitness
			parent1 = r.nextDouble();
			parent2 = r.nextDouble();
			parent1Index = 0;
			parent2Index = 0;
	
			while (fitness[parent1Index] < parent1 && parent1Index < fitness.length-1) {
				parent1Index++;
			}
			while (fitness[parent2Index] < parent2 && parent2Index < fitness.length-1) {
				parent2Index++;
			}
			
			double[][] children;
		
			if (r.nextDouble() < crossoverProbability) {
				children = crossover(population[parent1Index], population[parent2Index]);
			} else {
				children = new double[][] {population[parent1Index], population[parent2Index]};
			}
			
			if (r.nextDouble() < mutationProbability) {
				children[0] = mutation(children[0]);
				children[1] = mutation(children[1]);
			}
			
			newPopulation[index++] = children[0];
			newPopulation[index++] = children[1];
		}
		return newPopulation;
	}

	/**
	 * Generates a random chromosome
	 * @return Randomly generated chromosome with elements between -0.5 and 0.5
	 */
	public double[] randomChromosome() {
		double[] newChromosome = new double[chromosomeLength];
		for (int i = 0; i < chromosomeLength; i++) {
			newChromosome[i] = r.nextDouble() - 0.5;
		}
		return newChromosome;
	} // end randomChromosome()
	
	/**
	 * Generates a set of neural networks from a population of chromosomes
	 * @param population 
	 */
	public NeuralNetwork[] generateNetworks(double[][] population) {
		NeuralNetwork[] networks = new NeuralNetwork[popSize];
		for (int i = 0; i < popSize; i++) {
			networks[i] = new NeuralNetwork(population[i], nInputs, nOutputs, nLayers, nNeurons);
		}
		return networks;
	} // end generateNetworks()
	
	/**
	 * Calculate the fitness of a given neural network given learning data
	 * @param network Neural network to calculate fitness of 
	 * @param learningData Data to learn from
	 * @return fitness of the network
	 */
	public double fitness(NeuralNetwork network, DataSet learningData) {
		double[] activationResult;
		double[] inputs;
		double[] expectedOutputs;
		double error;
		double fitness = 0;
		
		for (int i = 0; i < learningData.getSize(); i++) {
			error = 0;
			inputs = learningData.getInputs(i);
			expectedOutputs = learningData.getOutputs(i);
			activationResult = network.feedForward(inputs);
			
			for (int j = 0; j < expectedOutputs.length; j++) {
				error += expectedOutputs[j] - activationResult[j];
			}
			if (error == 0) {
				fitness += 100;
			} else {
				fitness += 1/(error*error);
			}
		}		
		
		return fitness;
	} // end fitness()
	
	/**
	 * Calculate the fitness for a population of networks
	 * @param networks Array of networks to calculate fitness of
	 * @param learningData Data for networks to learn from
	 * @return Double array of fitness for each network
	 */
	public double[] fitnessPop(NeuralNetwork[] networks, DataSet learningData) {
		double[] result = new double[popSize];
		for (int i = 0; i < popSize; i++) {
			result[i] = fitness(networks[i], learningData);
		}
		return result;
	} // end fitnessPop()
	
	/**
	 * Crossover genetic operator, simple single point crossover
	 * @param parent1 First parent
	 * @param parent2 Second parent
	 * @return Array of two child chromosomes resulting from the crossover
	 */
	public double[][] crossover(double[] parent1, double[] parent2) {
		int point = r.nextInt(chromosomeLength);
		
		double[] child1 = new double[chromosomeLength];
		double[] child2 = new double[chromosomeLength];
		
		for (int i = 0; i < point; i++) {
			child1[i] = parent1[i];
			child2[i] = parent2[i];
		}
		
		for (int i = point; i < chromosomeLength; i++) {
			child1[i] = parent2[i];
			child2[i] = parent1[i];
		}
		
		return new double[][] {child1, child2};
	} // end crossover()
	
	/**
	 * Mutation genetic operator, simple Gaussian mutation
	 * @param chromosome Chromosome to be mutated
	 * @return A mutated version of the chromosome
	 */
	public double[] mutation(double[] chromosome) {
		double[] child = chromosome.clone();
		int point = r.nextInt(chromosomeLength);
		child[point] += r.nextGaussian();
		return child;
	} // end mutation()
	
	public String toString() {
		String result = "";
		for (int i = 0; i < population.length; i++) {
			for (int j = 0; j < population[i].length; j++) {
				result += population[i][j] + " ";
			}
			result += "\n";
		}
		
		return result;
	} // end toString()
}