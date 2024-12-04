package org.Roclh.sh.scripts;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.data.entities.BandwidthModel;
import org.Roclh.sh.ScriptRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateBandwidthRuleScript extends AbstractShScript<Boolean> {
    private final GetAllActiveRulesScript getAllActiveRulesScript;
    protected CreateBandwidthRuleScript(GetAllActiveRulesScript getAllActiveRulesScript) {
        super("set_bandwidth_rule_script.sh", """
                #!/bin/bash
                                    
                port=${1}
                bandwidth=${2}
                burst_rate=${3}
                                    
                tc filter add dev eth0 parent 1: protocol ip basic match 'cmp(u16 at 0 layer transport eq '$port')' action police rate $bandwidth burst $burst_rate
                if [$(tc filter show dev eth0 | grep -o $port | wc -l}) -eq 0]; then
                    echo Failed to add bandwidth
                else
                    echo Successfully added bandwidth $bandwidth for port $port with burst rate $burst_rate
                fi
                """);
        this.getAllActiveRulesScript = getAllActiveRulesScript;
    }

    @Override
    public Boolean execute(String... args) {
        init();
        return ScriptRunner.runCommand(new String[]{"./" + fileName, args[0],
                        args[1], args[2]},
                result -> result.contains("Successfully added bandwidth"));
    }

    public Boolean execute(BandwidthModel bandwidthModel){
        if (bandwidthModel.getBandwidth() == null || bandwidthModel.getUserModel().getUsedPort() == null ||
                getAllActiveRulesScript.execute().containsValue(bandwidthModel.getUserModel().getUsedPort())) {
            return false;
        }
        return ScriptRunner.runCommand(new String[]{"./" + fileName, bandwidthModel.getUserModel().getUsedPort().toString(),
                        bandwidthModel.getBandwidth().getBandwidth(), bandwidthModel.getBandwidth().getBurst()},
                result -> result.contains("Successfully added bandwidth"));
    }
}
