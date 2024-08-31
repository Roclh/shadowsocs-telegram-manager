package org.Roclh.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.CharSequenceInputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Slf4j
public class PasswordGenerator {
    public static Optional<Byte[]> md5(String password) {
        return Optional.ofNullable(md5(CharSequenceInputStream.builder().setCharSequence(password).get()));
    }

    @Nullable
    public static Byte[] md5(InputStream password) {
        try {
            return ArrayUtils.toObject(DigestUtils.md5Digest(password.readAllBytes()));
        } catch (IOException e) {
            log.error("Failed to generate md5 from password");
            return null;
        }
    }
}
