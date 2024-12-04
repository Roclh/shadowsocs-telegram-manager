package org.Roclh.sh.scripts;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.entities.BandwidthModel;
import org.Roclh.sh.ScriptRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class DeleteBandwidthRuleScript extends AbstractShScript<Boolean> {
    private final GetAllActiveRulesScript getAllActiveRulesScript;

    protected DeleteBandwidthRuleScript(GetAllActiveRulesScript getAllActiveRulesScript) {
        super("delete_bandwidth_rule.sh", """
                #!/bin/bash
                                    
                rule_number=${1}
                                    
                tc filter del dev eth0 prio $rule_number
                                    
                if [$(tc filter show dev eth0 | grep -o $port | wc -l}) -eq 0] then
                    echo Successfully removed rule by number $rule_number
                else
                    echo Failed to add bandwidth
                fi
                """);
        this.getAllActiveRulesScript = getAllActiveRulesScript;
    }

    @Override
    public Boolean execute(String... args) {
        return ScriptRunner.runCommand(new String[]{"./" + fileName, args[0]});
    }

    public boolean execute(@NonNull BandwidthModel bandwidthModel) {
        return getAllActiveRulesScript.execute()
                .entrySet().stream()
                .filter(entry -> bandwidthModel.getUserModel().getUsedPort() != null)
                .filter(entry -> bandwidthModel.getUserModel().getUsedPort().equals(entry.getValue())).map(Map.Entry::getKey).findFirst()
                .map(ruleNumber -> execute(ruleNumber.toString())).orElse(false);
    }
}
