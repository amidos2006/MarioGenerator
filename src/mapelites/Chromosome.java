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
	this._dimensions = new double[8];
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
	this._constraints = Math.min(1, (1.0 * evalInfo.lengthOfLevelPassedCells) / (evalInfo.totalLengthOfLevelCells - this._appendingSize));
	if(evalInfo.numOfJumps != EvaluationInfo.MagicNumberUndef) {
	    this._dimensions[0] = evalInfo.numOfJumps >= 1? 1:0;
	}
	if(evalInfo.jumpDistance != EvaluationInfo.MagicNumberUndef) {
	    this._dimensions[1] = evalInfo.jumpDistance <= 36.0? 1:0;
	}
	if(evalInfo.jumpDistance != EvaluationInfo.MagicNumberUndef) {
	    this._dimensions[2] = evalInfo.jumpDistance >= 96.0? 1:0;
	}
	if(evalInfo.stompKills != EvaluationInfo.MagicNumberUndef) {
	    this._dimensions[3] = evalInfo.stompKills >= 1? 1:0;
	}
	if(evalInfo.shellKills != EvaluationInfo.MagicNumberUndef) {
	    this._dimensions[4] = evalInfo.shellKills >= 1? 1:0;
	}
	if(evalInfo.totalKills != EvaluationInfo.MagicNumberUndef) {
	    this._dimensions[5] = (evalInfo.totalKills - evalInfo.stompKills - evalInfo.shellKills) >= 1? 1:0;
	}
	if(evalInfo.marioMode != EvaluationInfo.MagicNumberUndef) {
	    this._dimensions[6] = evalInfo.marioMode >= 1? 1:0;
	}
	if(evalInfo.numberOfGainedCoins != EvaluationInfo.MagicNumberUndef) {
	    this._dimensions[7] = evalInfo.numberOfGainedCoins >= 1? 1:0;
	}
    }
    
    public void calculateFitness() {
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
	this._fitness = empty / total;
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
