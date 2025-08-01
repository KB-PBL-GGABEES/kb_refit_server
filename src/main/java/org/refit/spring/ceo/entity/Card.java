package org.refit.spring.ceo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    private Long cardId;
    private boolean isCorporate;
    private String cardNumber;
    private Long userId;
}
