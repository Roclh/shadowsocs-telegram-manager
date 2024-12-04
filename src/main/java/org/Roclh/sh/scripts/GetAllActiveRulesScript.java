package org.Roclh.sh.scripts;

import lombok.extern.slf4j.Slf4j;
import org.Roclh.sh.ScriptRunner;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
public class GetAllActiveRulesScript extends AbstractShScript<Map<Long, Long>>{
    protected GetAllActiveRulesScript() {
        super("get_all_active_rules.sh", """
                    #!/bin/bash
                                        
                    port=${1}
                    bandwidth=${2}
                    burst_rate=${3}
                                        
                    tc filter show dev eth0 | grep -oE "pref .* basic chain 0 handle| eq .*)" | grep -oE "[0-9]{2,6}"
                    """);
    }

    @Override
    public Map<Long, Long> execute(String... args) {
        init();
        return Optional.ofNullable(ScriptRunner.runCommandWithResult(new String[]{"./" + fileName})).map(
                result -> {
                    String[] values = result.split("\n");
                    return IntStream.range(0, values.length / 2).boxed()
                            .collect(Collectors.toMap(i -> Long.parseLong(values[i*2]), i ->Long.parseLong(values[i*2+1])));
                }
        ).orElse(Map.of());
    }
}
