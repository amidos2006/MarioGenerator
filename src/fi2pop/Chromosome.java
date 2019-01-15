package fi2pop;

import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.tools.AgentResultObject;
import ch.idsia.tools.runners.RunLimitedAgentsLevel;
import shared.SlicesLibrary;

import java.util.HashMap;
import java.util.Random;

public class Chromosome extends shared.Chromosome {

    public Chromosome(Random rnd, SlicesLibrary lib, int size, int appendingSize) {
	super(rnd, lib, size, appendingSize);
	this._genes = new int[size];
	this._appendingSize = appendingSize;
	this._rnd = rnd;
	this._library = lib;
	this._fitness = 0;
	this._constraints = 0;
    }
    
    public void childEvaluationInitialization(String values) {
	String[] parts = values.split(",");
	this._constraints = Double.parseDouble(parts[0]);
	this._fitness = Double.parseDouble(parts[1]);
    }

    public void runAlgorithms(HashMap<String, String> parameters) {
	RunLimitedAgentsLevel rgl = new RunLimitedAgentsLevel(this._rnd, parameters);
	rgl.setLevel(this.toString(), this._appendingSize);
	AgentResultObject aro = rgl.runLevel(false);
	if(aro.perftectAgentWin == Mario.STATUS_WIN && aro.limitedAgentWin == Mario.STATUS_DEAD) {
	    this._constraints = 1;
	}
	else {
	    this._constraints = Math.min(1.0, (aro.perfectAgentDistance - aro.limitedAgentDistance) / aro.totalDistance);
	}
	
	if (this._constraints >= 1) {
	    this.calculateFitness();
	}
    }

    public Chromosome clone() {
	Chromosome clone = new Chromosome(this._rnd, this._library, this._genes.length, this._appendingSize);
	for (int i = 0; i < _genes.length; i++) {
	    clone._genes[i] = this._genes[i];
	}
	return clone;
    }
}
