package org.Roclh.ss;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

import javax.validation.constraints.NotBlank;
import java.io.FileWriter;
import java.io.IOException;

@Configuration
@Data
@ConfigurationProperties(prefix = "shadowsocks")
@Slf4j
public class ShadowsocksProperties {
    @NotBlank
    private String address;
    @NotBlank
    private String availablePorts;

    @EventListener(ContextRefreshedEvent.class)
    @Order(0)
    public void init() {
        try (FileWriter fileWriter = new FileWriter("/etc/shadowsocks-libev/config-example.json")) {
            String configuration = """
                    {
                        "server":"${address}",
                        "mode":"tcp_and_udp",
                        "server_port":8488,
                        "local_port":1080,
                        "password":"123456",
                        "timeout":60,
                        "method":"chacha20-ietf-poly1305"
                    }
                    """;
            configuration = configuration.replace("${address}", this.address);
            fileWriter.write(configuration);
            log.info("Created a configuration file with content {}", configuration);
        } catch (IOException e) {
            log.error("Failed to create a file", e);
        }
    }

    public PortsRange getPortRange(){
        return PortsRange.from(availablePorts);
    }


    @Getter
    public static class PortsRange{
        private final long leftRangeLimit;
        private final long rightRangeLimit;

        private PortsRange(long leftRangeLimit, long rightRangeLimit){
            this.leftRangeLimit = leftRangeLimit;
            this.rightRangeLimit = rightRangeLimit;
        }

        public static PortsRange from(String port){
            String[] portLimits = port.split(":");
            return new PortsRange(Long.parseLong(portLimits[0]), Long.parseLong(portLimits[1]));
        }
    }
}