package shared;

import java.util.HashMap;
import java.util.Random;

import ch.idsia.mario.engine.level.Level;

public abstract class Chromosome implements Comparable<Chromosome> {
    protected Random _rnd;
    protected int[] _genes;
    protected int _appendingSize;
    protected SlicesLibrary _library;
    protected double _constraints;
    protected double _fitness;
    protected boolean _algorithmRan;

    public Chromosome(Random rnd, SlicesLibrary lib, int size, int appendingSize) {
	this._genes = new int[size];
	this._appendingSize = appendingSize;
	this._rnd = rnd;
	this._library = lib;
	this._fitness = 0;
	this._constraints = 0;
	this._algorithmRan = false;
    }

    public void randomInitialize() {
	for (int i = 0; i < this._genes.length; i++) {
	    this._genes[i] = this._rnd.nextInt(this._library.getNumberOfSlices());
	}
    }

    public void stringInitialize(String level) {
	String[] parts = level.split(",");
	for (int i = 0; i < this._genes.length; i++) {
	    this._genes[i] = Integer.parseInt(parts[i]);
	}
    }

    public abstract void childEvaluationInitialization(String values);

    public double getConstraints() {
	return this._constraints;
    }

    public double getFitness() {
	return this._fitness;
    }
    
    private void calculateFitnessBasedOnEntropy() {
	HashMap<Character, Integer> stats = new HashMap<>();
	int[] horDerivative = new int[2];
	for (int i = 0; i < this._genes.length; i++) {
	    String slice = this._library.getSlice(this._genes[i]);
	    for (int j = 0; j < slice.length(); j++) {
		if(i > 0) {
		    String prevSlice = this._library.getSlice(this._genes[i - 1]);
		    if(slice.charAt(j) != prevSlice.charAt(j)) {
			horDerivative[1] += 1;
		    }
		    else {
			horDerivative[0] += 1;
		    }
		}
		Character c = slice.charAt(j);
		if(!stats.containsKey(c)) {
		    stats.put(c, 0);
		}
		stats.put(c, stats.get(c) + 1);
	    }
	}
	
	double entropy = 0;
	for(Character key:stats.keySet()) {
	    double prob = (1.0 * stats.get(key)) / (1.0*this._genes.length * this._library.getSlice(0).length());
	    if(prob > 0) {
		entropy += -prob * Math.log10(prob);
	    }
	}
	double derEntropy = 0;
	for(int i=0; i<horDerivative.length; i++) {
	    double prob = (1.0 * horDerivative[i]) / (1.0 * (this._genes.length - 1) * this._library.getSlice(0).length());
	    if(prob > 0) {
		derEntropy += - prob * Math.log10(prob);
	    }
	}
	
	this._fitness = ((1 - entropy) + 3 * (1 - derEntropy)) / 4.0;
    }
    
