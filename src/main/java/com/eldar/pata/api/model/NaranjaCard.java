package com.eldar.pata.api.model;

import javax.persistence.Entity;
import java.util.Calendar;


public class NaranjaCard{

    public double serviceFee() {
        double dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        return dayOfMonth*0.5;
    }
}
