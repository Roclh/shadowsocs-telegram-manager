package org.Roclh.data.repositories;

import org.Roclh.data.model.manager.ManagerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRepository extends JpaRepository<ManagerModel, Long> {
    long deleteByTelegramId(String telegramId);
}
