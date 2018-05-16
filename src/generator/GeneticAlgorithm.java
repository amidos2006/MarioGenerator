package generator;

import java.util.Arrays;
import java.util.Random;

public class GeneticAlgorithm {
    private int _populationSize;
    private int _chromosomeLength;
    private double _crossover;
    private double _mutation;
    private int _elitism;
    private int _selectionMethod;
    private Random _rnd;
    private LevelSlicesLibrary _lib;
    
    public GeneticAlgorithm(LevelSlicesLibrary lib, int populationSize, int chromosomeLength, double crossover,
	    double mutation, int elitism) {
	this(lib, populationSize, chromosomeLength, crossover, mutation, elitism, 0);
    }

    public GeneticAlgorithm(LevelSlicesLibrary lib, int populationSize, int chromosomeLength, double crossover,
	    double mutation, int elitism, int selectionMethod) {
	this._lib = lib;
	this._populationSize = populationSize;
	this._chromosomeLength = chromosomeLength;
	this._crossover = crossover;
	this._mutation = mutation;
	this._elitism = elitism;
	this._selectionMethod = selectionMethod;
	this._rnd = new Random();
    }
    
    private void calculateFitness(Chromosome[] pop){
	for (Chromosome c : pop) {
	    c.getFitness();
	}
	Arrays.sort(pop);
    }
    
    private Chromosome tournmentSelection(Chromosome[] pop){
	Chromosome[] tournment = new Chromosome[this._selectionMethod];
	for(int i=0; i<tournment.length; i++){
	    tournment[i] = pop[this._rnd.nextInt(pop.length)];
	}
	Arrays.sort(tournment);
	return tournment[0];
    }

    private Chromosome rankSelection(Chromosome[] pop) {
	double[] ranks = new double[pop.length];
	double total = 0;
	for (int i = 0; i < pop.length; i++) {
	    ranks[i] = pop.length - i;
	    total += ranks[i];
	}
	for (int i = 1; i < pop.length; i++) {
	    ranks[i] = ranks[i] + ranks[i - 1];
	}
	for (int i = 0; i < pop.length; i++) {
	    ranks[i] /= total;
	}
	double value = this._rnd.nextDouble();
	for (int i = 0; i < pop.length; i++) {
	    if (value < ranks[i]) {
		return pop[i];
	    }
	}
	return pop[pop.length - 1];
    }

    public Chromosome[] evolve(double time) {
	Chromosome[] currentPopulation = new Chromosome[this._populationSize];
	for (int i = 0; i < currentPopulation.length; i++) {
	    currentPopulation[i] = new Chromosome(this._rnd, this._lib, this._chromosomeLength);
	    currentPopulation[i].randomInitialize();
	}
	long startTime = System.currentTimeMillis();
	Chromosome[] newPopulation = new Chromosome[currentPopulation.length];
	while (System.currentTimeMillis() - startTime < time) {
	    this.calculateFitness(currentPopulation);
	    for (int i = 0; i < newPopulation.length - this._elitism; i++) {
		Chromosome parent1 = this.rankSelection(currentPopulation);
		if(this._selectionMethod > 0){
		    parent1 = this.tournmentSelection(currentPopulation);
		}
		Chromosome child = parent1.clone();
		if (this._rnd.nextDouble() < this._crossover) {
		    Chromosome parent2 = this.rankSelection(currentPopulation);
		    if(this._selectionMethod > 0){
			    parent2 = this.tournmentSelection(currentPopulation);
		    }
		    child = parent1.crossover(parent2);
		}
		if (this._rnd.nextDouble() < this._mutation) {
		    child = child.mutate();
		}
		newPopulation[i] = child;
	    }
	    for(int i=0; i<this._elitism; i++){
		newPopulation[newPopulation.length - 1 - i] = currentPopulation[i];
	    }
	    currentPopulation = newPopulation;
	}
	this.calculateFitness(currentPopulation);
	return currentPopulation;
    }
}
