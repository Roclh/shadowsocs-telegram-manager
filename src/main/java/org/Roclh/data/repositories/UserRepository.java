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

    @Transactional
    @Modifying
    long deleteByTelegramId(String telegramId);
    @Transactional
    @Modifying
    long deleteByUsedPort(String usedPort);

    @Transactional
    @Modifying
    @Query("update UserModel u set u.password = ?1 where u.telegramId = ?2")
    int updatePasswordByTelegramId(String password, String telegramId);

    @Transactional
    @Modifying
    @Query("update UserModel u set u.isAdded = ?1 where u.telegramId = ?2")
    int updateIsAddedByTelegramId(boolean isAdded, String telegramId);

    @Transactional
    @Modifying
    @Query("""
            update UserModel u set u.id = ?1, u.telegramName = ?2, u.usedPort = ?3, u.password = ?4, u.isAdded = ?5
            where u.telegramId = ?6""")
    int updateByTelegramId(Long id, String telegramName, String usedPort, String password, boolean isAdded, String telegramId);

    @Transactional
    @Modifying
    @Query("update UserModel u set u.usedPort = ?1, u.password = ?2, u.isAdded = ?3 where u.telegramId = ?4")
    int updateUsedPortAndPasswordAndIsAddedByTelegramId(String usedPort, String password, boolean isAdded, String telegramId);
}
