package org.Roclh.bot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;

@Configuration
@ConfigurationProperties(prefix = "telegram")
@Data
public class TelegramBotProperties {
    @NotBlank
    private String name;
    @NotBlank
    private String token;
    @NotBlank
    private String paymentToken;
    @NotBlank
    private String defaultManagerId;
    private int maxThreads = 1;
}
