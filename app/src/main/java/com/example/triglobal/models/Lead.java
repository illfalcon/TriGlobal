package com.example.triglobal.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Lead {
    @JsonProperty("re_id")
    private int id;
    @JsonProperty("re_moving_date")
    private Date movingDate;
    @JsonIgnore
    private SimpleDateFormat format;
    @JsonProperty("re_volume_m3")
    private double volumeMeters;
    @JsonProperty("re_volume_ft3")
    private double volumeFeet;
    @JsonProperty("re_volume_calculator")
    private String contents;
    @JsonProperty("re_storage")
    private int storage;
    @JsonProperty("re_packing")
    private int packing;
    @JsonProperty("re_assembly")
    private int assembly;
    @JsonProperty("re_business")
    private int business;
    @JsonProperty("re_street_from")
    private String streetFrom;
    @JsonProperty("re_zipcode_from")
    private String zipcodeFrom;
    @JsonProperty("re_city_from")
    private String cityFrom;
    @JsonProperty("re_co_code_from")
    private String coCodeFrom;
    @JsonProperty("re_street_to")
    private String streetTo;
    @JsonProperty("re_zipcode_to")
    private String zipcodeTo;
    @JsonProperty("re_city_to")
    private String cityTo;
    @JsonProperty("re_co_code_to")
    private String coCodeTo;
    @JsonProperty("re_full_name")
    private String fullName;
    @JsonProperty("re_telephone1")
    private String telephone1;
    @JsonProperty("re_telephone2")
    private String telephone2;
    @JsonProperty("re_email")
    private String email;
    @JsonProperty("re_remarks")
    private String remarks;

    public Lead() {
        format = new SimpleDateFormat("yyyy-MM-dd");
    }

    public int getId() {
        return id;
    }

    public String getMovingDate() {
        if (movingDate != null)
            return format.format(movingDate);
        return "";
    }

    public double getVolumeMeters() {
        return volumeMeters;
    }

    public double getVolumeFeet() {
        return volumeFeet;
    }

    public int isStorage() {
        return storage;
    }

    public int isPacking() {
        return packing;
    }

    public int isAssembly() {
        return assembly;
    }

    public int isBusiness() {
        return business;
    }

    public String getStreetFrom() {
        return streetFrom;
    }

    public String getZipcodeFrom() {
        return zipcodeFrom;
    }

    public String getCityFrom() {
        return cityFrom;
    }

    public String getCoCodeFrom() {
        return coCodeFrom;
    }

    public String getStreetTo() {
        return streetTo;
    }

    public String getZipcodeTo() {
        return zipcodeTo;
    }

    public String getCityTo() {
        return cityTo;
    }

    public String getCoCodeTo() {
        return coCodeTo;
    }

    public String getFullName() {
        return fullName;
    }

    public String getTelephone1() {
        return telephone1;
    }

    public String getTelephone2() {
        return telephone2;
    }

    public String getEmail() {
        return email;
    }

    public String getRemarks() {
        return remarks;
    }

//    public String getContents() {
//        return contents;
//    }
}
