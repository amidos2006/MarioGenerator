import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;

import fi2pop.Chromosome;
import fi2pop.GeneticAlgorithm;
import shared.RepeatedLevelSlicesLibrary;
import shared.SlicesLibrary;

import java.io.FileWriter;
import java.io.PrintWriter;

public class EvolveALevel {

    SlicesLibrary library;

//    Agent perfect;
//    Agent disabled;

    String popFolder = "populations/";

    GeneticAlgorithm ga;

//    public void init() {
//        perfect = = new AStarAgent();
//        disabled = new LimitedJumpAgent();
//    }
    public static void main(String[] args) {

        EvolveALevel eal = new EvolveALevel();
        eal.library = new RepeatedLevelSlicesLibrary();
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
                eal.library.addLevel(lines);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        long startTime = System.currentTimeMillis();
        long time = 1000*60*60;
        int count = 0;
        eal.init();

        // time based
//        while (System.currentTimeMillis() - startTime < time) {
//            eal.evolve(count + 1);
//            count++;
//        }
        // gen based
        for(int i = 0; i < 50; i++) {
            eal.evolve(i+1);
        }
    }

    public void init() {
        ga = new GeneticAlgorithm(library, 100, 12, 3, 0.9, 0.3, 1);
    }
    public void evolve(int gen) {

        try {
//            this.ga = new GeneticAlgorithm(library, 100, 18, 0.9, 0.3, 1);
            Chromosome[] pop = this.ga.evolve(1000);

            File popFile = new File(popFolder + "/" + gen + ".txt");
            FileWriter fWriter = new FileWriter(popFile, true);
            PrintWriter pWriter = new PrintWriter(fWriter);


            for (int i = 0; i < pop.length; i++) {
                System.out.println("index " + i + " constraints " + pop[i].getConstraints() + " fitness " + pop[i].getFitness());
                System.out.println(pop[i]);

                pWriter.println("Max Fitness Index: " + ga.getMaxFitnessIndex(pop) + ", " + ga.getMaxFitness(pop));
                pWriter.println("Average Constraint: " + ga.averageConstraint(pop));
                pWriter.println("AverageFitness: " + ga.averageFitness(pop));
                pWriter.println("index " + i + " constraints " + pop[i].getConstraints() + " fitness " + pop[i].getFitness());
                pWriter.println(pop[i]);
                pWriter.println(" ");
            }
            pWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
