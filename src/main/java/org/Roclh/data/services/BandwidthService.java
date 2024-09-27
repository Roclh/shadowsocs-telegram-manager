package org.Roclh.data.services;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.entities.BandwidthModel;
import org.Roclh.data.entities.UserModel;
import org.Roclh.data.repositories.BandwidthRepository;
import org.Roclh.sh.ScriptRunner;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class BandwidthService {
    private boolean wasInitialized = false;

    private final BandwidthRepository bandwidthRepository;

    @EventListener(ContextRefreshedEvent.class)
    @Order(11)
    private void init() {
        executeShScriptCreateRuleset();
        bandwidthRepository.findAll().stream()
                .filter(bandwidthModel -> bandwidthModel.getBandwidth() != null && bandwidthModel.getUserModel().getUsedPort() != null)
                .forEach(this::executeShScriptSetBandwidthRule);
    }

    public boolean setRule(@Nullable BandwidthModel bandwidthModel) {
        if (bandwidthModel == null) {
            return false;
        }
        try {
            bandwidthRepository.saveAndFlush(getRule(bandwidthModel.getUserModel())
                    .map(rule -> {
                        if (bandwidthModel.getBandwidth() != null) {
                            rule.setBandwidth(bandwidthModel.getBandwidth());
                        }
                        return rule;
                    }).orElse(bandwidthModel));
            return true;
        } catch (Exception e) {
            log.error("Failed to save a bandwidth rule {}", bandwidthModel, e);
        }
        return false;
    }

    public Optional<BandwidthModel> getRule(UserModel userModel) {
        return bandwidthRepository.findByUserModel(userModel);
    }

    public Optional<BandwidthModel> getRule(Long telegramId) {
        return bandwidthRepository.findByUserModel_UserModel_TelegramId(telegramId);
    }

    public boolean deleteRule(BandwidthModel bandwidthModel){
        return bandwidthRepository.deleteByUserModel(bandwidthModel.getUserModel()) > 0;
    }

    public boolean executeShScriptSetBandwidthRule(@NonNull BandwidthModel bandwidthModel) {
        return executeShScriptSetBandwidthRule(bandwidthModel, "set_bandwidth_rule_script.sh");
    }

    private boolean executeShScriptSetBandwidthRule(@NonNull BandwidthModel bandwidthModel, @NonNull String scriptPath) {
        if (bandwidthModel.getBandwidth() == null || bandwidthModel.getUserModel().getUsedPort() == null || isPortAlreadyHasARule(bandwidthModel.getUserModel().getUsedPort())) {
            return false;
        }
        if (!ScriptRunner.isShScriptExists(scriptPath)) {
            String scriptContent = """
                    #!/bin/bash
                                        
                    port=${1}
                    bandwidth=${2}
                    burst_rate=${3}
                                        
                    tc filter add dev eth0 parent 1: protocol ip basic match 'cmp(u16 at 0 layer transport eq '$port')' action police rate $bandwidth burst $burst_rate
                    if [$(tc filter show dev eth0 | grep -o $port | wc -l}) -eq 0] then
                        echo Failed to add bandwidth
                    else
                        echo Successfully added bandwidth $bandwidth for port $port with burst rate $burst_rate
                    fi
                    """;
            ScriptRunner.createShScript(scriptContent, scriptPath);
        }
        return ScriptRunner.runCommand(new String[]{"./" + scriptPath, bandwidthModel.getUserModel().getUsedPort().toString(),
                        bandwidthModel.getBandwidth().getBandwidth(), bandwidthModel.getBandwidth().getBurst()},
                result -> result.contains("Successfully added bandwidth"));
    }

    public boolean executeShScriptDeleteBandwidthRule(@NonNull BandwidthModel bandwidthModel){
        return executeShScriptDeleteBandwidthRule(bandwidthModel, "delete_bandwidth_rule.sh");
    }
    private boolean executeShScriptDeleteBandwidthRule(@NonNull BandwidthModel bandwidthModel, @NonNull String scriptPath){
        if(bandwidthModel.getUserModel().getUsedPort() == null || !isPortAlreadyHasARule(bandwidthModel.getUserModel().getUsedPort())){
            return false;
        }
        if(!ScriptRunner.isShScriptExists(scriptPath)){
            String scriptContent = """
                    #!/bin/bash
                    
                    rule_number=${1}
                    
                    tc filter del dev eth0 prio $rule_number
                    
                    if [$(tc filter show dev eth0 | grep -o $port | wc -l}) -eq 0] then
                        echo Successfully removed rule by number $rule_number
                    else
                        echo Failed to add bandwidth
                    fi
                    """;
            ScriptRunner.createShScript(scriptContent, scriptPath);
        }
        return executeShScriptGetAllActiveRules()
                .entrySet().stream().filter(entry->bandwidthModel.getUserModel().getUsedPort().equals(entry.getValue())).map(Map.Entry::getKey).findFirst()
                .map(ruleNumber -> ScriptRunner.runCommand(new String[]{"./" + scriptPath, ruleNumber.toString()})).orElse(false);
    }

    private boolean isPortAlreadyHasARule(Long port){
        return executeShScriptGetAllActiveRules().containsValue(port);
    }

    private Map<Long, Long> executeShScriptGetAllActiveRules(){
        return executeShScriptGetAllActiveRules("get_all_active_rules.sh");
    }
    private Map<Long, Long> executeShScriptGetAllActiveRules(@NonNull String scriptPath) {
        if (!ScriptRunner.isShScriptExists(scriptPath)) {
            String scriptContent = """
                    #!/bin/bash
                                        
                    port=${1}
                    bandwidth=${2}
                    burst_rate=${3}
                                        
                    tc filter show dev eth0 | grep -oE "pref .* basic chain 0 handle| eq .*)" | grep -oE "[0-9]{2,6}"
                    """;
            ScriptRunner.createShScript(scriptContent, scriptPath);
        }
        return Optional.ofNullable(ScriptRunner.runCommandWithResult(new String[]{"./" + scriptPath})).map(
                result -> {
                    String[] values = result.split("\n");
                    return IntStream.range(0, values.length / 2).boxed()
                            .collect(Collectors.toMap(i -> Long.parseLong(values[i*2]), i ->Long.parseLong(values[i*2+1])));
                }
        ).orElse(Map.of());
    }

    private void executeShScriptCreateRuleset() {
        if (wasInitialized) {
            return;
        }
        String scriptPath = "create_ruleset.sh";
        if (!ScriptRunner.isShScriptExists(scriptPath)) {
            String scriptContent = """
                    #!/bin/bash
                                        
                    tc qdisc add dev eth0 root handle 1: prio
                                        
                    """;
            ScriptRunner.createShScript(scriptContent, scriptPath);
        }
        ScriptRunner.runCommand(new String[]{"./" + scriptPath});
        wasInitialized = true;
    }
}
