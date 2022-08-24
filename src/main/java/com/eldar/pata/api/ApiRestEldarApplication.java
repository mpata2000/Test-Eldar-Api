package com.eldar.pata.api;

import com.eldar.pata.api.model.Card;
import com.eldar.pata.api.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiRestEldarApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ApiRestEldarApplication.class, args);
    }

    @Autowired
    private CardRepository cardRepository;

    @Override
    public void run(String... args) throws Exception {
    }
}
