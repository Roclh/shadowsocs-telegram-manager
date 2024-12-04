package org.Roclh.sh;

public interface ShScript<T>{
    T execute(String... args);

    void init();
}
