import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import ch.idsia.ai.agents.Agent;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.ai.tasks.Task;
import ch.idsia.mario.engine.GlobalOptions;
import ch.idsia.mario.engine.level.Level;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;
import mapelites.Chromosome;
import shared.*;

public class TestHuman {
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
	Chromosome c = new Chromosome(rnd, sl, 14, 3);
	c.stringInitialize("1805,2252,652,95,3178,1650,3349,2756,592,1856,2110,1864,2114,3248");
	Level lvl = Level.initializeLevel(c.toString(), 3, true);
	
	Agent controller = new HumanKeyboardAgent();
        EvaluationOptions options = new CmdLineOptions(new String[0]);
        options.setAgent(controller);
        Task task = new ProgressTask(options);
        options.setMaxFPS(false);
        options.setPauseWorld(false);
        options.setVisualization(true);
        options.setNumberOfTrials(1);
        options.setMatlabFileName("");
        options.setMarioMode(0);
        task.setOptions(options);
        System.out.println ("Score: " + ((ProgressTask)task).evaluate (controller, lvl)[0]);
    }
}
