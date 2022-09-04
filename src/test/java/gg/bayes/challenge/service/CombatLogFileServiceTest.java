package gg.bayes.challenge.service;

import gg.bayes.challenge.persistence.model.CombatLogEntryEntity;
import gg.bayes.challenge.persistence.model.MatchEntity;
import gg.bayes.challenge.service.impl.CombatLogFileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;


import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
public class CombatLogFileServiceTest {

    private static final String content =
            "[00:08:43.460] npc_dota_hero_pangolier casts ability pangolier_swashbuckle (lvl 1) on dota_unknown\n" +
                    "[00:08:46.759] npc_dota_hero_dragon_knight buys item item_quelling_blade\n" +
                    "[00:08:48.059] npc_dota_hero_dragon_knight buys item item_circlet\n" +
                    "[00:08:46.992] npc_dota_hero_dragon_knight buys item item_gauntlets\n"+
                    "[00:00:04.999] game state is now 2\n"+
                    "[00:19:19.538] npc_dota_hero_rubick uses item_tango\n"+
                    "[00:11:17.489] npc_dota_hero_snapfire is killed by npc_dota_hero_mars\n"+
                    "[00:10:42.031] npc_dota_hero_abyssal_underlord hits npc_dota_hero_bloodseeker with abyssal_underlord_firestorm for 18 damage (720->702)\n" +
                    "[00:10:42.031] npc_dota_hero_abyssal_underlord hits npc_dota_hero_bloodseeker with abyssal_underlord_firestorm for 5 damage (702->697)\n" +
                    "[00:10:43.064] npc_dota_hero_abyssal_underlord hits npc_dota_hero_bloodseeker with abyssal_underlord_firestorm for 5 damage (698->693)\n" +
                    "[00:10:43.064] npc_dota_hero_abyssal_underlord hits npc_dota_hero_bloodseeker with abyssal_underlord_firestorm for 18 damage (693->675)\n"+
                    "[00:10:59.893] npc_dota_hero_snapfire casts ability snapfire_scatterblast (lvl 1) on dota_unknown";

    @InjectMocks
    @Autowired
    private CombatLogFileService combatLogFileService;

    @Test
    public void processFileTest() {

        MatchEntity processedEntity = combatLogFileService.processFile(content);
        Set<CombatLogEntryEntity> combatLogEntries = processedEntity.getCombatLogEntries();
        assertThat(combatLogEntries.size()).isEqualTo(10);

        long spellCastEvents = combatLogEntries.stream().filter(entry -> entry.getType() == CombatLogEntryEntity.Type.SPELL_CAST).count();
        long itemPurchases = combatLogEntries.stream().filter(entry -> entry.getType() == CombatLogEntryEntity.Type.ITEM_PURCHASED).count();
        long killEvents = combatLogEntries.stream().filter(entry -> entry.getType() == CombatLogEntryEntity.Type.HERO_KILLED).count();
        long damageDoneEvents = combatLogEntries.stream().filter(entry -> entry.getType() == CombatLogEntryEntity.Type.DAMAGE_DONE).count();

        assertThat(spellCastEvents).isEqualTo(2);
        assertThat(itemPurchases).isEqualTo(3);
        assertThat(killEvents).isEqualTo(1);
        assertThat(damageDoneEvents).isEqualTo(4);
    }

}
