import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import ch.idsia.mario.engine.GlobalOptions;
import ch.idsia.tools.RunMapEliteLevel;
import mapelites.Chromosome;
import mapelites.MapElites;
import shared.RepeatedLevelSlicesLibrary;
import shared.SlicesLibrary;
import shared.UniqueLevelSlicesLibrary;

public class TestRunner {
    public static void main(String[] args) {
	GlobalOptions.VisualizationOn = true;
	GlobalOptions.MarioCeiling = false;
	
	Random rnd = new Random();
	SlicesLibrary sl = new UniqueLevelSlicesLibrary();
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
	Chromosome c = new Chromosome(sl, rnd, 3, 14);
	c.stringInitialize("2830,858,2686,2813,1821,993,2831,2572,681,1890,2254,2687,3396,2733");
	RunMapEliteLevel test = new RunMapEliteLevel(rnd, null);
	System.out.println(c.toString());
	test.setLevel(c.toString(), 3);
	test.runLevel(true);
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
