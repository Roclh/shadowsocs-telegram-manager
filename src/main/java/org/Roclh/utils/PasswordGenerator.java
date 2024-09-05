package org.Roclh.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Slf4j
public class PasswordGenerator {
    public static Optional<String> md5(String password) {
        return Optional.ofNullable(DigestUtils.md5(password)).map(bytes -> {
            try {
                return new String(bytes, "windows-1251");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Impossible state", e);
            }
        });
    }
}
