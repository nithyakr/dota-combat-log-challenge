package gg.bayes.challenge.util;

import gg.bayes.challenge.persistence.model.CombatLogEntryEntity;
import gg.bayes.challenge.persistence.model.MatchEntity;

public class TestUtility {

    public static MatchEntity createTestMatch() {

        MatchEntity matchEntity = new MatchEntity();

        for (int i = 0; i < 10; i++) {
            CombatLogEntryEntity damageDoneEvent = new CombatLogEntryEntity();
            damageDoneEvent.setActor("actor"+i%3);
            damageDoneEvent.setTarget("target"+i%3);
            damageDoneEvent.setDamage(i+10);
            damageDoneEvent.setTimestamp(123456L);
            damageDoneEvent.setAbility(i%2 == 1 ? "ability"+i : "dota_unknown");
            damageDoneEvent.setType(CombatLogEntryEntity.Type.DAMAGE_DONE);
            matchEntity.getCombatLogEntries().add(damageDoneEvent);
        }

        for (int i = 0; i < 10; i++) {
            CombatLogEntryEntity heroKilledEvent = new CombatLogEntryEntity();
            heroKilledEvent.setActor("actor"+i%3);
            heroKilledEvent.setTarget("target"+i%3);
            heroKilledEvent.setTimestamp(123456L);
            heroKilledEvent.setType(CombatLogEntryEntity.Type.HERO_KILLED);
            matchEntity.getCombatLogEntries().add(heroKilledEvent);
        }

        for (int i = 0; i < 10; i++) {
            CombatLogEntryEntity itemPurchaseEvent = new CombatLogEntryEntity();
            itemPurchaseEvent.setActor("actor"+i%3);
            itemPurchaseEvent.setItem("item"+i);
            itemPurchaseEvent.setTimestamp(123456L);
            itemPurchaseEvent.setType(CombatLogEntryEntity.Type.ITEM_PURCHASED);
            matchEntity.getCombatLogEntries().add(itemPurchaseEvent);
        }

        for (int i = 0; i < 10; i++) {
            CombatLogEntryEntity spellCastEvent = new CombatLogEntryEntity();
            spellCastEvent.setActor("actor"+(i%3));
            spellCastEvent.setAbility("spell"+(i%3));
            spellCastEvent.setAbilityLevel(i%3);
            spellCastEvent.setTimestamp(123456L);
            spellCastEvent.setType(CombatLogEntryEntity.Type.SPELL_CAST);
            matchEntity.getCombatLogEntries().add(spellCastEvent);
        }

        return matchEntity;

    }

}
