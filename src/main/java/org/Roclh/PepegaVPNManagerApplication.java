package org.Roclh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class PepegaVPNManagerApplication {
    public static void main(String[] args) {
        log.info("New line!");
        SpringApplication.run(PepegaVPNManagerApplication.class, args);
    }
}