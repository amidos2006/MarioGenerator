
import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;

import generator.Chromosome;
import generator.GeneticAlgorithm;
import generator.LevelSlicesLibrary;

public class TestGenerator {
    public static void main(String[] args){
	LevelSlicesLibrary library = new LevelSlicesLibrary();
	
	File directory = new File("levels/");
	File[] files = directory.listFiles(new FileFilter() {
	    @Override
	    public boolean accept(File pathname) {
		return pathname.isFile() && pathname.getName().endsWith("txt");
	    }
	});
	try {
	    for(File f:files){
		String[] lines = Files.readAllLines(f.toPath()).toArray(new String[0]);
		library.addLevel(lines);
	    }
	    GeneticAlgorithm ga = new GeneticAlgorithm(library, 100, 28, 0.7, 0.3, 2, 4);
	    Chromosome[] pop = ga.evolve(1000);
	    for(int i=0; i<pop.length; i++){
		System.out.println("index " + i + " fitness " + pop[i].getFitness());
		System.out.println(pop[i]);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