    private void calculateFitnessBasedOnEmptiness() {
	float total = 0;
	float empty = 0;
	float isolated = 0;
	for (int i = 0; i < this._genes.length; i++) {
	    String slice = this._library.getSlice(this._genes[i]);
	    for (int j = 0; j < slice.length(); j++) {
		if (Level.isEmpty(slice.charAt(j))) {
		    empty += 1;
		    if ((j == 0 || !Level.isEmpty(slice.charAt(j - 1)))
			    && (j == slice.length() - 1 || !Level.isEmpty(slice.charAt(j + 1)))) {
			isolated += 1;
		    }
		    if (j == slice.length() - 1
			    && (i == 0 || !Level.isEmpty(this._library.getSlice(this._genes[i - 1]).charAt(j)))
			    && (i == this._genes.length - 1
				    || !Level.isEmpty(this._library.getSlice(this._genes[i + 1]).charAt(j)))) {
			isolated += 1;
		    }
		    float surround = 0;
		    if (j > 0 && !Level.isEmpty(slice.charAt(j - 1))) {
			surround += 1;
		    }
		    if (j < slice.length() - 1 && !Level.isEmpty(slice.charAt(j + 1))) {
			surround += 1;
		    }
		    if (i > 0 && !Level.isEmpty(this._library.getSlice(this._genes[i - 1]).charAt(j))) {
			surround += 1;
		    }
		    if (i < this._genes.length - 1
			    && !Level.isEmpty(this._library.getSlice(this._genes[i + 1]).charAt(j))) {
			surround += 1;
		    }
		    if (surround > 1) {
			isolated += 1;
		    }
		} else {
		    if ((j == 0 || Level.isEmpty(slice.charAt(j - 1)))
			    && (j == slice.length() - 1 || Level.isEmpty(slice.charAt(j + 1)))) {
			isolated += 1;
		    }
		    if (j != slice.length() - 1
			    && (i == 0 || Level.isEmpty(this._library.getSlice(this._genes[i - 1]).charAt(j)))
			    && (i == this._genes.length - 1
				    || Level.isEmpty(this._library.getSlice(this._genes[i + 1]).charAt(j)))) {
			isolated += 1;
		    }
		    float surround = 0;
		    if (j > 0 && !Level.isEmpty(slice.charAt(j - 1))) {
			surround += 1;
		    }
		    if (j < slice.length() - 1 && !Level.isEmpty(slice.charAt(j + 1))) {
			surround += 1;
		    }
		    if (i > 0 && !Level.isEmpty(this._library.getSlice(this._genes[i - 1]).charAt(j))) {
			surround += 1;
		    }
		    if (i < this._genes.length - 1
			    && !Level.isEmpty(this._library.getSlice(this._genes[i + 1]).charAt(j))) {
			surround += 1;
		    }
		    if (surround == 0) {
			isolated += 1;
		    }
		}
		total += 1;
	    }
	}
	this._fitness = (empty - 2 * isolated) / total;
    }

    protected void calculateFitness(boolean entropy) {
	if(entropy) {
	    this.calculateFitnessBasedOnEntropy();
	}
	else {
	    this.calculateFitnessBasedOnEmptiness();
	}
    }

    public abstract void runAlgorithms(HashMap<String, String> parameters);

    public abstract Chromosome clone();

    public Chromosome mutate() {
	Chromosome mutated = this.clone();
	mutated._genes[mutated._rnd.nextInt(mutated._genes.length)] = mutated._rnd
		.nextInt(mutated._library.getNumberOfSlices());
	return mutated;
    }

    public Chromosome crossover(Chromosome c) {
	Chromosome child = this.clone();
	int index1 = child._rnd.nextInt(child._genes.length);
	int index2 = child._rnd.nextInt(child._genes.length);
	if (index1 > index2) {
	    int temp = index2;
	    index2 = index1;
	    index1 = temp;
	}
	for (int i = index1; i < index2 + 1; i++) {
	    child._genes[i] = c._genes[i];
	}
	return child;
    }

    public String getGenes() {
	String result = "" + this._genes[0];
	for (int i = 1; i < this._genes.length; i++) {
	    result += "," + this._genes[i];
	}
	return result;
    }

    public String toString() {
	String level = "";
	int height = this._library.getSlice(this._genes[0]).length();
	for (int i = 0; i < height; i++) {
	    String appendingChar = "-";
	    if (i == height - 1) {
		appendingChar = "X";
	    }
	    for (int k = 0; k < this._appendingSize; k++) {
		level += appendingChar;
	    }
	    for (int j = 0; j < this._genes.length; j++) {
		level += this._library.getSlice(this._genes[j]).charAt(i);
	    }
	    for (int k = 0; k < this._appendingSize; k++) {
		level += appendingChar;
	    }
	    level += "\n";
	}

	return level;
    }

    @Override
    public int compareTo(Chromosome o) {
	if (this._constraints == 1) {
	    return (int) Math.signum(this._fitness - o._fitness);
	}
	return (int) Math.signum(this._constraints - o._constraints);
    }
}
