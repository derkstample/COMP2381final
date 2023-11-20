package dataviewer2split;

import java.util.Map;
import java.util.TreeMap;

public class State implements DataStructure{
	private Map<String,Year> years;
	
	public State() {
		years = new TreeMap<String,Year>();
	}
	public void addDataStructure(String name, DataStructure ds) throws IllegalDataStructureException {
		if(!(ds instanceof Year)) throw new IllegalDataStructureException("States must have Years as substructures");
		years.put(name,(Year)ds);
	}
	public DataStructure getDataStructure(String name) throws DataStructureNotFoundException {
		if(!years.containsKey(name)) throw new DataStructureNotFoundException("Year not found in the database");
		return years.get(name);
	}
	public void removeDataStructure(String name) {
		years.remove(name);
	}
}
