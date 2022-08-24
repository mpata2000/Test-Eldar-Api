package com.eldar.pata.api.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Calendar;


public class AmericanExpress{

    public double serviceFee() {
        double month = Calendar.getInstance().get(Calendar.MONTH) + 1; // Calendar month is 0 base (january == 0)

        return month*0.1;
    }
}
