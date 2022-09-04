package gg.bayes.challenge.handler;

import gg.bayes.challenge.persistence.model.CombatLogEntryEntity;
import gg.bayes.challenge.persistence.model.MatchEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class SpellCastEventHandler extends AbstractEventHandler {

    @Value("${bayes.dota.spell-cast-event-format}")
    private String spellCastEventPattern;
    private Pattern pattern;

    @Override
    public void afterPropertiesSet() {
        pattern = Pattern.compile(spellCastEventPattern);
    }

    @Override
    public CombatLogEntryEntity.Type getType() {
        return CombatLogEntryEntity.Type.SPELL_CAST;
    }

    @Override
    public Optional<CombatLogEntryEntity> generateEventEntity(MatchEntity matchEntity, String event) {
        Matcher matcher = pattern.matcher(event);
        log.debug("Event Type Handler [{}] handling Event [{}]", getType(), event);
        if (matcher.find()) {
            String timestamp = matcher.group(1);
            String actor = matcher.group(2);
            String ability = matcher.group(3);
            String abilityLevel = matcher.group(4);

            try {
                CombatLogEntryEntity entryEntity = new CombatLogEntryEntity();
                entryEntity.setActor(actor);
                entryEntity.setTimestamp(calculateEventTimestamp(timestamp));
                entryEntity.setType(getType());
                entryEntity.setMatch(matchEntity);
                entryEntity.setAbility(ability);
                entryEntity.setAbilityLevel(Integer.parseInt(abilityLevel));
                return Optional.of(entryEntity);
            } catch (ParseException ex) {
                log.error("Error occurred while parsing event time, Malformed event!", ex);
            }
        } else {
            log.warn("Event data format is out of order. Will not be processing the event");
        }
        return Optional.empty();
    }
}
