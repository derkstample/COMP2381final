package dataviewer2split;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.io.File;

public class Database {
	private Map<String,Country> data;
	private String m_selectedCountryName;
	private Country m_selectedCountry;
	private State m_selectedState;
	private String m_selectedStartYear; //i pinky promise you theres a good reason these are strings
	private String m_selectedEndYear;
	private final static String DEFAULT_COUNTRY = "United States";
	private final static boolean DO_TRACE = true;
	private final static int FILE_COUNTRY_IDX = 4;
	private final static int FILE_DATE_IDX = 0;
	private final static int FILE_NUM_COLUMNS = 5;
	private final static int FILE_STATE_IDX = 3;
	private final static int FILE_TEMPERATURE_IDX = 1;
	private final static int FILE_UNCERTAINTY_IDX = 2;
	
	public Database(String datapath) {
		try {
			m_selectedCountryName = DEFAULT_COUNTRY; //trust me this needs to be here and i no longer have the energy to explain why
			buildDatabase(datapath);
		}catch(FileNotFoundException e) {
			e.printStackTrace(); //kinda a catastrophic failure, can't handle it much more gracefully than this
		}
	}
	
	public void setSelectedCountry(String country) {
		if(data.containsKey(country)) {
			m_selectedCountryName = country;
			m_selectedCountry = data.get(country);
		}
	}
	public void setSelectedState(String state) {
		if(m_selectedCountry.getStates().containsKey(state))
			try {
				m_selectedState = (State) m_selectedCountry.getDataStructure(state);
			} catch (DataStructureNotFoundException e) {
				e.printStackTrace();
			}
	}
	public void setSelectedStartYear(Integer year) {
		if(m_selectedState.getYears().containsKey(year.toString())) m_selectedStartYear = year.toString();
	}
	public void setSelectedEndYear(Integer year) {
		if(m_selectedState.getYears().containsKey(year.toString())) m_selectedEndYear = year.toString();
	}
	
	public List<Year> getSelectedYears() { //returns a nested list of each year's worth of data, with each sublist contain
		ArrayList<Year> output = new ArrayList<Year>();
		for(String yearKey: m_selectedState.getYears().keySet()) {
			if(Integer.valueOf(yearKey)>Integer.valueOf(m_selectedStartYear) && Integer.valueOf(yearKey)<Integer.valueOf(m_selectedEndYear)) {
				try {
					output.add((Year) m_selectedState.getDataStructure(yearKey));
				} catch (DataStructureNotFoundException e) {
					e.printStackTrace(); //i was about to get mad that the getDataStructure method stupidly can throw an exception when i remembered im the one who made it
				}
			}
		}
		return output;
	}
	
	private void buildDatabase(String datapath) throws FileNotFoundException{
		Map<String,Country> output = new TreeMap<String,Country>();
		try(Scanner scanner = new Scanner(new File(datapath))){
			boolean skipFirst = true;
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
    	    	
    	    	if(!skipFirst) {
    	    		List<Object> record = getRecordFromLine(line);
    	    		if(record != null) {
	    				try {
		    				String year = record.get(0).toString();
		    				String month = record.get(1).toString();
		    				double value = (Double)record.get(2);
		    				double uncertainty = (Double)record.get(3);
		    				String state = (String)record.get(4);
		    				String country = (String)record.get(5);
	    					Country c;
	    					State s;
	    					Year y;
	    					Datum d;
	    					if(!output.containsKey(country)) {
	    						c = (Country)DataStructureFactory.createDataStructure("Country");
	    						m_selectedCountryName = country;
	    						m_selectedCountry = c;
	    					}else {
	    						c = output.get(country);
	    					}
	    					if(!c.getStates().containsKey(state)) {
	    						s = (State)DataStructureFactory.createDataStructure("State");
	    					}else {
	    						s = (State)c.getDataStructure(state);
	    					}
	    					if(!s.getYears().containsKey(year)) {
	    						y = (Year)DataStructureFactory.createDataStructure("Year");
	    					}else {
	    						y = (Year)s.getDataStructure(year);
	    					}
	    					if(!y.getMonths().containsKey(month)) {
	    						d = new Datum(value, uncertainty);
	    					}else {
	    						d = (Datum)y.getDataStructure(month);
	    					}
	    					c.addDataStructure(state, s);
	    					s.addDataStructure(year, y);
	    					y.addDataStructure(month, d);
	    					output.put(country,c);
	    				}catch(DataStructureNotFoundException e) {
	    					e.printStackTrace();// if you let this exception happen, its your own fault
	    				}catch(IllegalDataStructureException e) {
	    					e.printStackTrace();
	    				}
    	    		}
    	    	}
    	    	else {
    	    		skipFirst = false;
    	    	}
			}
			scanner.close();
		}
		data = output;
		try {
			String defaultStateName = ((String)m_selectedCountry.getStates().keySet().toArray()[0]);
			m_selectedState = (State) m_selectedCountry.getDataStructure(defaultStateName); //gross i know, sorry. just trying to set a stupid default State
			
			Integer maxYear = -9999;
			Integer minYear = 9999;
			for(String yearKey:m_selectedState.getYears().keySet()){ //this just sets default start/end years to max/min in dataset.
				Integer year = Integer.valueOf(yearKey); //really starting to regret making everything a stupid map
				if(year<minYear) {
					 minYear = year;
				}
				if(year>maxYear) {
					 maxYear = year;
				}
			}
			m_selectedStartYear = minYear.toString();
			m_selectedEndYear = maxYear.toString();
		}catch(DataStructureNotFoundException e) {
			e.printStackTrace();//this really shouldn't have happened!!
		}
		

