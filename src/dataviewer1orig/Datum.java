package dataviewer2split;

public class Datum implements DataStructure{
	private double temperature;
	private double uncertainty;
	public Datum(double temperature, Double uncertainty) {
		this.temperature = temperature;
		this.uncertainty = uncertainty;
	}
	public double getTemperature() {
		return temperature;
	}
	public double getUncertainty() {
		return uncertainty;
	}
	public void addDataStructure(String name, DataStructure ds) throws IllegalDataStructureException {
		throw new IllegalDataStructureException("Datums cannot have substructures");
	}
	public DataStructure getDataStructure(String name) throws DataStructureNotFoundException {
		throw new DataStructureNotFoundException("Datums do not have substructures");
	}
	public void removeDataStructure(String name) {
		//do nothing, no structures to remove
	}
	//now you may be asking yourself, "why is Datum implementing DataStructure if literally all of the implemented methods are unused?" and to that I have a great answer, its because Year will throw a fit trying to get a Datum if it isn't a DataStructure
}
