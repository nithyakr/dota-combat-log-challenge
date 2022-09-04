package gg.bayes.challenge.handler;

import gg.bayes.challenge.util.Constants;
import org.springframework.beans.factory.InitializingBean;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public abstract class AbstractEventHandler implements EventHandler, InitializingBean {

    /**
     * Calculates the Event timestamp from the given time format.
     * Convert the current Event Time to Java Date and get the millis from epoch.
     * And then get the Match start time (00:00:00.000) as Java date and get the millis since epoch.
     * <p>
     * Deduct the Match start millis from event start millis, which will give the millis of the event since the match start.
     *
     * @param timestamp Event Date time as a String format (HH:mm:ss.SSS)
     * @return the milliseconds of the event since the match start
     * @throws ParseException incase of the Given timestamp is not in the expected format
     */
    protected long calculateEventTimestamp(String timestamp) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.EVENT_TIME_FORMAT);
        long timestampOfEvent = dateFormat.parse(timestamp).getTime();
        long timestampOfStartOfTheMatch = dateFormat.parse(Constants.EVENT_START_TIMESTAMP).getTime();
        return timestampOfEvent - timestampOfStartOfTheMatch;
    }

    @Override
    public String toString() {
        return "Event Handler of Type [".concat(getType().toString()).concat("]");
    }

}
