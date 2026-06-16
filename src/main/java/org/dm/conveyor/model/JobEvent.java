package org.dm.conveyor.model;

import java.io.Serializable;
import java.util.Objects;

public class JobEvent implements Serializable {

    private String id;

    private String data;

    private String type;

    public JobEvent() {
    }

    public JobEvent(String data, String type) {
        this.data = data;
        this.type = type;
    }

    public JobEvent(String id, String data, String type) {
        this.id = id;
        this.data = data;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public JobEvent setId(String id) {
        this.id = id;
        return this;
    }

    public String getData() {
        return data;
    }

    @SuppressWarnings("UnusedReturnValue")
    public JobEvent setData(String data) {
        this.data = data;
        return this;
    }

    @SuppressWarnings("unused")
    public String getType() {
        return type;
    }

    @SuppressWarnings("unused")
    public JobEvent setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof JobEvent that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(data, that.data) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, data, type);
    }

    @Override
    public String toString() {
        return String.format("JobEvent{id='%s', url='%s', fileType='%s'}", id, data, type);
    }
}
