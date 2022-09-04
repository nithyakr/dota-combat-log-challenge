package gg.bayes.challenge.service.impl;

import gg.bayes.challenge.handler.EventHandler;
import gg.bayes.challenge.persistence.model.CombatLogEntryEntity;
import gg.bayes.challenge.persistence.model.MatchEntity;
import gg.bayes.challenge.persistence.repository.MatchRepository;
import gg.bayes.challenge.service.LogFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static gg.bayes.challenge.persistence.model.CombatLogEntryEntity.Type.*;

@Service
@Slf4j
public class CombatLogFileService implements LogFileService {

    private final Map<CombatLogEntryEntity.Type, EventHandler> eventHandlerMap;
    private final MatchRepository matchRepository;

    @Autowired
    public CombatLogFileService(Map<CombatLogEntryEntity.Type, EventHandler> eventHandlerMap,
                                MatchRepository matchRepository) {
        this.eventHandlerMap = eventHandlerMap;
        this.matchRepository = matchRepository;
    }

    @Override
    public MatchEntity processFile(String content) {
        String[] lines = content.split("\n");
        MatchEntity matchEntity = new MatchEntity();
        for (String line : lines) {
            CombatLogEntryEntity.Type eventType = identifyEventType(line);
            if (eventType != null) {
                createCombatEvent(matchEntity, line, eventType);
            }
        }
        MatchEntity savedEntity = matchRepository.saveAndFlush(matchEntity);
        log.info("Saved Entity [{}] with [{}] Combat Entries", savedEntity.getId(), savedEntity.getCombatLogEntries().size());
        return savedEntity;
    }

    private void createCombatEvent(MatchEntity matchEntity, String line, CombatLogEntryEntity.Type eventType) {
        EventHandler eventHandler = eventHandlerMap.get(eventType);
        if (eventHandler == null) {
            log.warn("No Event Handler found for Event Type [{}]. " +
                    "This is probably due to programming error. Please register a bean type of Event Handler for the Event Type [{}]", eventType, eventType);
        } else {
            eventHandler.generateEventEntity(matchEntity, line).ifPresent(matchEntity.getCombatLogEntries()::add);
        }
    }

    private CombatLogEntryEntity.Type identifyEventType(String content) {

        if (content.contains("buys item")) {
            return ITEM_PURCHASED;
        } else if (content.contains("is killed by")) {
            return HERO_KILLED;
        } else if (content.contains("casts ability")) {
            return SPELL_CAST;
        } else if (content.contains("hits")) {
            return DAMAGE_DONE;
        }
        return null;
    }
}
