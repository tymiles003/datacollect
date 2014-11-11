package loaders;

/**
 * This class holds the per-item data in the {@link SmapTaskLoader}.
 */
public class TaskEntry {
    private final SmapTaskLoader mLoader;
    public String type;    // form or task
    public String status;
    public String name;
    public String project;
    public String ident;
    public String taskStart;
    public String taskAddress;
    public String taskForm;
    public String instancePath;
    public int formVersion;
    public long id;
    public double lon = 0.0;
    public double lat = 0.0;

    public TaskEntry(SmapTaskLoader loader) {
        mLoader = loader;
    }


    @Override
    public String toString() {
        return status;
    }

}