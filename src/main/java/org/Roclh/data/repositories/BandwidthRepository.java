package org.Roclh.data.repositories;

import org.Roclh.data.entities.BandwidthModel;
import org.Roclh.data.entities.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BandwidthRepository extends JpaRepository<BandwidthModel, Long> {
    Optional<BandwidthModel> findByUserModel(UserModel userModel);

    Optional<BandwidthModel> findByUserModel_UserModel_TelegramId(Long telegramId);

    long deleteByUserModel(UserModel userModel);
}
