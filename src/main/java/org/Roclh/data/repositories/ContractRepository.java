package org.Roclh.data.repositories;

import org.Roclh.data.entities.ContractModel;
import org.Roclh.data.entities.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractRepository  extends JpaRepository<ContractModel, Long> {
    Optional<ContractModel> findByUserModel(UserModel userModel);

    Optional<ContractModel> findByUserModel_UserModel_TelegramId(Long telegramId);
}
