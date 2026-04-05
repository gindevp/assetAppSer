package com.gindevp.app.repository;

import com.gindevp.app.domain.RepairRequestLine;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepairRequestLineRepository extends JpaRepository<RepairRequestLine, Long> {
    List<RepairRequestLine> findByRepairRequest_IdOrderByLineNoAsc(Long repairRequestId);
}
