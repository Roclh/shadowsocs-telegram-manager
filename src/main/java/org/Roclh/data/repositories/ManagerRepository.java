package org.Roclh.data.repositories;

import org.Roclh.data.model.manager.ManagerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ManagerRepository extends JpaRepository<ManagerModel, Long> {

    ManagerModel findByTelegramId(String telegramId);

    @Transactional
    @Modifying
    long deleteByTelegramId(String telegramId);
}
