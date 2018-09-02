package shared.evaluator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import mapelites.Chromosome;
import shared.SlicesLibrary;

public class ChildEvaluator {
    private int _id;
    private int _size;
    private String _inputFolder;
    private String _outputFolder;
    
    public ChildEvaluator(int id, int size, String inputFolder, String outputFolder) {
	this._id = id;
	this._size = size;
	this._inputFolder = inputFolder;
	this._outputFolder = outputFolder;
    }
    
    public boolean checkChromosomes() {
	int startIndex = this._id * this._size;
	for(int i=0; i<this._size; i++) {
	    File file = new File(this._inputFolder + (startIndex + i) + ".txt");
	    if(!file.exists()) {
		return false;
	    }
	}
	return true;
    }
    
    public Chromosome[] readChromosomes(SlicesLibrary lib, Random rnd, int appendingSize, int chromosomeLength) throws IOException {
	Chromosome[] result = new Chromosome[this._size];
	int startIndex = this._id * this._size;
	for(int i=0; i<this._size; i++) {
	    result[i] = new Chromosome(lib, rnd, appendingSize, chromosomeLength);
	    String level = Files.readAllLines(Paths.get(this._inputFolder, (startIndex + i) + ".txt")).get(0);
	    result[i].stringInitialize(level);
	}
	return result;
    }
    
    public void writeResults(Chromosome[] chromosomes) throws FileNotFoundException, UnsupportedEncodingException {
	int startIndex = this._id * this._size;
	for(int i=0; i<chromosomes.length; i++) {
	    PrintWriter writer = new PrintWriter(this._outputFolder + (startIndex + i) + ".txt", "UTF-8");
	    String result = "" + chromosomes[i].getConstraints();
	    double[] dimensions = chromosomes[i].getDimensions();
	    for(int j=0; j<dimensions.length; j++) {
		result += "," + dimensions[j];
	    }
	    writer.print(result);
	    writer.close();
	}
    }
    
    public void clearInputFiles(Chromosome[] chromosomes) {
	int startIndex = this._id * this._size;
	for(int i=0; i<chromosomes.length; i++) {
	    File f = new File(this._inputFolder + (startIndex + i) + ".txt");
	    f.delete();
	}
    }
}
