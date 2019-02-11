package fi2pop;

import ch.idsia.mario.engine.sprites.Mario;
import ch.idsia.tools.AgentResultObject;
import ch.idsia.tools.runners.RunLimitedAgentsLevel;
import ch.idsia.tools.runners.RunPunishingModelLevel;
import shared.SlicesLibrary;

import java.util.HashMap;
import java.util.Random;

public class Chromosome extends shared.Chromosome {
    
    private int _age;

    public Chromosome(Random rnd, SlicesLibrary lib, int size, int appendingSize) {
	super(rnd, lib, size, appendingSize);
	this._genes = new int[size];
	this._appendingSize = appendingSize;
	this._rnd = rnd;
	this._library = lib;
	this._fitness = 0;
	this._constraints = 0;
	this._age = 0;
    }
    
    public void childEvaluationInitialization(String values) {
	String[] parts = values.split(",");
	double newConstraints = Double.parseDouble(parts[0]);
//	if(this._algorithmRan && this._constraints <= newConstraints) {
//	    return;
//	}
//	this._algorithmRan = true;
	this._constraints = newConstraints;
	this._fitness = Double.parseDouble(parts[1]);;
    }
    
    public int getAge() {
	return this._age;
    }
    
    public void advanceAge() {
	this._age += 1;
    }

    public void runAlgorithms(HashMap<String, String> parameters) {
	AgentResultObject aro = null;
	switch (parameters.get("experimentType").trim().toLowerCase()) {
	case "punishingmodel":
	    RunPunishingModelLevel rpl = new RunPunishingModelLevel(this._rnd, parameters);
	    rpl.setLevel(this.toString(), this._appendingSize);
	    aro = rpl.runLevel(true);
	    break;
	case "limitedagent":
	    RunLimitedAgentsLevel rgl = new RunLimitedAgentsLevel(this._rnd, parameters);
	    rgl.setLevel(this.toString(), this._appendingSize);
	    aro = rgl.runLevel(true);
	    break;
	}
	if(aro.perftectAgentWin == Mario.STATUS_WIN && aro.limitedAgentWin != Mario.STATUS_WIN) {
	    this._constraints = 1;
	}
	else {
	    this._constraints = Math.min(1.0, (aro.perfectAgentDistance - aro.limitedAgentDistance) / aro.totalDistance);
	}
	
	if (this._constraints >= 1) {
	    this.calculateFitness(parameters.get("fitnessType").trim().toLowerCase().equals("entropy"));
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
