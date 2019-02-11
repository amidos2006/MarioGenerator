import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import ch.idsia.mario.engine.GlobalOptions;
import mapelites.MapElites;
import mapelites.Chromosome;
import shared.RepeatedLevelSlicesLibrary;
import shared.SlicesLibrary;
import shared.evaluator.ChildEvaluator;
import shared.evaluator.ParentEvaluator;

public class TestingGeneration {
    public static void main(String[] args) throws IOException {
	GlobalOptions.VisualizationOn = false;
	GlobalOptions.MarioCeiling = false;
	GlobalOptions.SceneGeneration = true;
	
	Random rnd = new Random();
	SlicesLibrary sl = new RepeatedLevelSlicesLibrary();
        File directory = new File("levels/");
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
                sl.addLevel(lines);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
	MapElites map = new MapElites(sl, rnd, 3, 14, 20, 0.25, 0.7, 0.3, 0.5);
	ParentEvaluator parent = new ParentEvaluator("input/", "output/");
	ChildEvaluator[] children = new ChildEvaluator[3];
	for(int i=0; i<children.length; i++) {
	    children[i] = new ChildEvaluator(i, 2, "input/", "output/");
	}
	Chromosome[] pop = map.randomChromosomes(6);
	String[] levels = new String[pop.length];
	for(int i=0; i<pop.length; i++) {
	    levels[i] = "";
	    levels[i] += pop[i].getGenes() + "\n" + pop[i].toString() + "\n";
	}
	parent.writeChromosomes(levels);
	for(int r=0; r<children.length; r++) {
	    levels = children[r].readChromosomes();
	    Chromosome[] chromosomes = new Chromosome[levels.length];
	    for(int i=0; i<chromosomes.length; i++) {
		chromosomes[i] = new Chromosome(rnd, sl, 14, 3);
		chromosomes[i].stringInitialize(levels[i]);
	    }
	    int index = 0;
	    for(Chromosome c:chromosomes) {
		System.out.println("\tRunning Child number: " + ++index);
		c.runAlgorithms(null);
	    }
	    String[] values = new String[chromosomes.length];
	    for (int i = 0; i < values.length; i++) {
		values[i] = chromosomes[i].getConstraints() + "," + chromosomes[i].getFitness();
		double[] dimensions = chromosomes[i].getDimensions();
		for (int j = 0; j < dimensions.length; j++) {
		    values[i] += "," + dimensions[j];
		}
		values[i] += "\n";
		values[i] += chromosomes[i].getGenes() + "\n";
		values[i] += chromosomes[i].toString() + "\n";
	    }
	    children[r].writeResults(values);
	}
	String[] values = parent.assignChromosomes(pop.length);
	for(int i=0; i<pop.length; i++) {
	    pop[i].childEvaluationInitialization(values[i]);
	}
	map.assignChromosomes(pop);
    }
}
