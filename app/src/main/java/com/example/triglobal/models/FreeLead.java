package com.example.triglobal.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class FreeLead extends Lead {
    public FreeLead() {
        super();
    }

    @JsonProperty("amount_matched")
    private String amountMatched;
    @JsonProperty("credit_amount")
    private int cost;
    @JsonProperty("time_left")
    private String timeLeft;

    public String getAmountMatched() {
        return amountMatched;
    }

    public int getCost() {
        return cost;
    }

    public String getTimeLeft() {
        return timeLeft;
    }

}
