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
    private String id;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private final Set<JobResult> results = new HashSet<>();

    public Job() {
    }

    public Job(String id) {
        this.state = StateEnum.CREATED;
        this.id = id;
    }

    public Job(Job.StateEnum state, String id) {
        this.state = state;
        this.id = id;
    }

    public Job.StateEnum getState() {
        return state;
    }

    @SuppressWarnings("UnusedReturnValue")
    public Job setState(Job.StateEnum status) {
        this.state = status;
        return this;
    }

    public String getId() {
        return id;
    }

    public Job setId(String id) {
        this.id = id;
        return this;
    }

    @SuppressWarnings("unused")
    public Set<JobResult> getResults() {
        return results;
    }

    public void addResult(JobResult jobResult) {
        results.add(jobResult);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Job job)) return false;
        return state == job.state && Objects.equals(id, job.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, id);
    }

    @Override
    public String toString() {
        return "CheckJob{" +
                "state=" + state +
                ", ID='" + id + '\'' +
                '}';
    }
}
