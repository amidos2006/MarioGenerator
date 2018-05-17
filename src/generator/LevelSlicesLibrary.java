package generator;

import java.util.HashSet;

public class LevelSlicesLibrary {
    private HashSet<String> slices;
    private String[] arrayedSlices;
    
    public LevelSlicesLibrary(){
	this.slices = new HashSet<String>();
	this.arrayedSlices = new String[0];
    }
    
    public void addLevel(String level){
	String[] lines = level.split("\n");
	this.addLevel(lines);
    }
    
    public void addLevel(String[] lines){
	for(int i=0; i<lines[0].length(); i++){
	    String slice = "";
	    for(int j=0; j<lines.length; j++){
		    slice += lines[j].charAt(i);
	    }
	    this.slices.add(slice);
	}
	this.arrayedSlices = this.slices.toArray(new String[0]);
    }
    
    public int getNumberOfSlices(){
	return this.slices.size();
    }
    
    public String getSlice(int index){
	return this.arrayedSlices[index];
    }
}
