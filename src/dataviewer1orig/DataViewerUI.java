package dataviewer2split;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.SortedMap;

import javax.swing.JOptionPane;

import edu.du.dudraw.DrawListener;

public class DataViewerUI extends DataObserver implements DrawListener {
	
	//instance variables   
	private final static String[] 	VISUALIZATION_MODES = { "Raw", "Extrema (within 10% of min/max)" };
	
    private int m_guiMode = 0; // Menu by default, 1 is data mode
    
    // user selections
    private String m_selectedCountry = "United States";
    private Integer m_selectedEndYear;
    private String m_selectedState;
    private Integer m_selectedStartYear;
    private String m_selectedVisualization = VISUALIZATION_MODES[0];

	public DataViewerUI(DataViewerApp dv, String windowTitle, int w, int h) {
		super(dv, windowTitle, w, h);
		window.addListener(this);
	}

    private void drawMainMenu() {
    	window.clear(Color.WHITE);

    	String[] menuItems = {
    			"Type the menu number to select that option:",
    			"",
    			String.format("C     Set country: [%s]", m_selectedCountry),
    			String.format("T     Set state: [%s]", m_selectedState),
    			String.format("S     Set start year [%d]", m_selectedStartYear),
    			String.format("E     Set end year [%d]", m_selectedEndYear),
    			String.format("V     Set visualization [%s]", m_selectedVisualization),
    			String.format("P     Plot data"),
    			String.format("Q     Quit"),
    	};
    	
    	// enable drawing by "percentage" with the menu drawing
        window.setXscale(0, 100);
		window.setYscale(0, 100);
		
		// draw the menu
    	window.setPenColor(Color.BLACK);
		
		drawMenuItems(menuItems);
    }

	private void drawMenuItems(String[] menuItems) {
		double yCoord = 90.0;
		
		for(int i=0; i<menuItems.length; i++) {
			window.textLeft(40.0, yCoord, menuItems[i]);
			yCoord -= 5.0;
		}
	}
    
