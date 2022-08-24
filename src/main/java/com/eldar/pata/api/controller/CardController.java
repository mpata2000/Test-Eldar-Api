package com.eldar.pata.api.controller;

import com.eldar.pata.api.exceptions.ResourceNotFoundException;
import com.eldar.pata.api.model.Card;
import com.eldar.pata.api.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/cards")
public class CardController {

    @Autowired
    private CardRepository cardRepository;

    @GetMapping
    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    @GetMapping("{cardNumber}")
    public Card getCard(@PathVariable long cardNumber) {
        return cardRepository.findById(cardNumber).orElseThrow(() -> new ResourceNotFoundException("Card not found"));
    }

    @PostMapping
    public Card createCard(@RequestBody Card card) {
        return cardRepository.save(card);
    }

    @GetMapping("{cardNumber}/operation/{amount}")
    public ResponseEntity<String> getCardOperationFee(@PathVariable long cardNumber, @PathVariable int amount) {
        Card card = cardRepository.findById(cardNumber).orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        return new ResponseEntity<>(card.operation(amount), HttpStatus.OK);
    }
    //delete card
    @DeleteMapping("{cardNumber}")
    public ResponseEntity<HttpStatus> deleteCard(@PathVariable long cardNumber) {
        Card card = cardRepository.findById(cardNumber).orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        cardRepository.delete(card);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
