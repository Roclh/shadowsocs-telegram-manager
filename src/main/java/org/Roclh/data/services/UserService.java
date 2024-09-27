package org.Roclh.data.services;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.entities.UserModel;
import org.Roclh.data.repositories.UserRepository;
import org.Roclh.sh.ScriptRunner;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Getter
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @EventListener(ContextRefreshedEvent.class)
    @Order(10)
    public void init() {
        getAllUsers().stream()
                .filter(userModel -> userModel.isAdded() && userModel.getUsedPort() != null && userModel.getPassword() != null)
                .forEach(this::executeShScriptAddUser);
    }

    public boolean saveUser(@NonNull UserModel userModel) {
        try {
            userRepository.saveAndFlush(getUser(userModel.getUserModel().getId()).map(user->{
                        user.getUserModel().setRole(userModel.getUserModel().getRole());
                        user.getUserModel().setTelegramId(userModel.getUserModel().getTelegramId());
                        user.getUserModel().setTelegramName(userModel.getUserModel().getTelegramName());
                        user.getUserModel().setChatId(userModel.getUserModel().getChatId());
                        user.setPassword(userModel.getPassword());
                        user.setAdded(userModel.isAdded());
                        user.setUsedPort(userModel.getUsedPort());
                        return user;
                    }
            ).orElse(userModel));
            return true;
        } catch (Exception e) {
            log.error("Failed to add user", e);
            return false;
        }
    }
    public Optional<UserModel> getUser(@NonNull Long userId) {
        return userRepository.findByUserModel_TelegramId(userId);
    }

    public boolean changePassword(Long telegramId, String password) {
        return userRepository.updatePasswordByUserModel_TelegramId(password, telegramId) > 0;
    }

    public boolean deleteUser(@NonNull Long telegramId){
        return userRepository.deleteByUserModel_TelegramId(telegramId) > 0;
    }
    public boolean delUser(@NonNull UserModel userModel) {
        return userRepository.deleteByUserModel_TelegramId(userModel.getUserModel().getTelegramId()) > 0;
    }

    public boolean delUser(@NonNull Long port) {
        return userRepository.deleteByUsedPort(port) > 0;
    }

    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }


    public boolean changeUserEnabled(Long userId, boolean isEnabled) {
        boolean isChanged = userRepository.updateIsAddedByUserModel_TelegramId(isEnabled, userId) > 0;
        boolean wasExecuted = false;
        if (isChanged) {
            wasExecuted = getUser(userId).map((userModel1) -> {
                        if (isEnabled) {
                            return executeShScriptAddUser(userModel1);
                        } else {
                            return executeShScriptDisableUser(userModel1);
                        }
                    }
            ).orElse(false);
        }
        return isChanged && wasExecuted;
    }

    public boolean executeShScriptChangePassword(@NonNull UserModel userModel) {
        return executeShScriptDisableUser(userModel) && executeShScriptAddUser(userModel);
    }

    public boolean executeShScriptAddUser(@NonNull UserModel userModel) {
        return executeShScriptAddUser(userModel, "add_user_java_script.sh");
    }

    public boolean executeShScriptDisableUser(@NonNull UserModel userModel) {
        return executeShScriptDisableUser(userModel, "disable_user_java_script.sh");
    }

    public boolean executeShScriptAddUser(@NonNull UserModel userModel, String scriptPath) {
        if (!ScriptRunner.isShScriptExists(scriptPath)) {
            String scriptContent = """
                    #!/bin/bash
                                        
                    DIR=/etc/shadowsocks-libev
                    BASE_CONFIG=${DIR}/${1}.json
                    if [ $(netstat -tlp | grep -o ${2}" " | wc -l) -eq 0 ]; then
                            cp ${DIR}/config-example.json ${BASE_CONFIG}
                        
                            sed -i '/server_port/c\\    "server_port":'${2}',' ${BASE_CONFIG}
                            sed -i '/password/c\\    "password":"'${3}'",' ${BASE_CONFIG}
                        
                            screen -dmS ${1} ss-server -c /etc/shadowsocks-libev/${1}.json -u
                            echo screen ${1} started
                            echo screen -dmS ${1} ss-server -c /etc/shadowsocks-libev/${1}.json -u >> /etc/shadowsocks-libev/allactiveusers.txt
                            echo ${1}>> /etc/shadowsocks-libev/justusers.txt
                    else
                            echo screen ${1} port ${2} already exists or other problem
                    fi
                        
                    netstat -tlp | grep ss-server
                                        
                    """;
            ScriptRunner.createShScript(scriptContent, scriptPath);
        }
        if(userModel.getUsedPort() == null || userModel.getPassword() == null){
            return false;
        }
        return ScriptRunner.runCommand(new String[]{"./" + scriptPath, userModel.getUserModel().getTelegramName() + ":" + userModel.getUserModel().getTelegramId(),
                        userModel.getUsedPort().toString(), userModel.getPassword()},
                (output) -> output.contains("started"));
    }

    public boolean executeShScriptDisableUser(@NonNull UserModel userModel, String scriptPath) {
        if(userModel.getUsedPort() == null || userModel.getPassword() == null){
            return false;
        }
        if (!ScriptRunner.isShScriptExists(scriptPath)) {
            String scriptContent = """
                    #!/bin/bash
                              DIR=/etc/shadowsocks-libev
                              
                              if [ $(grep ${1} ${DIR}/justusers.txt | wc -l) -ne 0 ]; then
                                      grep -v ${1} ${DIR}/justusers.txt > ${DIR}/tmpfile2; mv ${DIR}/tmpfile2 ${DIR}/justusers.txt
                                      screen -S ${1} -X quit
                                      rm ${DIR}/${1}.json
                                      echo succesfully removed ${1}
                              else
                                      echo no user ${1} check screen -ls and netstat -ltp
                              fi
                              
                              netstat -ltp | grep ss-server
                    """;
            ScriptRunner.createShScript(scriptContent, scriptPath);
        }
        return ScriptRunner.runCommand(new String[]{"./" + scriptPath, userModel.getUserModel().getTelegramName() + ":" + userModel.getUserModel().getTelegramId(), userModel.getUsedPort().toString()},
                (output) -> output.contains("succesfully removed"));
    }

}
