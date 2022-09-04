package gg.bayes.challenge.handler;

import gg.bayes.challenge.persistence.model.CombatLogEntryEntity;
import gg.bayes.challenge.persistence.model.MatchEntity;

import java.util.Optional;

public interface EventHandler {

    CombatLogEntryEntity.Type getType();

    /**
     * Generate the Combat Event Entity from the raw event line of the log
     *
     * @param matchEntity The match entity, this event belongs to
     * @param event       The raw line from the log which contains the event information
     * @return The Created Combat Log Entry. Optional.empty incase if the event line is not honouring the expected format
     */
    Optional<CombatLogEntryEntity> generateEventEntity(MatchEntity matchEntity, String event);

}
