package gg.bayes.challenge.persistance.repository;

import gg.bayes.challenge.persistence.model.CombatLogEntryEntity;
import gg.bayes.challenge.persistence.model.MatchEntity;
import gg.bayes.challenge.persistence.repository.CombatLogEntryRepository;
import gg.bayes.challenge.persistence.repository.MatchRepository;
import gg.bayes.challenge.rest.model.HeroDamage;
import gg.bayes.challenge.rest.model.HeroKills;
import gg.bayes.challenge.rest.model.HeroSpells;
import gg.bayes.challenge.util.TestUtility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
public class CombatLogEntryRepositoryTest {

    @Autowired
    private CombatLogEntryRepository combatEventRepository;
    @Autowired
    private MatchRepository matchRepository;
    private Long matchId;

    @BeforeAll
    public void setup() {
        matchRepository.deleteAll();
        MatchEntity testEntity = TestUtility.createTestMatch();
        matchId = matchRepository.saveAndFlush(testEntity).getId();
    }

    @Test
    public void testGetAllEvents() {
        List<CombatLogEntryEntity> all = combatEventRepository.findAll();
        assertThat(all.size()).isEqualTo(40);
    }

    @Test
    public void testKills() {
        List<HeroKills> killsForMatch = combatEventRepository.findKillsForMatch(matchId);
        Map<String, Long> killsMap = killsForMatch.stream().collect(Collectors.toMap(HeroKills::getHero, HeroKills::getKills));
        assertThat(killsMap.get("actor0")).isEqualTo(4);
        assertThat(killsMap.get("actor1")).isEqualTo(3);
        assertThat(killsMap.get("actor2")).isEqualTo(3);
    }

    @Test
    public void testFindByMatchActorAndType() {
        List<CombatLogEntryEntity> actor0Purchases = combatEventRepository.findByMatchIdAndActorAndType(matchId, "actor0", CombatLogEntryEntity.Type.ITEM_PURCHASED);
        List<CombatLogEntryEntity> actor1Purchases = combatEventRepository.findByMatchIdAndActorAndType(matchId, "actor1", CombatLogEntryEntity.Type.ITEM_PURCHASED);
        List<CombatLogEntryEntity> actor2Purchases = combatEventRepository.findByMatchIdAndActorAndType(matchId, "actor2", CombatLogEntryEntity.Type.ITEM_PURCHASED);

        assertThat(actor0Purchases.size()).isEqualTo(4);
        assertThat(actor1Purchases.size()).isEqualTo(3);
        assertThat(actor2Purchases.size()).isEqualTo(3);

        List<String> actor0ItemNames = actor0Purchases.stream().map(CombatLogEntryEntity::getItem).collect(Collectors.toList());
        List<String> actor1ItemNames = actor1Purchases.stream().map(CombatLogEntryEntity::getItem).collect(Collectors.toList());
        List<String> actor2ItemNames = actor2Purchases.stream().map(CombatLogEntryEntity::getItem).collect(Collectors.toList());

        assertThat(actor0ItemNames).containsAll(Arrays.asList("item0", "item3", "item6", "item9"));
        assertThat(actor1ItemNames).containsAll(Arrays.asList("item1", "item4", "item7"));
        assertThat(actor2ItemNames).containsAll(Arrays.asList("item2", "item5", "item8"));
    }

    @Test
    public void testSpellCasts() {
        List<HeroSpells> actor0SpellCasts = combatEventRepository.findSpellCastsForTheHero(matchId, "actor0");
        List<HeroSpells> actor1SpellCasts = combatEventRepository.findSpellCastsForTheHero(matchId, "actor1");
        List<HeroSpells> actor2SpellCasts = combatEventRepository.findSpellCastsForTheHero(matchId, "actor2");

        assertThat(actor0SpellCasts.size()).isEqualTo(1);
        assertThat(actor1SpellCasts.size()).isEqualTo(1);
        assertThat(actor2SpellCasts.size()).isEqualTo(1);

        assertThat(actor0SpellCasts.get(0).getSpell()).isEqualTo("spell0");
        assertThat(actor1SpellCasts.get(0).getSpell()).isEqualTo("spell1");
        assertThat(actor2SpellCasts.get(0).getSpell()).isEqualTo("spell2");

        assertThat(actor0SpellCasts.get(0).getCasts()).isEqualTo(4);
        assertThat(actor1SpellCasts.get(0).getCasts()).isEqualTo(3);
        assertThat(actor2SpellCasts.get(0).getCasts()).isEqualTo(3);

    }

    @Test
    public void testDamageDone() {
        List<HeroDamage> damageByActor0 = combatEventRepository.calculateDamageDoneByHero(matchId, "actor0");
        List<HeroDamage> damageByActor1 = combatEventRepository.calculateDamageDoneByHero(matchId, "actor1");
        List<HeroDamage> damageByActor2 = combatEventRepository.calculateDamageDoneByHero(matchId, "actor2");

        assertThat(damageByActor0.size()).isEqualTo(1);
        assertThat(damageByActor1.size()).isEqualTo(1);
        assertThat(damageByActor2.size()).isEqualTo(1);

        assertThat(damageByActor0.get(0).getTarget()).isEqualTo("target0");
        assertThat(damageByActor1.get(0).getTarget()).isEqualTo("target1");
        assertThat(damageByActor2.get(0).getTarget()).isEqualTo("target2");

        assertThat(damageByActor0.get(0).getDamageInstances()).isEqualTo(4);
        assertThat(damageByActor1.get(0).getDamageInstances()).isEqualTo(3);
        assertThat(damageByActor2.get(0).getDamageInstances()).isEqualTo(3);

        //Damage is calculated as 10 + i whereas 'i' is the index of the for loop in TestUtility
        assertThat(damageByActor0.get(0).getTotalDamage()).isEqualTo(10 + 13 + 16 + 19);
        assertThat(damageByActor1.get(0).getTotalDamage()).isEqualTo(11 + 14 + 17);
        assertThat(damageByActor2.get(0).getTotalDamage()).isEqualTo(12 + 15 + 18);

    }


}
