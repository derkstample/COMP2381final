package dataviewer1orig;

import java.awt.Color;

public class RawDataVisitor extends DataVisitor {

	public RawDataVisitor(DataViewerApp dv) {
		super(dv);
	}

	@Override
	public Color getDatumColor(Datum d) {
		
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
