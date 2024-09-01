package org.Roclh.data.model.user;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.repositories.UserRepository;
import org.Roclh.utils.ScriptRunner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Getter
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        getAllUsers().stream()
                .filter(userModel -> userModel.isAdded() && userModel.getUsedPort() != null && userModel.getPassword() != null)
                .forEach(this::executeShScriptAddUser);

    }

    public boolean saveUser(@NonNull UserModel userModel) {
        try {
            userRepository.save(userModel);
            return true;
        } catch (Exception e) {
            log.error("Failed to add user", e);
            return false;
        }
    }

    public Optional<UserModel> getUser(@NonNull String userId) {
        return userRepository.findByTelegramId(userId);
    }

    public boolean changePassword(String telegramId, String password) {
        return userRepository.updatePasswordByTelegramId(password, telegramId) > 0;
    }

    public boolean delUser(@NonNull UserModel userModel) {
        return userRepository.deleteByTelegramId(userModel.getTelegramId()) > 0;
    }

    public boolean delUser(@NonNull String port) {
        return userRepository.deleteByUsedPort(port) > 0;
    }

    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }


    public boolean changeUserEnabled(String userId, boolean isEnabled) {
        boolean isChanged = userRepository.updateIsAddedByTelegramId(isEnabled, userId) > 0;
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
        return ScriptRunner.runCommand(new String[]{"./" + scriptPath, userModel.getTelegramName() + ":" + userModel.getTelegramId(), userModel.getUsedPort(), userModel.getPassword()},
                (output) -> output.contains("started"));
    }

    public boolean executeShScriptDisableUser(@NonNull UserModel userModel, String scriptPath) {
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
        return ScriptRunner.runCommand(new String[]{"./" + scriptPath, userModel.getTelegramName() + ":" + userModel.getTelegramId(), userModel.getUsedPort(), userModel.getPassword()},
                (output) -> output.contains("succesfully removed"));
    }

}
