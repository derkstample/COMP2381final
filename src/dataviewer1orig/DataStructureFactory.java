package dataviewer2split;

public class DataStructureFactory {
	public static DataStructure createDataStructure(String type) throws DataStructureNotFoundException {
		switch(type) {
		case "Country":
			return new Country();
		case "State":
			return new State();
		case "Year":
			return new Year(); 
		default:
			throw new DataStructureNotFoundException("Cannot find DataStructure of specified type");
		}
	}
}
