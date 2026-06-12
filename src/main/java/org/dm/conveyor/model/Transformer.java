package org.dm.conveyor.model;

public final class Transformer {

    private Transformer() {
    }

    public static JobResult createCheckResult(JobResultEvent jobResultEvent) {
        return new JobResult(jobResultEvent.getState(), jobResultEvent.getName(), jobResultEvent.getDetails());
    }

}
