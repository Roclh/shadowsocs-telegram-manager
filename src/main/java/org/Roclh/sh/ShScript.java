package org.Roclh.sh;

public interface ShScript<T>{
    String path();
    String content();
    T execute(String... args);
}
