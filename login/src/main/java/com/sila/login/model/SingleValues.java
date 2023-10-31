package com.sila.login.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "singlevalues")
public class SingleValues {
    @Id
    @Column(name = "valuename")
    private String valuename;

    @Column(name = "value")
    private int value;

    public SingleValues() {
        
    }

    public SingleValues(String valuename, int value) {
        this.valuename = valuename;
        this.value = value;
    }

    public String getValuename() {
        return this.valuename;
    }

    public void setValuename(String valuename) {
        this.valuename = valuename;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
