package org.Roclh.ss;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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

    @PostConstruct
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
            configuration.replace("${address}", this.address);
            fileWriter.write(configuration);
        } catch (IOException e) {
            log.error("Failed to create a file", e);
        }
    }
}