		info("loaded %d data records",data.size());
		info("loaded data for %d states", m_selectedCountry.getStates().size());
		info("loaded data for %d years [%d, %d]", m_selectedState.getYears().size(), Integer.valueOf(m_selectedStartYear), Integer.valueOf(m_selectedEndYear));
	}
	private List<Object> getRecordFromLine(String line) {
        List<String> rawValues = new ArrayList<String>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(",");
            while (rowScanner.hasNext()) {
                rawValues.add(rowScanner.next());
            }
        }
        if(rawValues.size() != FILE_NUM_COLUMNS) {
        	trace("malformed line '%s'...skipping", line);
        	return null;
        }
        else if(!rawValues.get(FILE_COUNTRY_IDX).equals(m_selectedCountryName)) {
        	trace("skipping non-selected record: %s", rawValues);
        	return null;
        }
        else {
        	trace("processing raw data: %s", rawValues.toString());
        }
        try {
        	// Parse these into more useful objects than String
        	List<Object> values = new ArrayList<Object>(4);
        	
        	Integer year = parseYear(rawValues.get(FILE_DATE_IDX));
        	if(year == null) {
        		return null;
        	}
        	values.add(year);
        	
        	Integer month = parseMonth(rawValues.get(FILE_DATE_IDX));
        	if(month == null) {
        		return null;
        	}
        	values.add(month);
        	values.add(Double.parseDouble(rawValues.get(FILE_TEMPERATURE_IDX)));
        	//not going to use UNCERTAINTY yet
        	//BUT, its great to have it just in case
        	values.add(Double.parseDouble(rawValues.get(FILE_UNCERTAINTY_IDX)));
        	values.add(rawValues.get(FILE_STATE_IDX));
        	// since all are the same country
        	values.add(rawValues.get(FILE_COUNTRY_IDX));
        	return values;
        }
        catch(NumberFormatException e) {
        	trace("unable to parse data line, skipping...'%s'", line);
        	return null;
        }
    }
    /**
     * Utility function to pull a year integer out of a date string.  Supports M/D/Y and Y-M-D formats only.
     * 
     * @param dateString
     * @return
     */
    private Integer parseYear(String dateString) {
    	Integer ret = null;
    	if(dateString.indexOf("/") != -1) {
    		// Assuming something like 1/20/1823
    		String[] parts = dateString.split("/");
    		if(parts.length == 3) {
	    		ret = Integer.parseInt(parts[2]);
    		}
    	}
    	else if(dateString.indexOf("-") != -1) {
    		// Assuming something like 1823-01-20
    		String[] parts = dateString.split("-");
    		if(parts.length == 3) {
    			ret = Integer.parseInt(parts[0]);
    		}
    	}
    	else {
    		throw new RuntimeException(String.format("Unexpected date delimiter: '%s'", dateString));
    	}
    	if(ret == null) {
    		trace("Unable to parse year from date: '%s'", dateString);
    	}
    	return ret;
    }
    private Integer parseMonth(String dateString) {
    	Integer ret = null;
    	if(dateString.indexOf("/") != -1) {
    		// Assuming something like 1/20/1823
    		String[] parts = dateString.split("/");
    		if(parts.length == 3) {
	    		ret = Integer.parseInt(parts[0]);
    		}
    	}
    	else if(dateString.indexOf("-") != -1) {
    		// Assuming something like 1823-01-20
    		String[] parts = dateString.split("-");
    		if(parts.length == 3) {
    			ret = Integer.parseInt(parts[1]);
    		}
    	}
    	else {
    		throw new RuntimeException(String.format("Unexpected date delimiter: '%s'", dateString));
    	}
    	if(ret == null || ret.intValue() < 1 || ret.intValue() > 12) {
    		trace("Unable to parse month from date: '%s'", dateString);
    		return null;
    	}
    	return ret;
	}
    
    private void trace(String format, Object...args) {
    	if(DO_TRACE) {
    		System.out.print("TRACE: ");
    		System.out.println(String.format(format, args));
    	}
    }
    /**
     * For informational output.
     * @param format
     * @param args
     */
    private void info(String format, Object... args) {
    	System.out.print("INFO: ");
    	System.out.println(String.format(format, args));
    }
    
    public void printDatabase() { //this is really just so I know everything is stored correctly
    	for(String country:data.keySet()) {
    		System.out.println(country);
    		for(String state:data.get(country).getStates().keySet()) {
    			System.out.println("\t"+state);
    			for(String year:data.get(country).getStates().get(state).getYears().keySet()) {
    				System.out.println("\t\t"+year);
    				for(String month:data.get(country).getStates().get(state).getYears().get(year).getMonths().keySet()) {
    					System.out.println("\t\t\t"+month);
    					System.out.println("\t\t\t\t"+data.get(country).getStates().get(state).getYears().get(year).getMonths().get(month));
    				}
    			}
    		}
    	}
    }
}
