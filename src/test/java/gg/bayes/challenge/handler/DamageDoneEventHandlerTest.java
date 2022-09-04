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
public class DamageDoneEventHandlerTest extends BaseEventHandlerTest {

    @Autowired
    private DamageDoneEventHandler damageDoneEventHandler;

    @Override
    protected EventHandler getEventHandler() {
        return this.damageDoneEventHandler;
    }

    @BeforeAll
    public void prerequisites() {
        prerequisites(CombatLogEntryEntity.Type.DAMAGE_DONE);
    }

    @Test
    public void TestSuccessEvent() {

        String successFullEvent = "[00:10:42.031] npc_dota_hero_abyssal_underlord hits npc_dota_hero_bloodseeker with abyssal_underlord_firestorm for 18 damage (720->702)";

        Optional<CombatLogEntryEntity> optionalEvent = generateEventFromHandler(successFullEvent);

        assertThat(optionalEvent).isNotEmpty();

        CombatLogEntryEntity event = optionalEvent.get();

        assertThat(event.getTimestamp()).isEqualTo(calculateEventTime());
        assertThat(event.getActor()).isEqualTo("abyssal_underlord");
        assertThat(event.getTarget()).isEqualTo("bloodseeker");
        assertThat(event.getAbility()).isEqualTo("abyssal_underlord_firestorm");
        assertThat(event.getDamage()).isEqualTo(18);
        assertThat(event.getType()).isEqualByComparingTo(CombatLogEntryEntity.Type.DAMAGE_DONE);
    }

    @Test
    public void TestOutOfOrderEvent() {

        String malformedEvent = "[00:10:42.031] abyssal_underlord hits bloodseeker with abyssal_underlord_firestorm for eighteen damage (720->702)";

        Optional<CombatLogEntryEntity> optionalEvent = generateEventFromHandler(malformedEvent);

        assertThat(optionalEvent).isEmpty();
    }

    @Test
    public void TestInvalidTime() {

        String malformedEvent = "[00:abc:42.031] npc_dota_hero_abyssal_underlord hits npc_dota_hero_bloodseeker with abyssal_underlord_firestorm for 18 damage (720->702)";

        Optional<CombatLogEntryEntity> optionalEvent = generateEventFromHandler(malformedEvent);

        assertThat(optionalEvent).isEmpty();
    }

    private Long calculateEventTime() {
        return (10 * 60_000) + (42 * 1000) + 31L;
    }

}
