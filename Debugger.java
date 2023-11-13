package dataviewer1orig;

public class Debugger {

    private DataViewerApp app;

    public Debugger(DataViewerApp app) {
        this.app = app;
    }

    private boolean debuggingEnabled = true;
    
    public boolean isDebuggingEnabled() {
        return debuggingEnabled;
        }

    public void trace(String format, Object... args) {
        if (app.isDebuggingEnabled()) {
            System.out.print("TRACE: ");
            System.out.println(String.format(format, args));
        }
    }

    public void info(String format, Object... args) {
        System.out.print("INFO: ");
        System.out.println(String.format(format, args));
    }

    public void error(String format, Object... args) {
        System.out.print("ERROR: ");
        System.out.println(String.format(format, args));
    }

    public void debug(String format, Object... args) {
        if (app.isDebuggingEnabled()) {
            System.out.print("DEBUG: ");
            System.out.println(String.format(format, args));
        }
    }
}

