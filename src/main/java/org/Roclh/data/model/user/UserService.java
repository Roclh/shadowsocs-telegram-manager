package org.Roclh.data.model.user;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Getter
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public boolean saveUser(@NonNull UserModel userModel) {
        try{
            userRepository.save(userModel);
            return true;
        }catch (Exception e){
            log.error("Failed to add user", e);
            return false;
        }
    }

    public Optional<UserModel> getUser(@NonNull String userId) {
        return userRepository.findByTelegramId(userId);
    }

    public boolean changePassword(String telegramId, String password){
        return userRepository.updatePasswordByTelegramId(telegramId, password) > 0;
    }

    public boolean delUser(@NonNull UserModel userModel) {
        return userRepository.deleteByTelegramId(userModel.getTelegramId()) > 0;
    }

    public boolean delUser(@NonNull String port) {
        return userRepository.deleteByUsedPort(port) > 0;
    }

    public List<UserModel> getAllUsers(){
        return userRepository.findAll();
    }
}
