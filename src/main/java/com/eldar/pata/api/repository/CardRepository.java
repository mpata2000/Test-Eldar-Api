package com.eldar.pata.api.repository;

import com.eldar.pata.api.model.Card;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
    // all crud database methods
}
