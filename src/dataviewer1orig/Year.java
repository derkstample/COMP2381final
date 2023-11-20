package dataviewer2split;

import java.util.Map;
import java.util.TreeMap;

public class Year implements DataStructure{
	private Map<String,Datum> months;
	
	public Year() {
		months = new TreeMap<String,Datum>();
	}
	public void addDataStructure(String name, DataStructure ds) throws IllegalDataStructureException {
		if(!(ds instanceof Datum)) throw new IllegalDataStructureException("Years must have Datums as substructures");
		months.put(name,(Datum)ds);
	}
	public DataStructure getDataStructure(String name) throws DataStructureNotFoundException {
		if(!months.containsKey(name)) throw new DataStructureNotFoundException("State not found in the database");
		return months.get(name);
	}
	public void removeDataStructure(String name) {
		months.remove(name);
	}
}
