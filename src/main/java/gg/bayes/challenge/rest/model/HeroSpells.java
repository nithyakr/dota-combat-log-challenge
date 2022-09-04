package gg.bayes.challenge.rest.model;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class HeroSpells {
    String spell;
    Long casts;
}
