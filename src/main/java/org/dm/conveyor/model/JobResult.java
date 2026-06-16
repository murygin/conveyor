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
    private String id;

    private JobResultEvent.StateEnum state;

    /**
     * The name of the job.
     */
    private String name;

    /**
     * Result details from the job.
     */
    @Column(length = 4096)
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

    @SuppressWarnings("unused")
    public JobResultEvent.StateEnum getState() {
        return state;
    }

    @SuppressWarnings("unused")
    public JobResult setState(JobResultEvent.StateEnum status) {
        this.state = status;
        return this;
    }

    @SuppressWarnings("unused")
    public String getDetails() {
        return details;
    }

    @SuppressWarnings("unused")
    public JobResult setDetails(String details) {
        this.details = details;
        return this;
    }

}
