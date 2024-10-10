package com.pine;

public interface Mutable {
    /**
     * Stores id of last consumed version
     */
    default void freezeVersion(){
    }

    default boolean isFrozen(){
        return false;
    }

    int getChangeId();

    void registerChange();
}
