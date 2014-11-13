package loaders;

/**
 * This class holds the per-item data in the {@link SmapTaskLoader}.
 */
public class TaskEntry {
    public String type;    // form or task
    public String taskStatus;
    public String name;
    public String project;
    public String ident;
    public String taskStart;
    public String taskAddress;
    public String taskForm;
    public String instancePath;
    public int formVersion;
    public long id;
    public double schedLon = 0.0;
    public double schedLat = 0.0;
    public double actLon = 0.0;
    public double actLat = 0.0;
    public String isSynced;
    public long assignmentId;



    @Override
    public String toString() {
        return taskStatus;
    }

}