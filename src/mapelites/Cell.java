package mapelites;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Cell {
    private double[] _dimensions;
    private ArrayList<Chromosome> _pop;
    private int _size;
    private Random _rnd;
    
    public Cell(double[] dimensions, int size, Random rnd) {
	this._dimensions = dimensions;
	this._size = size;
	this._pop = new ArrayList<Chromosome>();
	this._rnd = rnd;
    }
    
    public double[] getDimensions(){
	return this._dimensions;
    }
    
    public Chromosome[] getFeasible(boolean descending) {
	ArrayList<Chromosome> feasible = new ArrayList<Chromosome>();
	for(Chromosome c:this._pop) {
	    if(c.getConstraints() == 1) {
		feasible.add(c);
	    }
	}
	Collections.sort(feasible);
	if(descending) {
	    Collections.reverse(feasible);
	}
	return feasible.toArray(new Chromosome[0]);
    }
    
    public Chromosome[] getInfeasible(boolean descending) {
	ArrayList<Chromosome> infeasible = new ArrayList<Chromosome>();
	for(Chromosome c:this._pop) {
	    if(c.getConstraints() < 1) {
		infeasible.add(c);
	    }
	}
	Collections.sort(infeasible);
	if(descending) {
	    Collections.reverse(infeasible);
	}
	return infeasible.toArray(new Chromosome[0]);
    }
    
    private Chromosome rankSelection(Chromosome[] pop) {
	double[] ranks = new double[pop.length];
	ranks[0] = 1;
	for(int i=1; i<pop.length; i++) {
	    ranks[i] = ranks[i-1] + i + 1;
	}
	for(int i=0; i<pop.length; i++) {
	    ranks[i] /= ranks[ranks.length - 1];
	}
	double randValue = this._rnd.nextDouble();
	for(int i=0; i<ranks.length; i++){
	    if(randValue <= ranks[i]) {
		return pop[i];
	    }
	}
	return pop[pop.length - 1];
    }
    
    public Chromosome getChromosome() {
	Chromosome[] feasible = this.getFeasible(false);
	Chromosome[] infeasible = this.getInfeasible(false);
	if(this._rnd.nextDouble() < (1.0 * feasible.length) / (feasible.length + infeasible.length)) {
	    return this.rankSelection(feasible);
	}
	return this.rankSelection(infeasible);
    }
    
    public void setChromosome(Chromosome c) {
	if(this._pop.size() >= this._size) {
	    Chromosome[] chromosomes = this.getInfeasible(false);
	    if(chromosomes.length == 0) {
		chromosomes = this.getFeasible(false);
	    }
	    this._pop.remove(chromosomes[0]);
	}
	this._pop.add(c);
    }
}
