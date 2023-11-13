package dataviewer1orig;

import java.util.SortedMap;
import java.util.TreeMap;

public class Plot {
	
	private TreeMap<Integer, SortedMap<Integer,Double>> m_plotData = null;
	private TreeMap<Integer,Double> m_plotMonthlyMaxValue = null;
	private TreeMap<Integer,Double> m_plotMonthlyMinValue = null;
	
	public Plot() {
		
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
