package gg.bayes.challenge.persistence.repository;

import gg.bayes.challenge.persistence.model.CombatLogEntryEntity;
import gg.bayes.challenge.rest.model.HeroDamage;
import gg.bayes.challenge.rest.model.HeroKills;
import gg.bayes.challenge.rest.model.HeroSpells;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CombatLogEntryRepository extends JpaRepository<CombatLogEntryEntity, Long> {

    /**
     * Queries all the kills for the requested match from the Database
     *
     * @param matchId ID of the match
     * @return List of Kills taken by each hero in the match
     */
    @Query("SELECT new gg.bayes.challenge.rest.model.HeroKills(actor, count(*) as kills) " +
            "FROM CombatLogEntryEntity c " +
            "WHERE c.match.id = :matchId " +
            "AND type = 'HERO_KILLED' " +
            "GROUP BY actor ORDER BY kills DESC")
    List<HeroKills> findKillsForMatch(@Param("matchId") Long matchId);

    /**
     * Queries the Database for the Combat entities filtered by match, hero and the Combat Entity Type
     *
     * @param matchId ID of the match
     * @param actor   Name of the Hero
     * @param type    Type of the Combat Entry
     * @return List of the match Entity records
     */
    List<CombatLogEntryEntity> findByMatchIdAndActorAndType(Long matchId, String actor, CombatLogEntryEntity.Type type);

    /**
     * Queries the database for the spells in a match by the specified hero
     *
     * @param matchId  ID of the Match
     * @param heroName Name of the Hero
     * @return List of the spells done by the hero in the match along with number of occurrences
     */
    @Query("SELECT new gg.bayes.challenge.rest.model.HeroSpells(ability, count(*) as casts) " +
            "FROM CombatLogEntryEntity c " +
            "WHERE c.match.id = :matchId " +
            "AND type = 'SPELL_CAST' " +
            "AND c.actor = :heroName " +
            "GROUP BY ability ORDER BY casts ASC")
    List<HeroSpells> findSpellCastsForTheHero(@Param("matchId") Long matchId,
                                              @Param("heroName") String heroName);

    /**
     * Queries the database for the Damage done by the specified hero in a match
     *
     * @param matchId  ID of the match
     * @param heroName Name of the Hero
     * @return The damage done by the hero agains all other heros in the match along with total damage and occurrences
     */
    @Query("SELECT new gg.bayes.challenge.rest.model.HeroDamage(target, count(*) as hits, SUM(damage) as total) " +
            "FROM CombatLogEntryEntity c " +
            "WHERE c.match.id = :matchId " +
            "AND type = 'DAMAGE_DONE' " +
            "AND c.actor = :heroName " +
            "GROUP BY target ORDER BY total DESC")
    List<HeroDamage> calculateDamageDoneByHero(@Param("matchId") Long matchId,
                                               @Param("heroName") String heroName);

}
