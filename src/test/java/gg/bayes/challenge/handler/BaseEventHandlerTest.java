package gg.bayes.challenge.handler;

import gg.bayes.challenge.persistence.model.CombatLogEntryEntity;
import gg.bayes.challenge.persistence.model.MatchEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseEventHandlerTest {

    protected MatchEntity testEntity;

    protected abstract EventHandler getEventHandler();

    public void prerequisites(CombatLogEntryEntity.Type expectedType) {
        testEntity = new MatchEntity();
        assertThat(getEventHandler().getType()).isEqualByComparingTo(expectedType);
    }

    public Optional<CombatLogEntryEntity> generateEventFromHandler(String event) {
        return getEventHandler().generateEventEntity(testEntity, event);
    }


}
