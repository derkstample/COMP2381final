package dataviewer1orig;

import edu.du.dudraw.Draw;

public abstract class DataObserver {
	protected DataViewerApp dv;
	protected Draw window;
	
	public DataObserver(DataViewerApp dv, String windowTitle, int w, int h) {
		this.dv = dv;
		
		window = new Draw(windowTitle);
		window.setCanvasSize(w, h);
		window.enableDoubleBuffering();
		
		update();
	}
	
	public abstract void update();
}
