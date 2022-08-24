package com.eldar.pata.api.model;

import com.eldar.pata.api.exceptions.InvalidOperationException;
import com.eldar.pata.api.exceptions.NotValidCardBrand;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.Calendar;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cards_table")
public class Card {

    public static final double MIN_FEE = 0.3;
    public static final double MAX_FEE = 5.0;

    @Id
    @Column(name = "number", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long number;

    @Column(name = "holder", nullable = false)
    protected String holder;

    @Column(name = "brand", nullable = false)
    protected String brand;

    @Column(name = "expirationDate", nullable = false)
    protected String expirationDate;

    public Card(String holder, String brand, String expirationDate) {

        if (StringUtils.isBlank(holder) || StringUtils.isBlank(brand)) {
            throw new IllegalArgumentException("Card holder, card brand and card expiration date must not be null or empty");
        }

        if(!(brand.equalsIgnoreCase("AMEX") || brand.equalsIgnoreCase("VISA") || brand.equalsIgnoreCase("NARA"))){
            throw new NotValidCardBrand();
        }


        if (!expirationDate.matches("^(0[1-9]|1[0-2])[/]([0-9]{2})$")) {
            throw new IllegalArgumentException("Card expiration date must be in the format MM/YY");
        }
        this.holder = holder;
        this.brand = brand;
        this.expirationDate = expirationDate;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return number == card.number &&
                holder.equals(card.holder) &&
                brand.equals(card.brand) &&
                expirationDate.equals(card.expirationDate);
    }

    protected double serviceFeeLimit(double fee){
        return Math.min(Math.max(fee,MIN_FEE),MAX_FEE);
    }
    public double serviceFee(){
        return serviceFeeLimit(CardBrand.getServiceFee(this.brand));
    }

    public String toJSON(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Override
    public String toString() {
        return "Card{" +
                "cardNumber=" + number +
                ", cardHolder='" + holder + '\'' +
                ", cardBrand='" + brand + '\'' +
                ", cardExpirationDate='" + expirationDate + '\'' +
                '}';
    }
    public boolean isExpired() {
        String[] date = expirationDate.split("/");
        int month = Integer.parseInt(date[0]);
        int year = Integer.parseInt(date[1]);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1; // Calendar month is 0 base (january == 0)
        int currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100;

        return currentYear > year || (currentYear == year && currentMonth > month);
    }

    public boolean canOperate(){
        return !this.isExpired();
    }

    public boolean validOperation(int operationAmount) {
        return !this.isExpired() && operationAmount < 1000;
    }

    public String operation(int operationAmount) {
        if(!this.validOperation(operationAmount)){
            throw new InvalidOperationException();
        }

        JSONObject json = new JSONObject();
        json.put("cardBrand", this.brand);
        json.put("serviceFee", this.serviceFee());
        json.put("serviceTotal", this.serviceFee()*operationAmount);
        json.put("operationAmount", operationAmount);
        json.put("operationTotal", operationAmount + this.serviceFee()*operationAmount);
        return json.toJSONString();
    }

}
