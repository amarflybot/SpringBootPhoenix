package com.example;

/**
 * Created by amarendra on 14/04/17.
 */
public enum IsParallel {
    SEQUENTIAL(false),
    PARALLEL(true);

    private final boolean flag;

    IsParallel(boolean flag) {
        this.flag = flag;
    }

    boolean getFlag() { return flag; }
}
