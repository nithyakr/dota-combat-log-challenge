package gg.bayes.challenge.service.impl;

import gg.bayes.challenge.persistence.model.CombatLogEntryEntity;
import gg.bayes.challenge.persistence.repository.CombatLogEntryRepository;
import gg.bayes.challenge.rest.model.HeroDamage;
import gg.bayes.challenge.rest.model.HeroItem;
import gg.bayes.challenge.rest.model.HeroKills;
import gg.bayes.challenge.rest.model.HeroSpells;
import gg.bayes.challenge.service.MatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CombatMatchService implements MatchService {

    private final CombatLogEntryRepository combatLogEntryRepository;

    public CombatMatchService(CombatLogEntryRepository combatLogEntryRepository) {
        this.combatLogEntryRepository = combatLogEntryRepository;
    }

    @Override
    public List<HeroKills> listHeroKillsForMatch(Long matchId) {
        log.info("Request received to fetch Kills by Heros for the match [{}]", matchId);
        return combatLogEntryRepository.findKillsForMatch(matchId);
    }

    @Override
    public List<HeroItem> listItemsForHero(Long matchId, String heroName) {
        log.info("Request received to fetch items bought by Hero [{}] in Match [{}]", heroName, matchId);
        List<CombatLogEntryEntity> itemPurchaseEntities = combatLogEntryRepository.findByMatchIdAndActorAndType(matchId, heroName, CombatLogEntryEntity.Type.ITEM_PURCHASED);
        return itemPurchaseEntities.stream().map(entity -> new HeroItem(entity.getItem(), entity.getTimestamp())).collect(Collectors.toList());
    }

    @Override
    public List<HeroSpells> listNumberOfSpellCasts(Long matchId, String heroName) {
        log.info("Request received to get spell casts by Hero [{}] in Match [{}]", heroName, matchId);
        return combatLogEntryRepository.findSpellCastsForTheHero(matchId, heroName);
    }

    @Override
    public List<HeroDamage> calculateDamageDoneByHero(Long matchId, String heroName) {
        log.info("Request received to calculate damage done by Hero [{}] in Match [{}]", heroName, matchId);
        return combatLogEntryRepository.calculateDamageDoneByHero(matchId, heroName);
    }


}
