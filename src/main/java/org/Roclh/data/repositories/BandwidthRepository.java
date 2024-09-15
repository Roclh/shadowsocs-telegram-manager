package org.Roclh.data.repositories;

import org.Roclh.data.entities.BandwidthModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BandwidthRepository extends JpaRepository<BandwidthModel, Long> {
}
