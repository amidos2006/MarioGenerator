import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import ch.idsia.mario.engine.GlobalOptions;
import shared.*;

public class TestRunner {
    private static String getDimensionIndex(double[] dimensions) {
	String result = "";
	for(int i=0; i<dimensions.length; i++) {
	    result += (int)Math.ceil(dimensions[i]) + ",";
	}
	return result.substring(0, result.length() - 1);
    }
    
    public static void main(String[] args) {
	GlobalOptions.VisualizationOn = true;
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
        
//        HashMap<String, String> parameters = new HashMap<>();
//        parameters.put("fitnessType", "entropy");
//        parameters.put("agentType", "AStarAgent");
//	mapelites.Chromosome c = new mapelites.Chromosome(rnd, sl, 14, 3);
//	c.stringInitialize("2832,2252,652,95,2280,1650,3349,2756,592,1856,2110,1864,2114,761");
//	c.runAlgorithms(parameters);
//	System.out.println("Constarints: " + c.getConstraints());
//	System.out.println("Fitness: " + c.getFitness());
//	System.out.println("Dimensions: " + getDimensionIndex(c.getDimensions()));
	
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("experimentType", "LimitedAgent");
        parameters.put("agentType", "NoRun");
        parameters.put("fitnessType", "entropy");
	fi2pop.Chromosome c = new fi2pop.Chromosome(rnd, sl, 14, 3);
	c.stringInitialize("1370,2682,1931,758,2613,2795,2756,405,3436,2778,2175,1239,3638,406");
	c.runAlgorithms(parameters);
	System.out.println("Constarints: " + c.getConstraints());
	System.out.println("Fitness: " + c.getFitness());
	
//	RunMapEliteLevel test = new RunMapEliteLevel(rnd, null);
//	System.out.println(c.toString());
//	test.setLevel(c.toString(), 3);
//	EvaluationInfo eval = test.runLevel(true);
//	System.out.println(eval.toString());
//	MapElites map = new MapElites(sl, rnd, 3, 14, 5, 0.2, 0, 0);
//	Chromosome[] chromosomes = new Chromosome[20];
//	for(int i=0; i<20; i++) {
//	    c = new Chromosome(sl, rnd, 3, 14);
//	    c._fitness = rnd.nextDouble();
//	    c._constraints = 1;
//	    chromosomes[i] = c;
//	}
//	map.assignChromosomes(chromosomes);
    }
}
