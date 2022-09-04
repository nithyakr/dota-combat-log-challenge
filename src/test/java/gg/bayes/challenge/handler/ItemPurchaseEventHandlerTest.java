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
public class ItemPurchaseEventHandlerTest extends BaseEventHandlerTest{

    @Autowired
    private ItemPurchaseEventHandler itemPurchaseEventHandler;

    @Override
    protected EventHandler getEventHandler() {
        return this.itemPurchaseEventHandler;
    }

    @BeforeAll
    public void prerequisites() {
        prerequisites(CombatLogEntryEntity.Type.ITEM_PURCHASED);
    }

    @Test
    public void TestSuccessEvent() {

        String successFullEvent = "[00:08:47.426] npc_dota_hero_dragon_knight buys item item_gauntlets";

        Optional<CombatLogEntryEntity> optionalEvent = generateEventFromHandler(successFullEvent);

        assertThat(optionalEvent).isNotEmpty();

        CombatLogEntryEntity event = optionalEvent.get();

        assertThat(event.getTimestamp()).isEqualTo(calculateEventTime());
        assertThat(event.getActor()).isEqualTo("dragon_knight");
        assertThat(event.getItem()).isEqualTo("gauntlets");
        assertThat(event.getType()).isEqualByComparingTo(CombatLogEntryEntity.Type.ITEM_PURCHASED);
    }

    @Test
    public void TestOutOfOrderEvent() {

        String malformedEvent = "[00:08:47.426] npc_dota_dragon_knight buys item item_gauntlets";

        Optional<CombatLogEntryEntity> optionalEvent = generateEventFromHandler(malformedEvent);

        assertThat(optionalEvent).isEmpty();
    }

    @Test
    public void TestInvalidTime() {

        String malformedEvent = "[hours:08:47.426] npc_dota_hero_dragon_knight buys item item_gauntlets";

        Optional<CombatLogEntryEntity> optionalEvent = generateEventFromHandler(malformedEvent);

        assertThat(optionalEvent).isEmpty();
    }

    private Long calculateEventTime() {
        return (8 * 60_000) + (47 * 1000) + 426L;
    }
}