    private void drawData() {
    	
    	DataVisitor visitor;
    	
    	final double DATA_WINDOW_BORDER = 50.0;
    	final String[] MONTH_NAMES = { "", // 1-based
    			"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
    	
    	// Give a buffer around the plot window
        window.setXscale(-DATA_WINDOW_BORDER, window.getCanvasWidth()+DATA_WINDOW_BORDER);
		window.setYscale(-DATA_WINDOW_BORDER, window.getCanvasHeight()+DATA_WINDOW_BORDER);

    	// gray background
    	window.clear(Color.LIGHT_GRAY);

    	// white plot area
		window.setPenColor(Color.WHITE);
		window.filledRectangle(window.getCanvasWidth()/2.0, window.getCanvasHeight()/2.0, window.getCanvasWidth()/2.0, window.getCanvasHeight()/2.0);  

    	window.setPenColor(Color.BLACK);
    	
    	double nCols = 12; // one for each month
    	double nRows = m_selectedEndYear - m_selectedStartYear + 1; // for the years
    	
    	dv.debugger.debug("nCols = %f, nRows = %f", nCols, nRows);
 		
        double cellWidth = window.getCanvasWidth() / nCols;
        double cellHeight = window.getCanvasHeight() / nRows;
        
        dv.debugger.debug("cellWidth = %f, cellHeight = %f", cellWidth, cellHeight);
        
        boolean extremaVisualization = m_selectedVisualization.equals(VISUALIZATION_MODES[1]);
        dv.debugger.info("visualization: %s (extrema == %b)", m_selectedVisualization, extremaVisualization);
        
        for(int month = 1; month <= 12; month++) {
            double fullRange = dv.plotter.getMonthlyMax(month) - dv.plotter.getMonthlyMin(month);
            double extremaMinBound = dv.plotter.getMonthlyMin(month) + 0.1 * fullRange;
            double extremaMaxBound = dv.plotter.getMonthlyMax(month) - 0.1 * fullRange;


            // draw the line separating the months and the month label
        	window.setPenColor(Color.BLACK);
        	double lineX = (month-1.0)*cellWidth;
        	window.line(lineX, 0.0, lineX, window.getCanvasHeight());
        	window.text(lineX+cellWidth/2.0, -DATA_WINDOW_BORDER/2.0, MONTH_NAMES[month]);
        	
        	// there should always be a map for the month
        	SortedMap<Integer,Double> monthData = dv.plotter.getMonthlyPlotData(month);
        	
        	for(int year = m_selectedStartYear; year <= m_selectedEndYear; year++) {

        		// month data structure might not have every year
        		if(monthData.containsKey(year)) {
        			Double value = monthData.get(year);
        			
        			double x = (month-1.0)*cellWidth + 0.5 * cellWidth;
        			double y = (year-m_selectedStartYear)*cellHeight + 0.5 * cellHeight;
        			
        	    	if (extremaVisualization) {
        	    		visitor = new ExtremaDataVisitor(dv, extremaMinBound, extremaMaxBound);
        	    	}
        	    	
        	    	else {
        	    		visitor = new RawDataVisitor(dv);
        	    	}
        			
        			Color cellColor = visitor.getDatumColor(value);
    
        			
        			// draw the rectangle for this data point
        			window.setPenColor(cellColor);
        			dv.debugger.trace("month = %d, year = %d -> (%f, %f) with %s", month, year, x, y, cellColor.toString());
        			window.filledRectangle(x, y, cellWidth/2.0, cellHeight/2.0);
        		}
        	}
        }
        
        // draw the labels for the y-axis
        window.setPenColor(Color.BLACK);

        double labelYearSpacing = (m_selectedEndYear - m_selectedStartYear) / 5.0;
        double labelYSpacing = window.getCanvasHeight()/5.0;
        // spaced out by 5, but need both the first and last label, so iterate 6
        for(int i=0; i<6; i++) {
        	int year = (int)Math.round(i * labelYearSpacing + m_selectedStartYear);
        	String text = String.format("%4d", year);
        	
        	window.textRight(0.0, i*labelYSpacing, text);
        	window.textLeft(window.getCanvasWidth(), i*labelYSpacing, text);
        }
     
        // draw rectangle around the whole data plot window
        window.rectangle(window.getCanvasWidth()/2.0, window.getCanvasHeight()/2.0, window.getCanvasWidth()/2.0, window.getCanvasHeight()/2.0);
        
        // put in the title
        String title = String.format("%s, %s from %d to %d. Press 'M' for Main Menu.  Press 'Q' to Quit.",
        		m_selectedState, m_selectedCountry, m_selectedStartYear, m_selectedEndYear);
        window.text(window.getCanvasWidth()/2.0, window.getCanvasHeight()+DATA_WINDOW_BORDER/2.0, title);
	}
    
    private Color getDataColor(Double value, boolean doGrayscale) {
    	if(null == value) {
    		return null;
    	}
    	double pct = (value + 10.0) / 40.0;
    	dv.debugger.trace("converted %f raw value to %f %%", value, pct);
    
    	if (pct > 1.0) {
            pct = 1.0;
        }
        else if (pct < 0.0) {
            pct = 0.0;
        }
        int r, g, b;
        // Replace the color scheme with my own
        if (!doGrayscale) {
        	r = (int)(255.0 * pct);
        	g = 0;
        	b = (int)(255.0 * (1.0-pct));
        	
        } else {
        	// Grayscale for the middle extrema
        	r = g = b = (int)(255.0 * pct);
        }
        
        dv.debugger.trace("converting %f to [%d, %d, %d]", value, r, g, b);

		return new Color(r, g, b);
	}
	
	
	//listener methods
	
    @Override public void keyPressed(int key) {
		boolean needsUpdate = false;
		boolean needsUpdatePlotData = false;
		dv.debugger.trace("key pressed '%c'", (char)key);
		// regardless of draw mode, 'Q' or 'q' means quit:
		if(key == 'Q') {
			System.out.println("Bye");
			System.exit(0);
		}
		else if(m_guiMode == 0) {
			if(key == 'P') {
				// plot the data
				m_guiMode = 1;
				if(dv.plotter.plotDataNull() == true) {
					// first time going to render data need to generate the plot data
					needsUpdatePlotData = true;
				}
				needsUpdate = true;
			}
			else if(key == 'C') {
				// set the Country
				Object selectedValue = JOptionPane.showInputDialog(null,
			             "Choose a Country", "Input",
			             JOptionPane.INFORMATION_MESSAGE, null,
			             m_dataCountries.toArray(), m_selectedCountry);
				
				if(selectedValue != null) {
					dv.debugger.info("User selected: '%s'", selectedValue);
					if(!selectedValue.equals(m_selectedCountry)) {
						// change in data
						m_selectedCountry = (String)selectedValue;
						try {
							dv.database.loadData();
						}
						catch(FileNotFoundException e) {
							// convert to a runtime exception since
							// we can't add throws to this method
							throw new RuntimeException(e);
						}
						needsUpdate = true;
						needsUpdatePlotData = true;
					}
				}
			}

			else if(key == 'T') {
				// set the state
				Object selectedValue = JOptionPane.showInputDialog(null,
			             "Choose a State", "Input",
			             JOptionPane.INFORMATION_MESSAGE, null,
			             m_dataStates.toArray(), m_selectedState);
				
				if(selectedValue != null) {
					dv.debugger.info("User selected: '%s'", selectedValue);
					if(!selectedValue.equals(m_selectedState)) {
						// change in data
						m_selectedState = (String)selectedValue;
						needsUpdate = true;
						needsUpdatePlotData = true;
					}
				}
			}
			else if(key == 'S') {
				// set the start year
				Object selectedValue = JOptionPane.showInputDialog(null,
			             "Choose the start year", "Input",
			             JOptionPane.INFORMATION_MESSAGE, null,
			             m_dataYears.toArray(), m_selectedStartYear);
				
				if(selectedValue != null) {
					dv.debugger.info("User seleted: '%s'", selectedValue);
					Integer year = (Integer)selectedValue;
					if(year.compareTo(m_selectedEndYear) > 0) {
						dv.debugger.error("new start year (%d) must not be after end year (%d)", year, m_selectedEndYear);
					}
					else {
						if(!m_selectedStartYear.equals(year)) {
							m_selectedStartYear = year;
							needsUpdate = true;
							needsUpdatePlotData = true;
						}
					}
				}
			}
			else if(key == 'E') {
				// set the end year
				Object selectedValue = JOptionPane.showInputDialog(null,
			             "Choose the end year", "Input",
			             JOptionPane.INFORMATION_MESSAGE, null,
			             m_dataYears.toArray(), m_selectedEndYear);
				
				if(selectedValue != null) {
					dv.debugger.info("User seleted: '%s'", selectedValue);
					Integer year = (Integer)selectedValue;
					if(year.compareTo(m_selectedStartYear) < 0) {
						dv.debugger.error("new end year (%d) must be not be before start year (%d)", year, m_selectedStartYear);
					}
					else {
						if(!m_selectedEndYear.equals(year)) {
							m_selectedEndYear = year;
							needsUpdate = true;
							needsUpdatePlotData = true;
						}
					}
				}
			}
			else if(key == 'V') {
				// set the visualization
				Object selectedValue = JOptionPane.showInputDialog(null,
						"Choose the visualization mode", "Input",
						JOptionPane.INFORMATION_MESSAGE, null,
						VISUALIZATION_MODES, m_selectedVisualization);

				if(selectedValue != null) {
					dv.debugger.info("User seleted: '%s'", selectedValue);
					String visualization = (String)selectedValue;
					if(!m_selectedVisualization.equals(visualization)) {
						m_selectedVisualization = visualization;
						needsUpdate = true;
					}
				}
			}

		}
		else if (m_guiMode == 1) {
			if(key == 'M') {
				m_guiMode = 0;
				needsUpdate = true;
			}
		}
		else {
			throw new IllegalStateException(String.format("unexpected mode: %d", m_guiMode));
		}
		if(needsUpdatePlotData) {
			// something changed with the data that needs to be plotted
			dv.updatePlotData();
		}
		if(needsUpdate) {
			update();
		}
	}

	@Override
	public void keyReleased(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(char arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(double arg0, double arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(double arg0, double arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(double arg0, double arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(double arg0, double arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update() {
		if(m_guiMode == 0) {
    		drawMainMenu();
    	}
    	else if(m_guiMode == 1) {
    		drawData();
    	}
    	else {
    		throw new IllegalStateException(String.format("Unexpected drawMode=%d", m_guiMode));
    	}
        // for double-buffering
        window.show();
	}

}
