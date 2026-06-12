package org.dm.conveyor.model;

import java.io.Serializable;

public class JobResultEvent implements Serializable {

    public static final String NAME_SLACKER = "slacker";

    public enum StateEnum {
        OK, // job has passed successfully.
        ERROR, // job has failed in a technical way.
        IGNORED; // job has not been executed, due to some pre-conditions.

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    private StateEnum state;

    /** The name of the check. */
    private String name;

    /** Result details from the check. */
    private String details;

    private String jobID;

    public JobResultEvent() {
    }

    public JobResultEvent(StateEnum state, String name, String details, String jobID) {
        this.state = state;
        this.name = name;
        this.details = details;
        this.jobID = jobID;
    }

    public String getName() {
        return name;
    }

    public JobResultEvent setName(String name) {
        this.name = name;
        return this;
    }

    public StateEnum getState() {
        return state;
    }

    public JobResultEvent setState(StateEnum status) {
        this.state = status;
        return this;
    }

    public String getDetails() {
        return details;
    }

    public JobResultEvent setDetails(String details) {
        this.details = details;
        return this;
    }

    public String getJobID() { return jobID; }

    public JobResultEvent setJobID(String jobID) {
        this.jobID = jobID;
        return this;
    }

    public String toString() {
        return String.format("CheckResultEvent{jobID='%s',name='%s',state=%s}",jobID,name,state);
    }
}
