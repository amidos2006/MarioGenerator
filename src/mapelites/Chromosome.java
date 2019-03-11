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
	this._dimensions = new double[12];
    }
    
    public double[] getDimensions() {
	return this._dimensions;
    }
    
    public void childEvaluationInitialization(String values) {
	String[] parts = values.split(",");
	double newConstraints = Double.parseDouble(parts[0]);
//	if(this._algorithmRan && this._constraints <= newConstraints) {
//	    return;
//	}
//	this._algorithmRan = true;
	this._constraints = newConstraints;
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
	    this._dimensions[1] = evalInfo.jumpHeight <= 30.0? 1:0;
	}
	if(evalInfo.jumpHeight != EvaluationInfo.MagicNumberUndef) {
	    this._dimensions[2] = evalInfo.jumpHeight >= 60.0? 1:0;
	}
	if(evalInfo.jumpDistance != EvaluationInfo.MagicNumberUndef) {
	    this._dimensions[3] = evalInfo.jumpDistance <= 48.0? 1:0;
	}
	if(evalInfo.jumpDistance != EvaluationInfo.MagicNumberUndef) {
	    this._dimensions[4] = evalInfo.jumpDistance >= 96.0? 1:0;
	}
	if(evalInfo.stompKills != EvaluationInfo.MagicNumberUndef) {
	    this._dimensions[5] = evalInfo.stompKills >= 1? 1:0;
	}
	if(evalInfo.shellKills != EvaluationInfo.MagicNumberUndef) {
	    this._dimensions[6] = evalInfo.shellKills >= 1? 1:0;
	}
	if(evalInfo.totalKills != EvaluationInfo.MagicNumberUndef) {
	    this._dimensions[7] = evalInfo.totalKills - evalInfo.shellKills - evalInfo.stompKills >= 1? 1:0;
	}
	if(evalInfo.marioMode != EvaluationInfo.MagicNumberUndef) {
	    this._dimensions[8] = evalInfo.marioMode >= 1? 1:0;
	}
	if(evalInfo.numberOfGainedCoins != EvaluationInfo.MagicNumberUndef) {
	    this._dimensions[9] = evalInfo.numberOfGainedCoins >= 1? 1:0;
	}
	if(evalInfo.breakBlock != EvaluationInfo.MagicNumberUndef) {
	    this._dimensions[10] = evalInfo.breakBlock >= 1? 1:0;
	}
	if(evalInfo.coinBlock != EvaluationInfo.MagicNumberUndef) {
	    this._dimensions[11] = evalInfo.coinBlock >= 1? 1:0;
	}
	
	if (this._constraints >= 1) {
	    this.calculateFitness(parameters.get("fitnessType").trim().toLowerCase().equals("entropy"));
	}
    }   
}
