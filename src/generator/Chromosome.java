package generator;

import java.util.Random;

public class Chromosome implements Comparable<Chromosome> {
    private Random _rnd;
    private int[] _section;
    private LevelSlicesLibrary _lib;
    private double _fitness;
    private boolean _fitnessCalculated;
    
    public Chromosome(Random rnd, LevelSlicesLibrary lib, int size){
	this._section = new int[size];
	this._rnd = rnd;
	this._lib = lib;
	this._fitness = 0;
	this._fitnessCalculated = false;
    }
    
    public void randomInitialize(){
	for(int i=0; i<this._section.length; i++){
	    this._section[i] = this._rnd.nextInt(this._lib.getNumberOfSlices());
	}
    }
    
    public double getFitness(){
	if(this._fitnessCalculated){
	    return this._fitness;
	}
	this._fitnessCalculated = true;
	this._fitness = this._rnd.nextDouble();
	return this._fitness;
    }
    
    public Chromosome clone(){
	Chromosome clone = new Chromosome(this._rnd, this._lib, this._section.length);
	for(int i=0; i<_section.length; i++){
	    clone._section[i] = this._section[i];
	}
	clone._fitness = this._fitness;
	clone._fitnessCalculated = this._fitnessCalculated;
	return clone;
    }
    
    public Chromosome mutate(){
	Chromosome mutated = this.clone();
	mutated._fitnessCalculated = false;
	mutated._section[mutated._rnd.nextInt(mutated._section.length)] = mutated._rnd.nextInt(mutated._lib.getNumberOfSlices());
	return mutated;
    }
    
    public Chromosome crossover(Chromosome c){
	Chromosome child = this.clone();
	child._fitnessCalculated = false;
	int index1 = child._rnd.nextInt(child._section.length);
	int index2 = child._rnd.nextInt(child._section.length);
	if(index1 > index2){
	    int temp = index2;
	    index2 = index1;
	    index1 = temp;
	}
	for(int i=index1; i<index2 + 1; i++){
	    child._section[i] = c._section[i];
	}
	return child;
    }
    
    public String toString(){
	String level = "";
	int height = this._lib.getSlice(this._section[0]).length();
	for(int i=0; i<height; i++){
	    for(int j=0; j<this._section.length; j++){
		level += this._lib.getSlice(j).charAt(i);
	    }
	    level += "\n";
	}
	return level;
    }

    @Override
    public int compareTo(Chromosome o) {
	return (int)Math.signum(o.getFitness() - this.getFitness());
    }
}
