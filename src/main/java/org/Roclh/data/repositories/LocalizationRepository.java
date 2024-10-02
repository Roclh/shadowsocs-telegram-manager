package org.Roclh.data.repositories;

import org.Roclh.data.entities.LocalizationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalizationRepository extends JpaRepository<LocalizationModel, Long> {


}