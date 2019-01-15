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

    public Chromosome(Random rnd, SlicesLibrary lib, int size, int appendingSize) {
	this._genes = new int[size];
	this._appendingSize = appendingSize;
	this._rnd = rnd;
	this._library = lib;
	this._fitness = 0;
	this._constraints = 0;
    }

    public void randomInitialize() {
	for (int i = 0; i < this._genes.length; i++) {
	    this._genes[i] = this._rnd.nextInt(this._library.getNumberOfSlices());
	}
    }
    
    public void stringInitialize(String level) {
	String[] parts = level.split(",");
	for(int i=0; i<this._genes.length; i++) {
	    this._genes[i] = Integer.parseInt(parts[i]);
	}
    }
    
    public abstract void childEvaluationInitialization(String values) ;

    public double getConstraints() {
	return this._constraints;
    }

    public double getFitness() {
	return this._fitness;
    }

    protected void calculateFitness() {
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
    
    public abstract void runAlgorithms(HashMap<String, String> parameters);

    public abstract Chromosome clone();

    public Chromosome mutate() {
	Chromosome mutated = this.clone();
	mutated._genes[mutated._rnd.nextInt(mutated._genes.length)] = mutated._rnd.nextInt(mutated._library.getNumberOfSlices());
	return mutated;
    }

    public Chromosome crossover(Chromosome c) {
	Chromosome child = this.clone();
	int index1 = child._rnd.nextInt(child._genes.length);
	int index2 = child._rnd.nextInt(child._genes.length);
	if(index1 > index2){
	    int temp = index2;
	    index2 = index1;
	    index1 = temp;
	}
	for(int i=index1; i<index2 + 1; i++){
	    child._genes[i] = c._genes[i];
	}
	return child;
    }

    public String getGenes() {
	String result = "" + this._genes[0];
	for(int i=1; i<this._genes.length; i++) {
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
	if(this._constraints == 1) {
	    return (int)Math.signum(this._fitness - o._fitness);
	}
	return (int)Math.signum(this._constraints - o._constraints);
    }
}
