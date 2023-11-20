package dataviewer2split;

public interface DataStructure {
	public void addDataStructure(String name, DataStructure ds) throws IllegalDataStructureException;
	public DataStructure getDataStructure(String name) throws DataStructureNotFoundException;
	public void removeDataStructure(String name);
}
