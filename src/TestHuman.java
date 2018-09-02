import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.AgentsPool;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.ai.tasks.Task;
import ch.idsia.mario.engine.GlobalOptions;
import ch.idsia.mario.engine.level.Level;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;
import ch.idsia.tools.RunMapEliteLevel;
import competition.cig.robinbaumgarten.AStarAgent;
import mapelites.Chromosome;
import mapelites.MapElites;
import shared.RepeatedLevelSlicesLibrary;
import shared.SlicesLibrary;
import shared.UniqueLevelSlicesLibrary;

public class TestHuman {
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
	c.stringInitialize("982,1426,3833,1520,570,3103,2078,4121,1544,328,1721,2538,3911,4182");
	Level lvl = Level.initializeLevel(c.toString(), 3, true);
	
	Agent controller = new HumanKeyboardAgent();
        EvaluationOptions options = new CmdLineOptions(new String[0]);
        options.setAgent(controller);
        Task task = new ProgressTask(options);
        options.setMaxFPS(false);
        options.setVisualization(true);
        options.setNumberOfTrials(1);
        options.setMatlabFileName("");
        options.setMarioMode(0);
        task.setOptions(options);
        
        System.out.println ("Score: " + ((ProgressTask)task).evaluate (controller, lvl)[0]);
    }
}