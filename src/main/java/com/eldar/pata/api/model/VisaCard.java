package com.eldar.pata.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import java.util.Calendar;



public class VisaCard{
    public VisaCard() {
    }

    public double serviceFee() {
        Calendar cal = Calendar.getInstance();
        double year = cal.get(Calendar.YEAR);
        double month = cal.get(Calendar.MONTH) + 1; // Calendar month is 0 base (january == 0)

        return  month/(year%100);
    }

}
