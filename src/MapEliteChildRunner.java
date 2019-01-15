import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import ch.idsia.mario.engine.GlobalOptions;
import mapelites.Chromosome;
import shared.RepeatedLevelSlicesLibrary;
import shared.SlicesLibrary;
import shared.UniqueLevelSlicesLibrary;
import shared.evaluator.ChildEvaluator;

public class MapEliteChildRunner {
    private static HashMap<String, String> readParameters(String filename) throws IOException {
	List<String> lines = Files.readAllLines(Paths.get("", filename));
	HashMap<String, String> parameters = new HashMap<String, String>();
	for(int i=0; i<lines.size(); i++) {
	    if(lines.get(i).trim().length() == 0) {
		continue;
	    }
	    String[] parts = lines.get(i).split("=");
	    parameters.put(parts[0].trim(), parts[1].trim());
	}
	return parameters;
    }
    
    public static void main(String[] args) {
	GlobalOptions.VisualizationOn = false;
	GlobalOptions.MarioCeiling = false;
	
	int id = Integer.parseInt(args[0]);
	int size = Integer.parseInt(args[1]);
	HashMap<String, String> parameters = null;
	try {
	    parameters = readParameters("parameters.txt");
	} catch (IOException e1) {
	    e1.printStackTrace();
	}
	parameters.put("agentType", "AStarAgent");
	if(args.length > 2) {
	    parameters.put("agentType", args[2]);
	}
	parameters.put("agentSTD", "1");
	if(args.length > 3) {
	    parameters.put("agentSTD", args[3]);
	}
	parameters.put("numTrials", "1");
	if(args.length > 4) {
	    parameters.put("agentSTD", args[4]);
	}
	ChildEvaluator child = new ChildEvaluator(id, size, parameters.get("inputFolder"), parameters.get("outputFolder"));
	SlicesLibrary lib = new RepeatedLevelSlicesLibrary();
	if(parameters.get("slicesType").toLowerCase().contains("unique")) {
	    lib = new UniqueLevelSlicesLibrary();
	}
	File directory = new File(parameters.get("levelFolder"));
        File[] files = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith("txt");
            }
        });
        try{
            Arrays.sort(files, new Comparator<File>() {
                public int compare(File o1, File o2) {
                    return o1.getName().compareTo(o2.getName());
                }});
            for (File f : files) {
                String[] lines = Files.readAllLines(f.toPath()).toArray(new String[0]);
                lib.addLevel(lines);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Random rnd = new Random(Integer.parseInt(parameters.get("seed")));
        int appendingSize = Integer.parseInt(parameters.get("appendingSize"));
        int chromosomeLength = Integer.parseInt(parameters.get("chromosomeLength"));
        Chromosome[] chromosomes = null;
	while(true) {
	    try {
		System.out.println("Waiting for parent");
		while(!child.checkChromosomes()) {
		    Thread.sleep(500);
		}
		Thread.sleep(1000);
		System.out.println("Reading children values");
		String[] levels = child.readChromosomes();
		chromosomes = new Chromosome[levels.length];
		for(int i=0; i<chromosomes.length; i++) {
		    chromosomes[i] = new Chromosome(rnd, lib, chromosomeLength, appendingSize);
		    chromosomes[i].stringInitialize(levels[i]);
		}
		int index = 0;
		for(Chromosome c:chromosomes) {
		    System.out.println("\tRunning Child number: " + ++index);
		    c.runAlgorithms(parameters);
		}
		child.clearInputFiles();
		System.out.println("Writing Chromosomes results.");
		String[] values = new String[chromosomes.length];
		for(int i=0; i<values.length; i++) {
		    values[i] = chromosomes[i].getConstraints() + "," + chromosomes[i].getFitness();
		    double[] dimensions = chromosomes[i].getDimensions();
		    for(int j=0; j<dimensions.length; j++) {
			values[i] += "," + dimensions[j];
		    }
		    values[i] += "\n";
		    values[i] += chromosomes[i].getGenes() + "\n";
		    values[i] += chromosomes[i].toString() + "\n";
		}
		child.writeResults(values);
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	
    }
}
