package dataviewer1orig;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class Plot {
	
	//constants
	private final static int		RECORD_MONTH_IDX = 1;
	private final static int		RECORD_STATE_IDX = 3;
	private final static int		RECORD_TEMPERATURE_IDX = 2;
	private final static int		RECORD_YEAR_IDX = 0;
	
	private Database db;
	private TreeMap<Integer, SortedMap<Integer,Double>> m_plotData = null;
	private TreeMap<Integer,Double> m_plotMonthlyMaxValue = null;
	private TreeMap<Integer,Double> m_plotMonthlyMinValue = null;
	
	public Plot(Database database) {
		this.db = database;
	}
	
	public void updatePlotData(String m_selectedState, Integer m_selectedStartYear, Integer m_selectedEndYear) {
		//debug("raw data: %s", m_rawData.toString());
		// plot data is a map where the key is the Month, and the value is a sorted map where the key
		// is the year. 
		m_plotData = new TreeMap<Integer,SortedMap<Integer,Double>>();
		for(int month = 1; month <= 12; month++) {
			// any year/months not filled in will be null
			m_plotData.put(month, new TreeMap<Integer,Double>());
		}
		// now run through the raw data and if it is related to the current state and within the current
		// years, put it in a sorted data structure, so that we 
		// find min/max year based on data 
		m_plotMonthlyMaxValue = new TreeMap<Integer,Double>();
		m_plotMonthlyMinValue = new TreeMap<Integer,Double>();
		for(List<Object> rec : db.getCountries()) {
			String state = (String)rec.get(RECORD_STATE_IDX);
			Integer year = (Integer)rec.get(RECORD_YEAR_IDX);
			
			// Check to see if they are the state and year range we care about
			if (state.equals(m_selectedState) && 
			   ((year.compareTo(m_selectedStartYear) >= 0 && year.compareTo(m_selectedEndYear) <= 0))) {
						
				// Ok, we need to add this to the list of values for the month
				Integer month = (Integer)rec.get(RECORD_MONTH_IDX);
				Double value = (Double)rec.get(RECORD_TEMPERATURE_IDX);
				
				if(!m_plotMonthlyMinValue.containsKey(month) || value.compareTo(m_plotMonthlyMinValue.get(month)) < 0) {
					m_plotMonthlyMinValue.put(month, value);
				}
				if(!m_plotMonthlyMaxValue.containsKey(month) || value.compareTo(m_plotMonthlyMaxValue.get(month)) > 0) {
					m_plotMonthlyMaxValue.put(month, value);
				}
	
				m_plotData.get(month).put(year, value);
			}
		}
		//debug("plot data: %s", m_plotData.toString());
	}
	
	public SortedMap<Integer,Double> getMonthlyPlotData(int month) {
		return m_plotData.get(month);
	}
	
	public Double getMonthlyMax(int month) {
		return m_plotMonthlyMaxValue.get(month);
	}
	
	public Double getMonthlyMin(int month) {
		return m_plotMonthlyMinValue.get(month);
	}
	
	public boolean plotDataNull() {
		if (m_plotData==null) {
			return true;
		}
		
		else {return false;}
	}

}
