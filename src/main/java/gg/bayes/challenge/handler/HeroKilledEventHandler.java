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
public class HeroKilledEventHandler extends AbstractEventHandler {

    @Value("${bayes.dota.hero-killed-event-format}")
    private String heroKilledEventPattern;
    private Pattern pattern;

    @Override
    public void afterPropertiesSet() {
        pattern = Pattern.compile(heroKilledEventPattern);
    }

    @Override
    public CombatLogEntryEntity.Type getType() {
        return CombatLogEntryEntity.Type.HERO_KILLED;
    }

    @Override
    public Optional<CombatLogEntryEntity> generateEventEntity(MatchEntity matchEntity, String event) {
        Matcher matcher = pattern.matcher(event);
        log.debug("Event Type Handler [{}] handling Event [{}]", getType(), event);
        if (matcher.find()) {
            String timestamp = matcher.group(1);
            String targetHero = matcher.group(2);
            String actor = matcher.group(3);

            try {
                CombatLogEntryEntity entryEntity = new CombatLogEntryEntity();
                entryEntity.setActor(actor);
                entryEntity.setTarget(targetHero);
                entryEntity.setTimestamp(calculateEventTimestamp(timestamp));
                entryEntity.setType(getType());
                entryEntity.setMatch(matchEntity);
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
