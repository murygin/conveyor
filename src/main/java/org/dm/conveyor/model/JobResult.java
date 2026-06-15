package org.dm.conveyor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.io.Serializable;

@Entity
public class JobResult implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String ID;

    private JobResultEvent.StateEnum state;

    /** The name of the check. */
    private String name;

    /** Result details from the check. */
    @Column(length=4096)
    private String details;

    public JobResult() {
    }

    public JobResult(JobResultEvent.StateEnum state, String name, String details) {
        this.state = state;
        this.name = name;
        this.details = details;
    }

    public String getName() {
        return name;
    }

    public JobResult setName(String name) {
        this.name = name;
        return this;
    }

    public JobResultEvent.StateEnum getState() {
        return state;
    }

    public JobResult setState(JobResultEvent.StateEnum status) {
        this.state = status;
        return this;
    }

    public String getDetails() {
        return details;
    }

    public JobResult setDetails(String details) {
        this.details = details;
        return this;
    }

}
