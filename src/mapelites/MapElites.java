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
    private double _eliteProb;
    private int _appendingSize;
    private int _chromosomeLength;
    
    public MapElites(SlicesLibrary lib, Random rnd, int appendingSize, int chromosomeLength, int popSize, 
	    double inbreeding, double crossover, double mutation, double eliteProb) {
	this._map = new HashMap<String, Cell>();
	this._lib = lib;
	this._rnd = rnd;
	this._appendingSize = appendingSize;
	this._chromosomeLength = chromosomeLength;
	this._popSize = popSize;
	this._inbreeding = inbreeding;
	this._crossover = crossover;
	this._mutation = mutation;
	this._eliteProb = eliteProb;
    }
    
    public Cell[] getCells() {
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
	    newBatch[i] = new Chromosome(this._rnd, this._lib, this._chromosomeLength, this._appendingSize);
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
		c = (Chromosome)s1.getChromosome(this._eliteProb).crossover(s2.getChromosome(this._eliteProb));
		if(this._rnd.nextDouble() < this._mutation) {
		    c = (Chromosome)c.mutate();
		}
	    }
	    else {
		c = (Chromosome)s1.getChromosome(this._eliteProb).mutate();
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
	    this._map.get(key).setChromosome(c);
	}
    }
    
    public void writeMap(String path) throws FileNotFoundException, UnsupportedEncodingException {
	Cell[] cells = this.getCells();
	for(Cell c:cells) {
	    String currentPath = path + this.getDimensionIndex(c.getDimensions()) + "/";
	    File f = new File(currentPath);
	    f.mkdir();
	    Chromosome elite = c.getElite();
	    Chromosome[] infeasible = c.getInfeasible(true);
	    int index = 0;
	    PrintWriter resultWriter = new PrintWriter(currentPath + "result.txt", "UTF-8"); 
	    if(elite != null) {
		PrintWriter writer = new PrintWriter(currentPath + index + ".txt", "UTF-8");
		writer.println(elite.getGenes());
		writer.println("Fitness: " + elite.getFitness());
		writer.println("Constraints: " + elite.getConstraints());
		writer.println("Dimensions: " + this.getDimensionIndex(elite.getDimensions()));
		writer.println("Level:\n" + elite.toString());
		writer.close();
		resultWriter.println("Chromosome " + index + ": " + elite.getConstraints() + ", " + elite.getFitness());
		index += 1;
	    }
	    for(Chromosome ch:infeasible) {
		PrintWriter writer = new PrintWriter(currentPath + index + ".txt", "UTF-8");
		writer.println("Genes: " + ch.getGenes());
		writer.println("Fitness: " + ch.getFitness());
		writer.println("Constraints: " + ch.getConstraints());
		writer.println("Dimensions: " + this.getDimensionIndex(ch.getDimensions()));
		writer.println("Level:\n" + ch.toString());
		writer.close();
		resultWriter.println("Chromosome " + index + ": " + ch.getConstraints() + ", " + ch.getFitness());
		index += 1;
	    }
	    resultWriter.close();
	}
	
    }
    
    public double[] getStatistics() {
	Cell[] cells = this.getCells();
	int numberElites = 0;
	int maxInfeasible = 0;
	int avgInfeasible = 0;
	int minInfeasible = Integer.MAX_VALUE;
	
	for(Cell c:cells) {
	    Chromosome elite = c.getElite();
	    Chromosome[] infeasible = c.getInfeasible(true);
	    if(elite != null) {
		numberElites += 1;
	    }
	    if(infeasible.length > maxInfeasible) {
		maxInfeasible = infeasible.length;
	    }
	    if(infeasible.length < minInfeasible) {
		minInfeasible = infeasible.length;
	    }
	    avgInfeasible += infeasible.length;
	}
	avgInfeasible /= cells.length;
	
	return new double[] {cells.length, numberElites, maxInfeasible, avgInfeasible, minInfeasible};
    }
}
