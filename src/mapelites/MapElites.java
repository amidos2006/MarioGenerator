package mapelites;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map.Entry;

import shared.SlicesLibrary;

import java.util.Random;

public class MapElites {
    private HashMap<String, Cell> _map;
    private Random _rnd;
    private SlicesLibrary _lib;
    
    private int _popSize;
    private double _inbreeding;
    private double _crossover;
    private double _mutation;
    private int _appendingSize;
    private int _chromosomeLength;
    
    public MapElites(SlicesLibrary lib, Random rnd, int appendingSize, int chromosomeLength, int popSize, 
	    double inbreeding, double crossover, double mutation) {
	this._map = new HashMap<String, Cell>();
	this._lib = lib;
	this._rnd = rnd;
	this._appendingSize = appendingSize;
	this._chromosomeLength = chromosomeLength;
	this._popSize = popSize;
	this._inbreeding = inbreeding;
	this._crossover = crossover;
	this._mutation = mutation;
    }
    
    private Cell[] getCells() {
	Cell[] cells = new Cell[this._map.size()];
	int index = 0;
	for(Entry<String,Cell> pair : this._map.entrySet()) {
	    cells[index] = pair.getValue();
	    index += 1;
	}
	return cells;
    }
    
    public Chromosome[] randomChromosomes(int batchSize) {
	Chromosome[] newBatch = new Chromosome[batchSize];
	for(int i=0; i<newBatch.length; i++) {
	    newBatch[i] = new Chromosome(this._lib,this._rnd, this._appendingSize, this._chromosomeLength);
	    newBatch[i].randomInitialize();
	}
	return newBatch;
    }
    
    public Chromosome[] getNextChromosomes(int batchSize) {
	Chromosome[] newBatch = new Chromosome[batchSize];
	Cell[] cells = this.getCells();
	for(int i=0; i<newBatch.length; i++) {
	    Cell s1 = cells[this._rnd.nextInt(cells.length)];
	    Cell s2 = cells[this._rnd.nextInt(cells.length)];
	    if(this._rnd.nextDouble() < this._inbreeding) {
		s2 = s1;
	    }
	    Chromosome c = null;
	    if(this._rnd.nextDouble() < this._crossover) {
		c = s1.getChromosome().crossover(s2.getChromosome());
		if(this._rnd.nextDouble() < this._mutation) {
		    c = c.mutate();
		}
	    }
	    else {
		c = s1.getChromosome().mutate();
	    }
	    newBatch[i] = c;
	}
	return newBatch;
    }
    
    private String getDimensionIndex(double[] dimensions) {
	String result = "";
	for(int i=0; i<dimensions.length; i++) {
	    result += (int)Math.ceil(dimensions[i]) + ",";
	}
	return result.substring(0, result.length() - 1);
    }
    
    public void assignChromosomes(Chromosome[] chromosomes) {
	for(Chromosome c:chromosomes) {
	    String key = this.getDimensionIndex(c.getDimensions());
	    if(!this._map.containsKey(key)) {
		this._map.put(key, new Cell(c.getDimensions(), this._popSize, this._rnd));
	    }
	    c.calculateFitness(this._map.get(key).getFeasible(true));
	    this._map.get(key).setChromosome(c);
	}
    }
    
    public void writeMap(String path) throws FileNotFoundException, UnsupportedEncodingException {
	Cell[] cells = this.getCells();
	for(Cell c:cells) {
	    String currentPath = path + this.getDimensionIndex(c.getDimensions()) + "/";
	    File f = new File(currentPath);
	    f.mkdir();
	    Chromosome[] feasible = c.getFeasible(true);
	    Chromosome[] infeasible = c.getInfeasible(true);
	    int index = 0;
	    PrintWriter resultWriter = new PrintWriter(currentPath + "result.txt", "UTF-8"); 
	    for(Chromosome ch:feasible) {
		PrintWriter writer = new PrintWriter(currentPath + index + ".txt", "UTF-8");
		writer.println(ch.getGenes());
		writer.println(ch.toString());
		writer.close();
		resultWriter.println("Chromosome " + index + ": " + ch.getConstraints() + ", " + ch.getFitness());
		index += 1;
	    }
	    for(Chromosome ch:infeasible) {
		PrintWriter writer = new PrintWriter(currentPath + index + ".txt", "UTF-8");
		writer.println("Genes: " + ch.getGenes());
		writer.println("Fitness: " + ch.getFitness());
		writer.println("Constraints: " + ch.getConstraints());
		writer.println("Level:\n" + ch.toString());
		writer.close();
		resultWriter.println("Chromosome " + index + ": " + ch.getConstraints() + ", " + ch.getFitness());
		index += 1;
	    }
	    resultWriter.close();
	}
	
    }
    
    public int[] getStatistics() {
	Cell[] cells = this.getCells();
	int maxFeasible = 0;
	int avgFeasible = 0;
	int minFeasible = Integer.MAX_VALUE;
	int maxInfeasible = 0;
	int avgInfeasible = 0;
	int minInfeasible = Integer.MAX_VALUE;
	
	for(Cell c:cells) {
	    Chromosome[] feasible = c.getFeasible(true);
	    Chromosome[] infeasible = c.getInfeasible(true);
	    if(feasible.length > maxFeasible) {
		maxFeasible = feasible.length;
	    }
	    if(infeasible.length > maxInfeasible) {
		maxInfeasible = infeasible.length;
	    }
	    if(feasible.length < minFeasible) {
		minFeasible = feasible.length;
	    }
	    if(infeasible.length < minInfeasible) {
		minInfeasible = infeasible.length;
	    }
	    avgFeasible += feasible.length;
	    avgInfeasible += infeasible.length;
	}
	
	avgFeasible /= cells.length;
	avgInfeasible /= cells.length;
	
	return new int[] {cells.length, maxFeasible, avgFeasible, minFeasible, maxInfeasible, avgInfeasible, minInfeasible};
    }
}
