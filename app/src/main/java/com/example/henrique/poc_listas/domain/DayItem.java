package com.example.henrique.poc_listas.domain;

import java.math.BigDecimal;

/**
 * Created by Henrique on 16/03/2017.
 */

public class DayItem {

    private String iteration;
    private String day;
    private String month;
    private BigDecimal value;
    private Boolean paid;

    public DayItem(String iteration, String day, String month, BigDecimal value, Boolean paid) {
        this.iteration = iteration;
        this.day = day;
        this.month = month;
        this.value = value;
        this.paid = paid;
    }

    public DayItem(String iteration, String day, String month, BigDecimal value) {
        this(iteration, day, month, value, false);
    }

    public String getIteration() {
        return iteration;
    }

    public void setIteration(String iteration) {
        this.iteration = iteration;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }
}
