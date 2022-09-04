package gg.bayes.challenge.service;

import gg.bayes.challenge.rest.model.HeroDamage;
import gg.bayes.challenge.rest.model.HeroItem;
import gg.bayes.challenge.rest.model.HeroKills;
import gg.bayes.challenge.rest.model.HeroSpells;

import java.util.List;

public interface MatchService {

    /**
     * Lists the number of kills carried out by each hero present in the requested Match
     *
     * @param matchId ID of the match
     * @return The list of the Kills per each hero
     */
    List<HeroKills> listHeroKillsForMatch(Long matchId);

    /**
     * Lists all the item purchased by the requested hero in the particular match
     *
     * @param matchId  ID of the interested match
     * @param heroName Name of the hero
     * @return The list of Items purchased by the hero in the specified match
     */
    List<HeroItem> listItemsForHero(Long matchId, String heroName);

    /**
     * Lists all the spells casts by the requested hero in the match and the number of occurrences
     *
     * @param matchId  ID if the match
     * @param heroName Name of the hero
     * @return The list of the spells which is casted by the hero in the match
     */
    List<HeroSpells> listNumberOfSpellCasts(Long matchId, String heroName);

    /**
     * Returns all the damage done by the hero in the specified match
     *
     * @param matchId  ID of the match
     * @param heroName Name of the hero
     * @return The total damage done by the hero against each hero in the match
     */
    List<HeroDamage> calculateDamageDoneByHero(Long matchId, String heroName);

}
