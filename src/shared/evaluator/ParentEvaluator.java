package shared.evaluator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;

import mapelites.Chromosome;

public class ParentEvaluator {
    private String _inputFolder;
    private String _outputFolder;
    
    public ParentEvaluator(String inputFolder, String outputFolder) {
	this._inputFolder = inputFolder;
	this._outputFolder = outputFolder;
    }
    
    public void writeChromosomes(Chromosome[] chromosomes) throws FileNotFoundException, UnsupportedEncodingException {
	for(int i=0; i<chromosomes.length; i++) {
	    PrintWriter writer = new PrintWriter(this._inputFolder + i + ".txt", "UTF-8");
	    writer.print(chromosomes[i].getGenes());
	    writer.close();
	}
    }
    
    public boolean checkChromosomes(Chromosome[] chromosomes) {
	for(int i=0; i<chromosomes.length; i++) {
	    File f = new File(this._outputFolder + i + ".txt");
	    if(!f.exists()) {
		return false;
	    }
	}
	return true;
    }
    
    public void assignChromosomes(Chromosome[] chromosomes) throws IOException {
	for(int i=0; i<chromosomes.length; i++) {
	    String values = Files.readAllLines(Paths.get(this._outputFolder, i + ".txt")).get(0);
	    chromosomes[i].constraintsDimensionsInitialize(values);
	}
    }
    
    public void clearOutputFiles(Chromosome[] chromosomes) {
	for(int i=0; i<chromosomes.length; i++) {
	    File f = new File(this._outputFolder + i + ".txt");
	    f.delete();
	}
    }
}
