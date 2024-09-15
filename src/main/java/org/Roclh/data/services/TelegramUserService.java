package org.Roclh.data.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.Role;
import org.Roclh.data.entities.TelegramUserModel;
import org.Roclh.data.repositories.TelegramUserRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramUserService {

    private final TelegramUserRepository telegramUserRepository;

    public boolean saveUser(@Nullable TelegramUserModel userModel) {
        if (userModel == null) {
            return false;
        }
        try {
            telegramUserRepository.save(getUser(userModel.getTelegramId())
                    .map(user -> {
                        user.setTelegramId(userModel.getTelegramId());
                        if(userModel.getRole().prior > user.getRole().prior){
                            user.setRole(userModel.getRole());
                        }
                        if (userModel.getTelegramName() != null) {
                            user.setTelegramName(userModel.getTelegramName());
                        }
                        if (userModel.getChatId() != null) {
                            user.setChatId(userModel.getChatId());
                        }
                        return user;
                    })
                    .orElse(userModel));
            return true;
        } catch (Exception e) {
            log.error("Failed to add user", e);
            return false;
        }
    }

    public boolean deleteUser(Long telegramId){
        return telegramUserRepository.deleteByTelegramId(telegramId) > 0;
    }

    public List<TelegramUserModel> getUsers(Predicate<TelegramUserModel> filter) {
        return telegramUserRepository.findAll().stream().filter(filter).toList();
    }

    public List<TelegramUserModel> getUsers() {
        return telegramUserRepository.findAll();
    }

    public boolean exists(Long telegramId) {
        return telegramUserRepository.existsByTelegramId(telegramId);
    }

    public boolean isAllowed(Long telegramId, Role required){
        return telegramUserRepository.findByTelegramId(telegramId).map(
                user -> user.getRole().prior >= required.prior
        ).orElse(false);
    }

    public Optional<TelegramUserModel> getUser(Long telegramId) {
        return telegramUserRepository.findByTelegramId(telegramId);
    }

    public boolean setRole(Long telegramId, Role role) {
        return telegramUserRepository.updateRoleByTelegramId(role, telegramId) > 0;
    }

}
