package mapelites;

import java.util.HashMap;
import java.util.Random;

import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.runners.RunMapEliteLevel;
import shared.SlicesLibrary;

public class Chromosome extends shared.Chromosome{
    
    private double[] _dimensions;
    
    public Chromosome(Random rnd, SlicesLibrary lib, int size, int appendingSize) {
	super(rnd, lib, size, appendingSize);
	this._dimensions = new double[8];
    }
    
    public double[] getDimensions() {
	return this._dimensions;
    }
    
    public void childEvaluationInitialization(String values) {
	String[] parts = values.split(",");
	this._constraints = Double.parseDouble(parts[0]);
	this._fitness = Double.parseDouble(parts[1]);
	for(int i=2; i<parts.length; i++) {
	    this._dimensions[i-2] = Double.parseDouble(parts[i]);
	}
    }
    
    public Chromosome clone() {
	Chromosome c = new Chromosome(this._rnd, this._library, this._genes.length, this._appendingSize);
	for(int i=0; i<this._genes.length; i++) {
	    c._genes[i] = this._genes[i];
	}
	return c;
    }
    
    public void runAlgorithms(HashMap<String, String> parameters) {
	RunMapEliteLevel test = new RunMapEliteLevel(this._rnd, parameters);
	test.setLevel(this.toString(), this._appendingSize);
	EvaluationInfo evalInfo = test.runLevel(true);
	this._constraints = Math.min(1, (1.0 * evalInfo.lengthOfLevelPassedCells) / (evalInfo.totalLengthOfLevelCells - this._appendingSize));
	if(evalInfo.numOfJumps != EvaluationInfo.MagicNumberUndef) {
	    this._dimensions[0] = evalInfo.numOfJumps >= 1? 1:0;
	}
	if(evalInfo.jumpHeight != EvaluationInfo.MagicNumberUndef) {
	    this._dimensions[1] = evalInfo.jumpHeight >= 60.0? 1:0;
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
	    this._dimensions[5] = evalInfo.totalKills - evalInfo.shellKills - evalInfo.stompKills >= 1? 1:0;
	}
	if(evalInfo.marioMode != EvaluationInfo.MagicNumberUndef) {
	    this._dimensions[6] = evalInfo.marioMode >= 1? 1:0;
	}
	if(evalInfo.numberOfGainedCoins != EvaluationInfo.MagicNumberUndef) {
	    this._dimensions[7] = evalInfo.numberOfGainedCoins >= 1? 1:0;
	}
	
	if (this._constraints >= 1) {
	    this.calculateFitness();
	}
    }   
}
