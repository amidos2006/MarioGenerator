package fi2pop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import shared.SlicesLibrary;

public class GeneticAlgorithm {
    private int _populationSize;
    private int _chromosomeLength;
    private int _appendingSize;
    private double _crossover;
    private double _mutation;
    private int _elitism;
    private int _selectionMethod;
    private Random _rnd;
    private SlicesLibrary _lib;
    
    public GeneticAlgorithm(SlicesLibrary lib, int populationSize, int chromosomeLength, int appendingSize, double crossover,
							double mutation, int elitism) {
	this(lib, populationSize, chromosomeLength, appendingSize, crossover, mutation, elitism, 0);
    }

    public GeneticAlgorithm(SlicesLibrary lib, int populationSize, int chromosomeLength, int appendingSize, double crossover,
							double mutation, int elitism, int selectionMethod) {
	this._lib = lib;
	this._populationSize = populationSize;
	this._chromosomeLength = chromosomeLength;
	this._appendingSize = appendingSize;
	this._crossover = crossover;
	this._mutation = mutation;
	this._elitism = elitism;
	this._selectionMethod = selectionMethod;
	this._rnd = new Random();
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
    
    private Chromosome[][] getFeasibleInfeasible(Chromosome[] pop){
	ArrayList<Chromosome> feasible = new ArrayList<Chromosome>();
	ArrayList<Chromosome> infeasible = new ArrayList<Chromosome>();
	for(int i=0; i<pop.length; i++) {
	    if(pop[i].getConstraints() < 1) {
		infeasible.add(pop[i]);
	    }
	    else {
		feasible.add(pop[i]);
	    }
	}
	return new Chromosome[][] {feasible.toArray(new Chromosome[0]), infeasible.toArray(new Chromosome[0]) };
    }


    public double averageFitness(Chromosome[] pop) {
    	double fitAvg = 0;
		for (Chromosome c : pop) {
			fitAvg += c.getFitness();
		}

		return fitAvg / pop.length;
	}

	public double averageConstraint(Chromosome[] pop) {
		double constAvg = 0;
		for (Chromosome c : pop) {
			constAvg += c.getConstraints();
		}
		return constAvg / pop.length;
	}

	public int getMaxFitnessIndex(Chromosome[] pop) {
		double max = -100;
		int maxIndex = -1;
    	for (int i = 0; i < pop.length; i++) {
			double fit = pop[i].getFitness();
			if(fit > max) {
				max = fit;
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	public double getMaxFitness(Chromosome[] pop) {
		double max = -100;
		for (Chromosome c : pop) {
			double fit = c.getFitness();
			if(fit > max) {
				max = fit;
			}
		}
		return max;
	}
    private void calculateFitness(Chromosome[] pop) {
	for (Chromosome c : pop) {
	    c.calculateFitness();
	}
    }
    
    public Chromosome[] evolve(double time) {
	Chromosome[] currentPopulation = new Chromosome[this._populationSize];
	for (int i = 0; i < currentPopulation.length; i++) {
	    currentPopulation[i] = new Chromosome(this._rnd, this._lib, this._chromosomeLength, this._appendingSize);
	    currentPopulation[i].randomInitialize();
	}
	long startTime = System.currentTimeMillis();
	Chromosome[] newPopulation = new Chromosome[currentPopulation.length];
	while (System.currentTimeMillis() - startTime < time) {
	    this.calculateFitness(currentPopulation);
	    Chromosome[][] twoPop = this.getFeasibleInfeasible(currentPopulation);
	    Arrays.sort(twoPop[0]);
	    Arrays.sort(twoPop[1]);
	    for (int i = 0; i < newPopulation.length - this._elitism; i++) {
		Chromosome[] usedPopulation = twoPop[1];
		if(this._rnd.nextDouble() < (double)(twoPop[0].length) / currentPopulation.length) {
		    usedPopulation = twoPop[0];
		}
		Chromosome parent1 = this.rankSelection(usedPopulation);
		if(this._selectionMethod > 0){
		    parent1 = this.tournmentSelection(usedPopulation);
		}
		Chromosome child = parent1.clone();
		if (this._rnd.nextDouble() < this._crossover) {
		    Chromosome parent2 = this.rankSelection(usedPopulation);
		    if(this._selectionMethod > 0){
			    parent2 = this.tournmentSelection(usedPopulation);
		    }
		    child = parent1.crossover(parent2);
		}
		if (this._rnd.nextDouble() < this._mutation) {
		    child = child.mutate();
		}
		newPopulation[i] = child;
	    }
	    for(int i=0; i<this._elitism; i++){
		if(i < twoPop[0].length) {
		    newPopulation[newPopulation.length - 1 - i] = twoPop[0][i];
		}
		else {
		    newPopulation[newPopulation.length - 1 - i] = twoPop[1][i];
		}
	    }
	    currentPopulation = newPopulation;
	}
	this.calculateFitness(currentPopulation);
	return currentPopulation;
    }
}
