package com.sourceartists.rpg.controller;

import com.sourceartists.rpg.engine.GameEngine;
import com.sourceartists.rpg.exception.DoesNotStandAChanceException;
import com.sourceartists.rpg.exception.HeroIsAChickenExcpetion;
import com.sourceartists.rpg.exception.HeroOvercomesDeathAndCrushesHisEnemy;
import com.sourceartists.rpg.exception.HeroSlainedByDragonException;
import com.sourceartists.rpg.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class GameController {

    public static final Integer MIN_SUPERBUFF_LEVEL = 75;
    public static final Integer MIN_SUPERBUFF_MORALE = 8;

    @Autowired
    private GameEngine gameEngine;

    public void levelUp(Hero hero){
        hero.setLevel(hero.getLevel() + 1);

        if(hero.getLevel() % 10 == 0){
            hero.addSpell(gameEngine.generateSpecialSpell());
        }

        if(hero.getLevel() % 5 == 0){
            hero.addMoney(gameEngine.generateBonusMoney());
        }

        if(hero.getActiveBuff() == null){
            hero.setActiveBuff(gameEngine.generateRandomBuff());
        }
    }

    public void heroDies(Hero hero, Enemy enemy) throws HeroOvercomesDeathAndCrushesHisEnemy {
        Hit hit = gameEngine.hit(enemy, hero);

        while(!hit.isCritical()){
            hit = gameEngine.hit(enemy, hero);
        }

        enemy.performDeadlyFinalBlow();
    }

    public void openLootChest(Hero hero, Chest chest){
        for(Lockpick lockpick: hero.getLockpicks()){
            boolean opened = gameEngine.attemptToOpen(lockpick
                    , hero.getLockpickingLevel(), chest.getPercentageChanceToSpawnGuardian());

            hero.increaseLockpicking();
            chest.increaseChanceToSpawnGuardian();

            if(opened){
                hero.addMoney(chest.getMoney());
                return;
            }
        }
    }

    public void gainBuff(Hero hero, BuffType buffType){
        if(hero.getActiveBuff() != null){
            return;
        }

        if(gameEngine.giveSuperBuff(hero.getLevel(), hero.getMoraleLevel())){
            hero.setActiveBuff(new SuperBuff("super duper buff", buffType));

            return;
        }

        hero.setActiveBuff(new Buff("normal duper buff", buffType));
    }

    public void fightTheBoss(Hero hero, Boss boss){
        gameEngine.castSpell(hero.mostPowerfullOffensiveSpell(), boss);

        while(boss.isAlive()){
            boolean criticalHit = gameEngine.determineCritical(hero.getEquippedWeapon(), hero);

            AttackOutcome attackOutcome = gameEngine.attack(
                    hero.getEquippedWeapon(), boss, criticalHit);

            if(attackOutcome.isDeadly()){
                boss.setAlive(false);
            }
        }
    }

    public void breakIntoCastleAndSteal(Hero hero, Castle castle){
        boolean mainDoorOpened = false;

        for(Lockpick lockpick: hero.getLockpicks()){
            boolean opened = gameEngine.attemptToOpenDoor(lockpick
                    , hero.getLockpickingLevel());

            if(opened){
                mainDoorOpened = true;
                break;
            }
        }

        if(!mainDoorOpened){
            return;
        }

        BigDecimal jeweleryWorth = gameEngine.stealFromJeweleryBox(castle, hero);
        hero.addMoney(jeweleryWorth);
    }

    public boolean fightWithMightyDragon(Hero hero, MightyDragon mightyDragon) throws DoesNotStandAChanceException, HeroSlainedByDragonException {
        BuffType buffType = hero.getActiveBuff().getBuffType();
        boolean dragonSlained = false;

        if(buffType.equals(BuffType.INCREASED_AGILITY)
            || buffType.equals(BuffType.INCREASED_STRENTH)
                || buffType.equals(BuffType.INCREASED_SPEED)){
            dragonSlained = gameEngine.fightWithDragon(hero, mightyDragon);
        }else{
            throw new DoesNotStandAChanceException();
        }

        return dragonSlained;
    }

    public boolean stealGoldFromDragon(Hero hero, Dragon dragon, Integer amountToSteal)
            throws HeroSlainedByDragonException {
        if(amountToSteal == null || amountToSteal <= 0){
            throw new RuntimeException("Do not chicken right now!");
        }

        if(amountToSteal > 2000){
            throw new RuntimeException("You cannot try to steal more than 2000 gold at a time.");
        }

        if(amountToSteal >= Integer.valueOf(1000)){
            boolean dragonSlained = gameEngine.fightWithDragon(hero, dragon);

            if(!dragonSlained){
                throw new HeroSlainedByDragonException();
            }
        }

        return gameEngine.stealGold(hero, dragon);
    }

    public boolean stealTreasureFromDragon(Hero hero, Dragon dragon, List<Treasure> treasures)
            throws HeroIsAChickenExcpetion {
        if(CollectionUtils.isEmpty(treasures)){
            throw new HeroIsAChickenExcpetion();
        }

        return gameEngine.stealTreasures(hero, dragon, treasures);
    }

    public BigDecimal countLoot(List<Treasure> treasures, Hero hero){
        BigDecimal lootWorth = hero.getGold();

        return treasures.stream()
                .map(treasure -> treasure.getTreasureType().getWorth())
                .reduce(lootWorth, (totalWorth, treasureWorth) -> totalWorth.add(treasureWorth));
    }


}
