package gg.bayes.challenge.rest.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import gg.bayes.challenge.handler.DamageDoneEventHandler;
import gg.bayes.challenge.rest.model.HeroDamage;
import gg.bayes.challenge.rest.model.HeroItem;
import gg.bayes.challenge.rest.model.HeroKills;
import gg.bayes.challenge.rest.model.HeroSpells;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/*
 * Integration test template to get you started. Add tests and make modifications as you see fit.
 */
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MatchControllerIntegrationTest {

    private static final String COMBATLOG_FILE_1 = "/data/combatlog_1.log.txt";
    private static final String COMBATLOG_FILE_2 = "/data/combatlog_2.log.txt";
    public static final TypeReference<List<Map<String, Object>>> GENERIC_JSON_LIST_RESPONSE_TYPE = new TypeReference<>() {
    };

    @Autowired
    private MockMvc mvc;
    @Autowired
    private DamageDoneEventHandler damageDoneEventHandler;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Gson GSON = new Gson();
    private Map<String, Long> matchIds;

    @BeforeAll
    void setup() throws Exception {
        // Populate the database with all events from both sample data files and store the returned
        // match IDS.
        matchIds = Map.of(
                COMBATLOG_FILE_1, ingestMatch(COMBATLOG_FILE_1),
                COMBATLOG_FILE_2, ingestMatch(COMBATLOG_FILE_2));
    }


    @Test
    public void heroKillsApiTest() throws Exception {
        String responseOfFirstMatch = mvc.perform(get("/api/match/{matchId}", matchIds.get(COMBATLOG_FILE_1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String responseOfSecondMatch = mvc.perform(get("/api/match/{matchId}", matchIds.get(COMBATLOG_FILE_2))
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Type heroKillsType = TypeToken.getParameterized(List.class, HeroKills.class).getType();

        List<HeroKills> killsInFirstMatch = GSON.fromJson(responseOfFirstMatch, heroKillsType);
        List<HeroKills> killsInSecondMatch = GSON.fromJson(responseOfSecondMatch, heroKillsType);

        assertThat(killsInFirstMatch.size()).isEqualTo(10);
        assertThat(killsInSecondMatch.size()).isEqualTo(9);

        Map<String, Long> firstMatchKillsMap = killsInFirstMatch.stream().collect(Collectors.toMap(HeroKills::getHero, HeroKills::getKills));
        Map<String, Long> secondMatchKillsMap = killsInSecondMatch.stream().collect(Collectors.toMap(HeroKills::getHero, HeroKills::getKills));

        assertThat(firstMatchKillsMap.get("bloodseeker")).isPositive();
        assertThat(firstMatchKillsMap.get("bloodseeker")).isEqualTo(11);

        assertThat(firstMatchKillsMap.get("dragon_knight")).isPositive();
        assertThat(firstMatchKillsMap.get("dragon_knight")).isEqualTo(3);

        assertThat(firstMatchKillsMap.get("unknown-user")).isNull();

        assertThat(secondMatchKillsMap.get("ember_spirit")).isPositive();
        assertThat(secondMatchKillsMap.get("ember_spirit")).isEqualTo(14);

        assertThat(secondMatchKillsMap.get("gyrocopter")).isPositive();
        assertThat(secondMatchKillsMap.get("gyrocopter")).isEqualTo(3);

        assertThat(secondMatchKillsMap.get("unknown-user-match-2")).isNull();
    }

    @Test
    public void heroItemPurchaseApiTest() throws Exception {
        String dragonKnightPurchasesFromFirstMatch = mvc.perform(get("/api/match/{matchId}/{heroName}/items", matchIds.get(COMBATLOG_FILE_1), "dragon_knight")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String monkeyKingPurchasesFromSecondMatch = mvc.perform(get("/api/match/{matchId}/{heroName}/items", matchIds.get(COMBATLOG_FILE_2), "monkey_king")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Type heroKillsType = TypeToken.getParameterized(List.class, HeroItem.class).getType();

        List<HeroItem> purchasesOfFirstMatch = GSON.fromJson(dragonKnightPurchasesFromFirstMatch, heroKillsType);
        List<HeroItem> purchasesOfSecondMatch = GSON.fromJson(monkeyKingPurchasesFromSecondMatch, heroKillsType);

        assertThat(purchasesOfFirstMatch.size()).isEqualTo(32);
        assertThat(purchasesOfSecondMatch.size()).isEqualTo(35);

    }

    @Test
    public void heroSpellCastsApiTest() throws Exception {
        String firstMatchResponse = mvc.perform(get("/api/match/{matchId}/{heroName}/spells", matchIds.get(COMBATLOG_FILE_1), "bloodseeker")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String secondMatchResponse = mvc.perform(get("/api/match/{matchId}/{heroName}/spells", matchIds.get(COMBATLOG_FILE_2), "grimstroke")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();


        Type heroSpellsType = TypeToken.getParameterized(List.class, HeroSpells.class).getType();

        List<HeroSpells> spellsInFirstMatch = GSON.fromJson(firstMatchResponse, heroSpellsType);
        List<HeroSpells> spellsInSecondMatch = GSON.fromJson(secondMatchResponse, heroSpellsType);

        assertThat(spellsInFirstMatch.size()).isEqualTo(3);
        assertThat(spellsInSecondMatch.size()).isEqualTo(4);

        Map<String, Long> firstMatchSpellsMap = spellsInFirstMatch.stream().collect(Collectors.toMap(HeroSpells::getSpell, HeroSpells::getCasts));
        Map<String, Long> secondMatchSpellsMap = spellsInSecondMatch.stream().collect(Collectors.toMap(HeroSpells::getSpell, HeroSpells::getCasts));

        assertThat(firstMatchSpellsMap.get("bloodseeker_rupture")).isPositive();
        assertThat(firstMatchSpellsMap.get("bloodseeker_rupture")).isEqualTo(10);

        assertThat(firstMatchSpellsMap.get("bloodseeker_blood_bath")).isPositive();
        assertThat(firstMatchSpellsMap.get("bloodseeker_blood_bath")).isEqualTo(19);

        assertThat(firstMatchSpellsMap.get("non-existing-spell-match-1")).isNull();

        assertThat(secondMatchSpellsMap.get("grimstroke_ink_creature")).isPositive();
        assertThat(secondMatchSpellsMap.get("grimstroke_ink_creature")).isEqualTo(15);

        assertThat(secondMatchSpellsMap.get("grimstroke_dark_artistry")).isPositive();
        assertThat(secondMatchSpellsMap.get("grimstroke_dark_artistry")).isEqualTo(68);

        assertThat(secondMatchSpellsMap.get("non-existing-spell-match-2")).isNull();

    }

    @Test
    public void heroDamageApiTest() throws Exception {
        String firstMatchResponse = mvc.perform(get("/api/match/{matchId}/{heroName}/damage", matchIds.get(COMBATLOG_FILE_1), "abyssal_underlord")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String secondMatchResponse = mvc.perform(get("/api/match/{matchId}/{heroName}/damage", matchIds.get(COMBATLOG_FILE_2), "earthshaker")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();


        Type heroDamageType = TypeToken.getParameterized(List.class, HeroDamage.class).getType();

        List<HeroDamage> damageDoneInFirstMatch = GSON.fromJson(firstMatchResponse, heroDamageType);
        List<HeroDamage> damageDoneInSecondMatch = GSON.fromJson(secondMatchResponse, heroDamageType);

        assertThat(damageDoneInFirstMatch.size()).isEqualTo(5);
        assertThat(damageDoneInSecondMatch.size()).isEqualTo(5);

        Map<String, HeroDamage> firstMatchKillsMap = damageDoneInFirstMatch.stream().collect(Collectors.toMap(HeroDamage::getTarget, heroDamage -> heroDamage));
        Map<String, HeroDamage> secondMatchKillsMap = damageDoneInSecondMatch.stream().collect(Collectors.toMap(HeroDamage::getTarget, heroDamage -> heroDamage));

        assertThat(firstMatchKillsMap.get("death_prophet")).isNotNull();
        assertThat(firstMatchKillsMap.get("death_prophet").getDamageInstances()).isEqualTo(76);
        assertThat(firstMatchKillsMap.get("death_prophet").getTotalDamage()).isEqualTo(5865);

        assertThat(firstMatchKillsMap.get("rubick")).isNotNull();
        assertThat(firstMatchKillsMap.get("rubick").getDamageInstances()).isEqualTo(27);
        assertThat(firstMatchKillsMap.get("rubick").getTotalDamage()).isEqualTo(1624);

        assertThat(firstMatchKillsMap.get("non-existing-damage-match-1")).isNull();

        assertThat(secondMatchKillsMap.get("keeper_of_the_light")).isNotNull();
        assertThat(secondMatchKillsMap.get("keeper_of_the_light").getDamageInstances()).isEqualTo(19);
        assertThat(secondMatchKillsMap.get("keeper_of_the_light").getTotalDamage()).isEqualTo(1175);

        assertThat(secondMatchKillsMap.get("monkey_king")).isNotNull();
        assertThat(secondMatchKillsMap.get("monkey_king").getDamageInstances()).isEqualTo(14);
        assertThat(secondMatchKillsMap.get("monkey_king").getTotalDamage()).isEqualTo(1388);

        assertThat(secondMatchKillsMap.get("non-existing-damage-match-2")).isNull();

    }

    /**
     * Helper method that ingests a combat log file and returns the match id associated with all parsed events.
     *
     * @param file file path as a classpath resource, e.g.: /data/combatlog_1.log.txt.
     * @return the id of the match associated with the events parsed from the given file
     * @throws Exception if an error happens when reading or ingesting the file
     */
    private Long ingestMatch(String file) throws Exception {
        String fileContent = IOUtils.resourceToString(file, StandardCharsets.UTF_8);

        return Long.parseLong(mvc.perform(post("/api/match")
                                         .contentType(MediaType.TEXT_PLAIN)
                                         .content(fileContent))
                                 .andReturn()
                                 .getResponse()
                                 .getContentAsString());
    }

    private static class ListTypeReference extends TypeReference<List<HeroKills>> {
        private ListTypeReference() {
        }
    }
}
