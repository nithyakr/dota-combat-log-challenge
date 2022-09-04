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
public class SpellCastEventHandlerTest extends BaseEventHandlerTest{

    @Autowired
    private SpellCastEventHandler spellCastEventHandler;

    @Override
    protected EventHandler getEventHandler() {
        return this.spellCastEventHandler;
    }

    @BeforeAll
    public void prerequisites() {
        prerequisites(CombatLogEntryEntity.Type.SPELL_CAST);
    }

    @Test
    public void TestSuccessEvent() {

        String successFullEvent = "[00:08:43.460] npc_dota_hero_pangolier casts ability pangolier_swashbuckle (lvl 1) on dota_unknown";

        Optional<CombatLogEntryEntity> optionalEvent = generateEventFromHandler(successFullEvent);

        assertThat(optionalEvent).isNotEmpty();

        CombatLogEntryEntity event = optionalEvent.get();

        assertThat(event.getTimestamp()).isEqualTo(calculateEventTime());
        assertThat(event.getActor()).isEqualTo("pangolier");
        assertThat(event.getAbility()).isEqualTo("pangolier_swashbuckle");
        assertThat(event.getAbilityLevel()).isEqualTo(1);
        assertThat(event.getType()).isEqualByComparingTo(CombatLogEntryEntity.Type.SPELL_CAST);
    }

    @Test
    public void TestOutOfOrderEvent() {

        String malformedEvent = "[00:08:43.460] npc_dota_hero_pangolier casts ability (lvl 1) pangolier_swashbuckle on dota_unknown";

        Optional<CombatLogEntryEntity> optionalEvent = generateEventFromHandler(malformedEvent);

        assertThat(optionalEvent).isEmpty();
    }

    @Test
    public void TestInvalidTime() {

        String malformedEvent = "[652214] npc_dota_hero_pangolier casts ability (lvl 1) pangolier_swashbuckle on dota_unknown";

        Optional<CombatLogEntryEntity> optionalEvent = generateEventFromHandler(malformedEvent);

        assertThat(optionalEvent).isEmpty();
    }

    private Long calculateEventTime() {
        return (8 * 60_000) + (43 * 1000) + 460L;
    }
}
