package gg.bayes.challenge.persistence.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "dota_match")
public class MatchEntity {

    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "dota_match_sequence_generator"
    )
    @SequenceGenerator(
            name = "dota_match_sequence_generator",
            sequenceName = "dota_match_sequence",
            allocationSize = 1
    )
    @Id
    @Column(name = "id")
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "match")
    private Set<CombatLogEntryEntity> combatLogEntries = new HashSet<>();
}