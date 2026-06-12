package org.dm.conveyor.repository;

import org.dm.conveyor.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, String> {
}
