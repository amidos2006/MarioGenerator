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
//	mapelites.Chromosome c = new mapelites.Chromosome(rnd, sl, 14, 3);
//	c.stringInitialize("3233,1015,1474,1955,1390,952,3071,1692,2315,1581,2153,2771,2314,131");
//	c.runAlgorithms(null);
//	System.out.println("Constarints: " + c.getConstraints());
//	System.out.println("Fitness: " + c.getFitness());
//	System.out.println("Dimensions: " + getDimensionIndex(c.getDimensions()));
	
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("experimentType", "LimitedAgent");
        parameters.put("agentType", "LimitedJump");
	fi2pop.Chromosome c = new fi2pop.Chromosome(rnd, sl, 14, 3);
	c.stringInitialize("3480,2798,2773,1360,1870,3702,1088,236,2058,3322,2039,1871,1457,399");
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
