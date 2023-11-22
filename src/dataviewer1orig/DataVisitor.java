package dataviewer1orig;

public abstract class DataVisitor {
	
	protected DataViewerApp dv;
	
	public DataVisitor(DataViewerApp dv) {
		this.dv = dv;
	}
	
	public Color visit(Datum d) {
		return Datum.accept(DataVisitor);
	}
	
	public abstract Color getDatumColor(Datum d);
}
