package org.Roclh.bot;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;
import java.util.List;

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
    private Long defaultManagerId;
    @NotBlank
    @Value("#{'${telegram.supported-locales}'.split(';')}")
    private List<String> supportedLocales;
    @NotBlank
    private String defaultLocale;
    private int maxUpdateBuffer = 30;
    private int maxThreads = 1;
}
