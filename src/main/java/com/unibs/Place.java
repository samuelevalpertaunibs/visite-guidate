package com.unibs;

public class Place {
    private String name;
    private String description;
    private String region;
    private String district;
    private String city;
    private String address;

    public Place(String name, String description, String region, String district, String city, String address) {
        this.name = name;
        this.description = description;
        this.region = region;
        this.district = district;
        this.city = city;
        this.address = address;
    }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public String getRegion() { return region; }

    public String getDistrict() { return district; }

    public String getCity() { return city; }

    public String getAddress() { return address; }
}