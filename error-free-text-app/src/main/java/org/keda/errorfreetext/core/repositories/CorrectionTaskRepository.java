package org.keda.errorfreetext.core.repositories;

import org.keda.errorfreetext.core.domain.CorrectionTaskEntity;
import org.keda.errorfreetext.core.domain.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CorrectionTaskRepository extends JpaRepository<CorrectionTaskEntity, Long> {

    Optional<CorrectionTaskEntity> findByTaskUuid(UUID uuid);

    Page<CorrectionTaskEntity> findByTaskStatusOrderByCreatedAtAsc(TaskStatus status, Pageable pageable);

}
