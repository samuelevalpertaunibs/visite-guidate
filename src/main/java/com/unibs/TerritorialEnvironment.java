package com.unibs;

public enum TerritorialEnvironment {
    NAZIONALE,
    REGIONALE,
    PROVINCIALE,
    COMUNALE,
    INDIRIZZO;

    public static TerritorialEnvironment getTerritorialEnvironment(int index) {
        return TerritorialEnvironment.values()[index];
    }
}