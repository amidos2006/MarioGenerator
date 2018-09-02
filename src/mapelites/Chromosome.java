package mapelites;

import java.util.HashMap;
import java.util.Random;

import ch.idsia.mario.engine.level.Level;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.RunMapEliteLevel;
import shared.SlicesLibrary;

public class Chromosome implements Comparable<Chromosome>{
    private SlicesLibrary _library;
    private int[] _genes;
    private Random _rnd;
    private int _appendingSize;
    private double _fitness;
    private double _constraints;
    private double[] _dimensions;
    
    public Chromosome(SlicesLibrary lib, Random rnd, int appendingSize, int chromosomeLength) {
	this._library = lib;
	this._rnd = rnd;
	this._genes = new int[chromosomeLength];
	this._appendingSize = appendingSize;
	this._fitness = 0;
	this._constraints = 0;
	this._dimensions = new double[7];
    }
    
    public void randomInitialize() {
	for(int i=0; i<this._genes.length; i++) {
	    this._genes[i] = this._rnd.nextInt(this._library.getNumberOfSlices());
	}
    }
    
    public void stringInitialize(String level) {
	String[] parts = level.split(",");
	for(int i=0; i<this._genes.length; i++) {
	    this._genes[i] = Integer.parseInt(parts[i]);
	}
    }
    
    public void constraintsDimensionsInitialize(String values) {
	String[] parts = values.split(",");
	this._constraints = Double.parseDouble(parts[0]);
	for(int i=1; i<parts.length; i++) {
	    this._dimensions[i-1] = Double.parseDouble(parts[i]);
	}
    }
    
    public Chromosome clone() {
	Chromosome c = new Chromosome(this._library, this._rnd, this._appendingSize, this._genes.length);
	for(int i=0; i<this._genes.length; i++) {
	    c._genes[i] = this._genes[i];
	}
	return c;
    }
    
    public void runAlgorithms(HashMap<String, String> parameters) {
	RunMapEliteLevel test = new RunMapEliteLevel(this._rnd, parameters);
	test.setLevel(this.toString(), this._appendingSize);
	EvaluationInfo evalInfo = test.runLevel(false);
	this._constraints = Math.min(1, evalInfo.lengthOfLevelPassedCells / (evalInfo.totalLengthOfLevelCells - this._appendingSize));
	if(evalInfo.numOfJumps != EvaluationInfo.MagicNumberUndef) {
	    this._dimensions[0] = Math.min(1, evalInfo.numOfJumps / 5.0);
	}
	if(evalInfo.jumpDistance != EvaluationInfo.MagicNumberUndef) {
	    this._dimensions[1] = Math.min(1, Math.floor(evalInfo.jumpDistance / 96.0));
	}
	if(evalInfo.stompKills != EvaluationInfo.MagicNumberUndef && evalInfo.totalKills > 0) {
	    this._dimensions[2] = Math.min(1, evalInfo.stompKills / (1.0 * evalInfo.totalKills));
	}
	if(evalInfo.shellKills != EvaluationInfo.MagicNumberUndef && evalInfo.totalKills > 0) {
	    this._dimensions[3] = Math.min(1, evalInfo.shellKills / (1.0 * evalInfo.totalKills));
	}
	if(evalInfo.totalKills != EvaluationInfo.MagicNumberUndef && evalInfo.totalKills > 0) {
	    this._dimensions[4] = Math.min(1, (evalInfo.totalKills - evalInfo.stompKills - evalInfo.shellKills) / (1.0 * evalInfo.totalKills));
	}
	if(evalInfo.marioMode != EvaluationInfo.MagicNumberUndef) {
	    this._dimensions[5] = Math.min(1, evalInfo.marioMode);
	}
	if(evalInfo.numberOfGainedCoins != EvaluationInfo.MagicNumberUndef && evalInfo.totalNumberOfCoins > 0) {
	    this._dimensions[6] = Math.min(1, evalInfo.numberOfGainedCoins / (1.0 * evalInfo.totalNumberOfCoins));
	}
    }
    
    private boolean checkSliceSimilarity(int g1, int g2) {
	String slice1 = this._library.getSlice(g1);
	String slice2 = this._library.getSlice(g2);
	return slice1.equals(slice2);
    }
    
    private float getSimplicityFitness() {
	float total = 0;
	float empty = 0;
	for(int i=0; i<this._genes.length; i++) {
	    String slice = this._library.getSlice(this._genes[i]);
	    for(int j=0; j<slice.length(); j++) {
		if(Level.isEmpty(slice.charAt(j))) {
		    empty += 1;
		}
		total += 1;
	    }
	}
	return empty / total;
    }
    
    public void calculateFitness(Chromosome[] pop) {
//	if(pop.length == 0) {
//	    this._fitness = 0.5 + 0.5 * this.getSimplicityFitness();
//	    return;
//	}
	double finalMaxValue = 0;
	for(Chromosome p:pop) {
	    int maxValue = 0;
	    for(int i=0; i<this._genes.length - 1; i++) {
		int match1 = 0;
		int match2 = 0;
		for (int j = i; j < p._genes.length; j++) {
		    if (this.checkSliceSimilarity(this._genes[i], p._genes[j])) {
			match1 += 1;
		    }
		    if (this.checkSliceSimilarity(p._genes[i], this._genes[j])) {
			match2 += 1;
		    }
		}
		if (match1 > maxValue) {
		    maxValue = match1;
		}
		if (match2 > maxValue) {
		    maxValue = match2;
		}
	    }
	    if(maxValue > finalMaxValue) {
		finalMaxValue = maxValue;
	    }
	}
//	this._fitness = 0.5 * (1 - ((double)finalMaxValue) / this._genes.length) + 0.5 * this.getSimplicityFitness();
	this._fitness = this.getSimplicityFitness();
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
    
    public Chromosome mutate() {
	Chromosome mutated = this.clone();
	mutated._genes[mutated._rnd.nextInt(mutated._genes.length)] = mutated._rnd.nextInt(mutated._library.getNumberOfSlices());
	return mutated;
    }
    
    public double getFitness() {
	return this._fitness;
    }
    
    public double getConstraints() {
	return this._constraints;
    }
    
    public double[] getDimensions() {
	return this._dimensions;
    }
    
    public String getGenes() {
	String result = "" + this._genes[0];
	for(int i=1; i<this._genes.length; i++) {
	    result += "," + this._genes[i];
	}
	return result;
    }
    
    public String toString(){
	String level = "";
	int height = this._library.getSlice(this._genes[0]).length();
	for(int i=0; i<height; i++){
	    String appendingChar = "-";
	    if(i == height - 1) {
		appendingChar = "X";
	    }
	    for(int k=0; k<this._appendingSize; k++) {
		level += appendingChar;
	    }
	    for(int j=0; j<this._genes.length; j++){
		level += this._library.getSlice(this._genes[j]).charAt(i);
	    }
	    for(int k=0; k<this._appendingSize; k++) {
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
