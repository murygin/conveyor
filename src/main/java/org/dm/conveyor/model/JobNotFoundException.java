package org.dm.conveyor.model;

public class JobNotFoundException extends RuntimeException {

    public JobNotFoundException(String id) {
        super("Job not found: " + id);
    }

}
