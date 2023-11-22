package dataviewer1orig;

import java.awt.Color;

public class ExtremaDataVisitor extends DataVisitor {
	
	private double extremaMin;
	private double extremaMax;
	
	public ExtremaDataVisitor(DataViewerApp dv, double ExtremaMinimum, double ExtremaMaximum) {
		super(dv);
		
		extremaMin = ExtremaMinimum;
		extremaMax = ExtremaMaximum;
	}

	@Override
	public Color getDatumColor(Datum d) {
		
		double value = d.getValue();
		Color cellColor = null;
		
		if(value > extremaMin && value < extremaMax) {
			cellColor = getColor(d);
		}
		else {
			// doing extrema visualization, show "high" values in red "low" values in blue.
			if(value >= extremaMax) {
				cellColor = Color.RED;
			}
			else {
				cellColor = Color.BLUE;
			}
		}
		
		return cellColor;
	}
	
	public Color getColor(Datum d) {
		
		double value = d.getValue();
		
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

        r = (int)(255.0 * pct);
       	g = 0;
       	b = (int)(255.0 * (1.0-pct));
        
        dv.debugger.trace("converting %f to [%d, %d, %d]", value, r, g, b);

		return new Color(r, g, b);
	}

}
