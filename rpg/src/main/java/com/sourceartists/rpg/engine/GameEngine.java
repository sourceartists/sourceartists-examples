package com.sourceartists.rpg.engine;

import com.sourceartists.rpg.exception.HeroSlainedByDragonException;
import com.sourceartists.rpg.model.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class GameEngine {
    public Spell generateSpecialSpell() {

        return null;
    }

    public BigDecimal generateBonusMoney() {
        return null;
    }

    public Buff generateRandomBuff() {
        return null;
    }

    public void castSpell(Spell mostPowerfullOffensiveSpell, Enemy enemy) {

    }

    public AttackOutcome attack(Weapon weapon, Enemy enemy, boolean fatalityHit) {
        return null;
    }

    public boolean determineCritical(Weapon equippedWeapon, Hero hero) {
        return false;
    }

    public boolean attemptToOpen(Lockpick lockpick, Integer hero, Integer chest) {
        return false;
    }

    public boolean giveSuperBuff(Integer level, Integer moraleLevel) {
        return false;
    }

    public boolean attemptToOpenDoor(Lockpick lockpick, Integer lockpickingLevel) {
        return false;
    }

    public BigDecimal stealFromJeweleryBox(Castle castle, Hero hero) {
        return null;
    }

    public void attackTheCastle(Hero hero, Castle castle) {

    }



    public Hit hit(Enemy enemy, Hero hero) {
        return null;
    }

    public boolean fightWithDragon(Hero hero, Dragon dragon) throws HeroSlainedByDragonException{
        return false;
    }

    public boolean stealGold(Hero hero, Dragon dragon) {
        return false;
    }

    public boolean stealTreasures(Hero hero, Dragon dragon, List<Treasure> treasures) {
        return false;
    }

    public void fightDragonGuards(Hero hero, Dragon dragon) throws HeroSlainedByDragonException {

    }
}
