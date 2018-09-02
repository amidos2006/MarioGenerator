package fi2pop;

import ch.idsia.tools.AgentResultObject;
import ch.idsia.tools.RunGivenLevel;
import shared.SlicesLibrary;

import java.util.Random;

public class Chromosome implements Comparable<Chromosome> {
    private Random _rnd;
    private int[] _section;
    private int _appendingSize;
    private SlicesLibrary _lib;
    private double _constraints;
    private double _fitness;
    private boolean _fitnessCalculated;

    public Chromosome(Random rnd, SlicesLibrary lib, int size, int appendingSize) {
	this._section = new int[size];
	this._appendingSize = appendingSize;
	this._rnd = rnd;
	this._lib = lib;
	this._fitness = 0;
	this._constraints = 0;
	this._fitnessCalculated = false;
    }

    public void randomInitialize() {
	for (int i = 0; i < this._section.length; i++) {
	    this._section[i] = this._rnd.nextInt(this._lib.getNumberOfSlices());
	}
    }

    public double getConstraints() {
	return this._constraints;
    }

    public double getFitness() {
	return this._fitness;
    }

    public void calculateFitness() {
	if (this._fitnessCalculated) {
	    return;
	}
	this._fitnessCalculated = true;
	// Constraints calculated
	String[] lines = this.toString().split("\n");
	int total = 0;
	int mistakes = 0;
	for (int y = 0; y < lines.length; y++) {
	    for (int x = 0; x < lines[y].length(); x++) {
		Character c = lines[y].charAt(x);
		switch (c) {
		case '<':
		    if (x < lines[y].length() - 1) {
			if (lines[y].charAt(x + 1) != '>') {
			    mistakes += 1;
			}
		    } else {
			mistakes += 1;
		    }
		    total += 1;
		    break;
		case '>':
		    if (x > 0) {
			if (lines[y].charAt(x - 1) != '<') {
			    mistakes += 1;
			}
		    } else {
			mistakes += 1;
		    }
		    total += 1;
		    break;
		case '[':
		    if (x < lines[y].length() - 1) {
			if (lines[y].charAt(x + 1) != ']') {
			    mistakes += 1;
			}
		    } else {
			mistakes += 1;
		    }
		    total += 1;
		    break;
		case ']':
		    if (x > 0) {
			if (lines[y].charAt(x - 1) != '[') {
			    mistakes += 1;
			}
		    } else {
			mistakes += 1;
		    }
		    total += 1;
		    break;
		}
	    }
	}
	this._constraints = 1;
	if (total != 0) {
	    this._constraints = 1 - ((double) mistakes) / total;
	}

	// this._fitness = this._rnd.nextDouble();
	if (this._constraints == 1.0) {
	    RunGivenLevel rgl = new RunGivenLevel();
	    rgl.setLevel(this.toString(), this._appendingSize);
	    AgentResultObject aro = rgl.runLevel(null);
	    // if(aro.firstAgentResult == 1 && aro.secondAgentResult == 0) {
	    this._fitness = 1 - getNormalizedTileUse();
	    // } else {
	    // this._fitness = aro.score;
	    // }
	} else {
	    this._fitness = 0;
	}
    }

    public double getNormalizedTileUse() {
	double result;

	String mapString = this.toString();
	int count = 0;
	for (char c : mapString.toCharArray()) {
	    if (c != '-') {
		count++;
	    }
	}
	result = count / (mapString.toCharArray().length * 1.0);
	return result;
    }

    public Chromosome clone() {
	Chromosome clone = new Chromosome(this._rnd, this._lib, this._section.length, this._appendingSize);
	for (int i = 0; i < _section.length; i++) {
	    clone._section[i] = this._section[i];
	}
	clone._fitness = this._fitness;
	clone._fitnessCalculated = this._fitnessCalculated;
	return clone;
    }

    public Chromosome mutate() {
	Chromosome mutated = this.clone();
	mutated._fitnessCalculated = false;
	mutated._section[mutated._rnd.nextInt(mutated._section.length)] = mutated._rnd
		.nextInt(mutated._lib.getNumberOfSlices());
	return mutated;
    }

    public Chromosome crossover(Chromosome c) {
	Chromosome child = this.clone();
	child._fitnessCalculated = false;
	int index1 = child._rnd.nextInt(child._section.length);
	int index2 = child._rnd.nextInt(child._section.length);
	if (index1 > index2) {
	    int temp = index2;
	    index2 = index1;
	    index1 = temp;
	}
	for (int i = index1; i < index2 + 1; i++) {
	    child._section[i] = c._section[i];
	}
	return child;
    }

    public String toString() {
	String level = "";
	int height = this._lib.getSlice(this._section[0]).length();
	for (int i = 0; i < height; i++) {
	    String appendingChar = "-";
	    if (i == height - 1) {
		appendingChar = "X";
	    }
	    for (int k = 0; k < this._appendingSize; k++) {
		level += appendingChar;
	    }
	    for (int j = 0; j < this._section.length; j++) {
		level += this._lib.getSlice(this._section[j]).charAt(i);
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
	    return (int) Math.signum(o._fitness - this._fitness);
	}
	return (int) Math.signum(o._constraints - this._constraints);
    }
}
