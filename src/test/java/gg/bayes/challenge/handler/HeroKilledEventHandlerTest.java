package gg.bayes.challenge.handler;

import gg.bayes.challenge.persistence.model.CombatLogEntryEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
public class HeroKilledEventHandlerTest extends BaseEventHandlerTest {

    @Autowired
    private HeroKilledEventHandler heroKilledEventHandler;

    @Override
    protected EventHandler getEventHandler() {
        return this.heroKilledEventHandler;
    }

    @BeforeAll
    public void prerequisites() {
        prerequisites(CombatLogEntryEntity.Type.HERO_KILLED);
    }

    @Test
    public void TestSuccessEvent() {

        String successFullEvent = "[00:11:17.489] npc_dota_hero_snapfire is killed by npc_dota_hero_mars";

        Optional<CombatLogEntryEntity> optionalEvent = generateEventFromHandler(successFullEvent);

        assertThat(optionalEvent).isNotEmpty();

        CombatLogEntryEntity event = optionalEvent.get();

        assertThat(event.getTimestamp()).isEqualTo(calculateEventTime());
        assertThat(event.getActor()).isEqualTo("mars");
        assertThat(event.getTarget()).isEqualTo("snapfire");
        assertThat(event.getType()).isEqualByComparingTo(CombatLogEntryEntity.Type.HERO_KILLED);
    }

    @Test
    public void TestOutOfOrderEvent() {

        String malformedEvent = "[00:11:17.489] npc_dota_hero_mars killed npc_dota_hero_snapfire";

        Optional<CombatLogEntryEntity> optionalEvent = generateEventFromHandler(malformedEvent);

        assertThat(optionalEvent).isEmpty();
    }

    @Test
    public void TestInvalidTime() {

        String malformedEvent = "[00:11:17.dd] npc_dota_hero_mars killed npc_dota_hero_snapfire";

        Optional<CombatLogEntryEntity> optionalEvent = generateEventFromHandler(malformedEvent);

        assertThat(optionalEvent).isEmpty();
    }

    private Long calculateEventTime() {
        return (11 * 60_000) + (17 * 1000) + 489L;
    }
}
