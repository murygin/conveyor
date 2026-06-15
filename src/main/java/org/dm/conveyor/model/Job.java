package org.dm.conveyor.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Job implements Serializable {

    public enum StateEnum {
        CREATED,
        RUNNING,
        FINISHED,
        FAILED;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    private Job.StateEnum state;

    @Id
    private String ID;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private final Set<JobResult> results = new HashSet<>();

    public Job() {
    }

    public Job(String ID) {
        this.state = StateEnum.CREATED;
        this.ID = ID;
    }

    public Job(Job.StateEnum state, String ID) {
        this.state = state;
        this.ID = ID;
    }

    public Job.StateEnum getState() {
        return state;
    }

    public Job setState(Job.StateEnum status) {
        this.state = status;
        return this;
    }


    public String getID() {
        return ID;
    }

    public Job setID(String ID) {
        this.ID = ID;
        return this;
    }

    public Set<JobResult> getResults() {
        return results;
    }

    public void addResult(JobResult jobResult) {
        results.add(jobResult);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Job job)) return false;
        return state == job.state && Objects.equals(ID, job.ID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, ID);
    }

    @Override
    public String toString() {
        return "CheckJob{" +
                "state=" + state +
                ", ID='" + ID + '\'' +
                '}';
    }
}
