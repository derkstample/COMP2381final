package dataviewer2split;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.io.File;

public class Database {
	private Map<String,Country> data;
	private final static int FILE_COUNTRY_IDX = 4;
	private final static int FILE_DATE_IDX = 0;
	private final static int FILE_NUM_COLUMNS = 5;
	private final static int FILE_STATE_IDX = 3;
	private final static int FILE_TEMPERATURE_IDX = 1;
	private final static int FILE_UNCERTAINTY_IDX = 2;
	
	public Database(String datapath) {
		try {
			data = buildDatabase(datapath);
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private Map<String,Country> buildDatabase(String datapath) throws FileNotFoundException{
		Map<String,Country> output = new TreeMap<String,Country>();
		try(Scanner scanner = new Scanner(new File(datapath))){
			boolean skipFirst = true;
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
    	    	
    	    	if(!skipFirst) {
    	    		List<Object> record = getRecordFromLine(line);
    	    		if(record != null) {
    	    			m_dataRaw.add(record);
    	    		}
    	    	}
    	    	else {
    	    		skipFirst = false;
    	    	}
			}
		}
		
		return output;
		 m_selectedState = 
		 m_selectedStartYear = m_dataYears.first();
		 m_selectedEndYear = m_dataYears.last();
		
		 info("loaded %d data records", m_dataRaw.size());
		 info("loaded data for %d states", m_dataStates.size());
		 info("loaded data for %d years [%d, %d]", m_dataYears.size(), m_selectedStartYear, m_selectedEndYear);

		return output;
	}
}
