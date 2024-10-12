package com.pine;

public interface Mutable {
    /**
     * Stores id of last consumed version
     */
    default void freezeVersion(){
    }

    default boolean isNotFrozen(){
        return true;
    }

    int getChangeId();

    void registerChange();
}
