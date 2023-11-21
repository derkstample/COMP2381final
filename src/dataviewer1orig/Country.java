package dataviewer2split;

import java.util.Map;
import java.util.TreeMap;

public class Country implements DataStructure {
	private Map<String,State> states;
	
	public Country() {
		states = new TreeMap<String,State>();
	}
	public void addDataStructure(String name,DataStructure ds) throws IllegalDataStructureException {
		if(!(ds instanceof State)) throw new IllegalDataStructureException("Countries must have States as substructures");
		getStates().put(name,(State)ds);
	}
	public DataStructure getDataStructure(String name) throws DataStructureNotFoundException {
		if(!getStates().containsKey(name)) throw new DataStructureNotFoundException("State not found in this country");
		return getStates().get(name);
	}
	public void removeDataStructure(String name) {
		getStates().remove(name);
	}
	public Map<String,State> getStates() {
		return states;
	}
}
