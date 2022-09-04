package gg.bayes.challenge.service;

import gg.bayes.challenge.persistence.model.MatchEntity;
import gg.bayes.challenge.rest.model.HeroKills;

import java.util.List;

public interface LogFileService {

    /**
     * Processes the file content and save the Match entity into the DB along with the combat events
     *
     * @param content content of the file received
     * @return The saved Match Entity
     */
    MatchEntity processFile(String content);

}
