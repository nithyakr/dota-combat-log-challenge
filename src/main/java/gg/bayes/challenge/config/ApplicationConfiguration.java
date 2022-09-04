package gg.bayes.challenge.config;

import gg.bayes.challenge.handler.EventHandler;
import gg.bayes.challenge.persistence.model.CombatLogEntryEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class ApplicationConfiguration {

    @Autowired
    @Bean
    public Map<CombatLogEntryEntity.Type, EventHandler> eventHandlerMap(List<EventHandler> initializedEventHandlers) {
        Map<CombatLogEntryEntity.Type, EventHandler> eventHandlerMap = new HashMap<>();
        initializedEventHandlers.forEach(handler -> eventHandlerMap.put(handler.getType(), handler));
        log.info("Event Handler Map Created [{}]", eventHandlerMap);
        return eventHandlerMap;
    }

}
