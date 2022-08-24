package com.eldar.pata.api;

import com.eldar.pata.api.model.Card;
import com.eldar.pata.api.model.AmericanExpress;
import com.eldar.pata.api.exceptions.NotValidCardBrand;
import com.eldar.pata.api.model.VisaCard;
import com.eldar.pata.api.model.NaranjaCard;
import com.eldar.pata.api.exceptions.InvalidOperationException;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import com.google.gson.JsonObject;


import java.util.Calendar;

import static com.eldar.pata.api.model.Card.MAX_FEE;
import static com.eldar.pata.api.model.Card.MIN_FEE;
import static org.junit.jupiter.api.Assertions.*;


public class CardTest {

    @Test
    public void CardCreateWithValidValuesCreatesACard(){
        Card card = new Card("Martin","AMEX","07/22");
        assertNotNull(card);
    }


    @Test
    public void CardHolderCanNotBeNull(){
        assertThrows(IllegalArgumentException.class, ()->  new Card(null,"AMEX","07/22"));
    }

    @Test
    public void CardHolderCanNotBeBlank(){
        assertThrows(IllegalArgumentException.class, ()->  new Card("","AMEX","07/22"));
        assertThrows(IllegalArgumentException.class, ()->  new Card("   ","AMEX","07/22"));
    }

    @Test
    public void CardBrandCanNotBeBlank(){
        assertThrows(IllegalArgumentException.class, ()->  new Card("x","","07/22"));
        assertThrows(IllegalArgumentException.class, ()->  new Card("x","  ","07/22"));
    }

    @Test
    public void ExperitionDateMustBeMonthYear(){
        assertThrows(IllegalArgumentException.class, ()->  new Card("x","AMEX","7/22"));
        assertThrows(IllegalArgumentException.class, ()->  new Card("x","AMEX","07/224"));
        assertThrows(IllegalArgumentException.class, ()->  new Card("x","AMEX",""));
        assertThrows(IllegalArgumentException.class, ()->  new Card("x","AMEX","11/07/24"));
    }

    @Test
    public void ExperitionDateMustBeValid(){
        assertThrows(IllegalArgumentException.class, ()->  new Card("x","AMEX","13/22"));
        assertThrows(IllegalArgumentException.class, ()->  new Card("x","AMEX","00/22"));
        assertThrows(IllegalArgumentException.class, ()->  new Card("x","AMEX","-02/22"));
    }


    @Test
    public void CardThowsNotValidBrandIfBRandIsntValid(){
        assertThrows(NotValidCardBrand.class, ()->  new Card("Martin","F1","07/22"));
    }

    @Test
    public void CardInformationIsReadableAndCorrect(){
        String cardHolder = "Martin";
        String cardBrand = "NARA";
        String exp = "07/22";

        Card card = new Card(cardHolder,cardBrand,exp);

        JsonObject jsonObject = new Gson().fromJson(card.toJSON(), JsonObject.class);

        assertEquals(cardHolder, jsonObject.get("holder").getAsString());
        assertEquals(cardBrand, jsonObject.get("brand").getAsString());
        assertEquals(exp, jsonObject.get("expirationDate").getAsString());
    }


    @Test
    public void AmexServiceFee(){
        Card card = new Card("Martin","AMEX","09/22");
        double month = Calendar.getInstance().get(Calendar.MONTH) + 1; // Calendar month is 0 base (january == 0)

        assertEquals(Math.min(Math.max(month*0.1,MIN_FEE),MAX_FEE),card.serviceFee());
        assertTrue(0.3 <= card.serviceFee() && card.serviceFee() <= 5.0);
    }

    @Test
    public void VisaServiceFee(){
        Card card = new Card("Martin","VISA","09/22");
        Calendar cal = Calendar.getInstance();
        double year = cal.get(Calendar.YEAR);
        double month = cal.get(Calendar.MONTH) + 1; // Calendar month is 0 base (january == 0)

        assertEquals(Math.min(Math.max(month/(year-2000),MIN_FEE),MAX_FEE),card.serviceFee()); // Deberia cambiarlo a algo que corte el numero
        assertTrue(0.3 <= card.serviceFee() && card.serviceFee() <= 5.0);
    }

    @Test
    public void NaraServiceFee(){
        Card card = new Card("Martin","NARA","09/22");
        double dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        assertEquals(Math.min(Math.max(dayOfMonth*0.5,MIN_FEE),MAX_FEE),card.serviceFee());
        assertTrue(0.3 <= card.serviceFee() && card.serviceFee() <= 5.0);
    }

    @Test
    public void CardWithEqualValuesAreEqual(){
        Card card1 = new Card("Martin","NARA","09/22");
        Card card2 = new Card("Martin","NARA","09/22");
        assertEquals(card1,card2);
    }

    @Test
    public void CardWithDifferentValuesAreNotEqual(){
        Card card1 = new Card("Martin","NARA","09/22");
        Card card2 = new Card("Martin Pata","AMEX","09/23");
        assertNotEquals(card1,card2);
    }

    @Test
    public void CardIsEqualToItself(){
        Card card = new Card("Martin","NARA","09/22");
        assertEquals(card,card);
    }

    @Test
    public void ExpiredCardCanNotOperate(){
        Card card = new Card("Martin","NARA","06/22");
        assertTrue(card.isExpired());
        assertFalse(card.validOperation(100));

        Card cardExpYear = new Card("Martin","NARA","09/12");
        assertTrue(cardExpYear.isExpired());
        assertFalse(cardExpYear.validOperation(100));
    }

    @Test
    public void ValidCardCanOperate100(){
        Card card = new Card("Martin","NARA","09/22");
        assertFalse(card.isExpired());
        assertTrue(card.validOperation(100));
    }

    @Test
    public void ValidCardCantOperate1000(){
        Card card = new Card("Martin","NARA","09/22");
        assertFalse(card.isExpired());
        assertFalse(card.validOperation(1000));
    }

    @Test
    public void ValidOperationReturnJSONWithInformation(){

        Card card = new Card("Martin","NARA","09/22");
        JsonObject jsonObject = new Gson().fromJson(card.operation(100), JsonObject.class);

        assertEquals("NARA", jsonObject.get("cardBrand").getAsString());
        assertEquals(card.serviceFee(), jsonObject.get("serviceFee").getAsDouble());
        assertEquals(100, jsonObject.get("operationAmount").getAsInt());
    }

    @Test
    public void TryingToOperateWithExperiedCardThrowsException(){
        Card card = new Card("Martin","NARA","09/19");
        assertThrows(InvalidOperationException.class, ()-> card.operation(1000));
    }

    @Test
    public void NotExperiedCardCanOperate(){
        Card card = new Card("Martin","NARA","09/22");
        assertFalse(card.isExpired());
        assertTrue(card.canOperate());
    }

    @Test
    public void ExperiedCardCanNotOperate(){
        Card card = new Card("Martin","NARA","09/12");
        assertTrue(card.isExpired());
        assertFalse(card.canOperate());
    }
}
