package org.Roclh.data;

public enum Role {
    GUEST(0), USER(1), MANAGER(2), ROOT(3);

    public final int prior;

    Role(int prior){
        this.prior = prior;
    }

}
