package org.Roclh.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Stream;

@Slf4j
public class PasswordUtils {
    private static final String usedSymbols = "0123456789abcdefghijklmnopqrstuvwxyz";

    public static boolean validate(String password){
        return true;
    }
    public static Optional<String> md5(String password) {
        return Optional.ofNullable(DigestUtils.md5(StringUtils.getBytes(password, Charset.forName("windows-1251")))).map(bytes -> {
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            return Stream.generate(byteBuffer::get).limit(byteBuffer.capacity())
                    .map(b -> usedSymbols.charAt(Math.abs(b) % usedSymbols.length()))
                    .collect(Collector.of(
                            StringBuilder::new,
                            StringBuilder::append,
                            StringBuilder::append,
                            StringBuilder::toString
                    ));
        });
    }
}
