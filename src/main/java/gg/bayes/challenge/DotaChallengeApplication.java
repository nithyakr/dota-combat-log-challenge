package gg.bayes.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DotaChallengeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DotaChallengeApplication.class, args);
    }

}
