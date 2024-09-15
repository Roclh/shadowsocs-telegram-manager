package org.Roclh.data.repositories;

import org.Roclh.data.entities.ContractModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository  extends JpaRepository<ContractModel, Long> {
}
