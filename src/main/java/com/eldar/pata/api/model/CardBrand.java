package com.eldar.pata.api.model;

import com.eldar.pata.api.exceptions.NotValidCardBrand;

public interface CardBrand {
    public static double getServiceFee(String brand){
        switch (brand.toUpperCase()){
            case "VISA":
                return new VisaCard().serviceFee();
            case "NARA":
                return new NaranjaCard().serviceFee();
            case "AMEX":
                return new AmericanExpress().serviceFee();
            default:
                throw new NotValidCardBrand();
        }
    }

    public abstract double serviceFee();
}
