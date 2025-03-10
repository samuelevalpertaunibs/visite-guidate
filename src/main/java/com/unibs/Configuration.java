package com.unibs;

public class Configuration {
    private TerritorialEnvironment territorialEnvironment;
    private int maxPeople;

    public Configuration() {
        this.maxPeople = 0;
    }

    public TerritorialEnvironment getTerritorialEnvironment() {
        return territorialEnvironment;
    }

    public void setTerritorialEnvironment(TerritorialEnvironment territorialEnvironment) {
        this.territorialEnvironment = territorialEnvironment;
    }

    public int getMaxPeople() {
        return maxPeople;
    }

    public void setMaxPeople(int maxPeople) {
        this.maxPeople = maxPeople;
    }
}