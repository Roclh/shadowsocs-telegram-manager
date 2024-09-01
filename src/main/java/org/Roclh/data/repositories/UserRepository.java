package org.Roclh.data.repositories;

import org.Roclh.data.model.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByTelegramId(String telegramId);

    long deleteByTelegramId(String telegramId);

    long deleteByUsedPort(String usedPort);

    @Transactional
    @Modifying
    @Query("update UserModel u set u.password = ?1 where u.telegramId = ?2")
    int updatePasswordByTelegramId(String password, String telegramId);

    @Transactional
    @Modifying
    @Query("update UserModel u set u.isAdded = ?1 where u.telegramId = ?2")
    int updateIsAddedByTelegramId(boolean isAdded, String telegramId);
}
