package org.keda.errorfreetext.core.repositories;

import org.keda.errorfreetext.core.domain.CorrectionTask;
import org.keda.errorfreetext.core.domain.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CorrectionTaskRepository extends JpaRepository<CorrectionTask, Long> {

    Optional<CorrectionTask> findByUuid(UUID uuid);

    Page<CorrectionTask> findByStatusOrderByCreatedAtAsc(TaskStatus status, Pageable pageable);

}
