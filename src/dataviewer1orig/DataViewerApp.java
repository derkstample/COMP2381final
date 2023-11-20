package dataviewer2split;

import edu.du.dudraw.DrawListener;

public class DataViewerApp{

    private void handleMainMenuKeyPress(int key, boolean needsUpdate, boolean needsUpdatePlotData) {
        if (key == 'P') {
            goToDataMode(needsUpdate, needsUpdatePlotData);
        } else if (key == 'C') {
            setCountry();
            needsUpdate = true;
            needsUpdatePlotData = true;
        } else if (key == 'T') {
            setState();
            needsUpdate = true;
            needsUpdatePlotData = true;
        } else if (key == 'S') {
            setStartYear();
            needsUpdate = true;
            needsUpdatePlotData = true;
        } else if (key == 'E') {
            setEndYear();
            needsUpdate = true;
            needsUpdatePlotData = true;
        } else if (key == 'V') {
            setVisualization();
            needsUpdate = true;
        }
    }

    private void handleDataModeKeyPress(int key, boolean needsUpdate) {
        if (key == 'M') {
            goToMainMenu(needsUpdate);
        }
    }
// 

    private void quit() {
        System.out.println("Bye");
        System.exit(0);
    }

    private void goToDataMode(boolean needsUpdate, boolean needsUpdatePlotData) {
        m_guiMode = GUI_MODE_DATA;
        if (m_plotData == null) {
            needsUpdatePlotData = true;
        }
        needsUpdate = true;
    }

    private void setCountry() {
         m_selectedCountry = country;
        updateMenuItems();

    }

    private void setState() {
       m_selectedState = state;
        updateMenuItems();
    }

    private void setStartYear() {
       m_selectedStartYear;
	    updateMenuItems() = items;
    }

    private void setEndYear() {
        m_selectedEndYear = year;
	    updateMenuItems();
    }

    private void setVisualization() {
       m_selectedVisualization = visualization;
	    updateMenuItems();
    }

    private void goToMainMenu(boolean needsUpdate) {
        m_guiMode = GUI_MODE_MAIN_MENU;
        needsUpdate = true;
    }

}

