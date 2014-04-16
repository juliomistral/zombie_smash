package com.getlocket.zombiesmash.domain;


import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class Game {
    private static final Integer MOVE_DURATION = 1000;  // Should be pulled from some configuration
    private static final Integer CELLS_PER_MOVE = 1;
    private static final Integer SMASHER_RECHARGE_DURATION = 750;

    private Collection<Zombie> zombies;

    private Coordinate playerLocation;
    private Integer zombiesKilled;

    private Long currentTime;
    private Integer timeTillZapperIsReady;

    public Game() {
        this.zombies = new LinkedHashSet<Zombie>();
        this.playerLocation = new Coordinate(0,0);
        this.currentTime = 0L;
        this.timeTillZapperIsReady = 0;
        this.zombiesKilled = 0;
    }

    public Coordinate getPlayerLocation() {
        return playerLocation;
    }

    public Long getCurrentTime() {
        return currentTime;
    }

    public Integer getZombiesKilled() {
        return zombiesKilled;
    }

    public boolean isGameInProgress() {
        return !zombies.isEmpty();
    }

    public void registerZombie(Integer x, Integer y, Long appearanceTime) {
        Zombie newZombie = new Zombie(x, y, appearanceTime, this);
        this.zombies.add(newZombie);
    }

    /**
     * One thing to consider is order of operations.  You can have the case where a player moves to a spot the moment
     * the zombie is suppose to disappear.  So depending on the order of operations, you can have the case where the
     * player zaps the zombie (record move, check if zombie is there, smash zombie, increment clock) or misses
     * them (record move, increment clock, hide zombie, player get major sad for missing zombie :( ).
     */
    public void executeTurn(Direction direction) {
        incrementGameClocks();
        movePlayerToNewSpot(direction);

        Iterator<Zombie> safeToRemoveFromCollection = this.zombies.iterator();
        while (safeToRemoveFromCollection.hasNext()) {
            Zombie currentZombie = safeToRemoveFromCollection.next();

            notifyZombieOfCurrentTime(currentZombie);
            if (canPlayerSmashZombie(currentZombie)) {
                smashZombie(currentZombie);
            }
        }
    }

    private void incrementGameClocks() {
        this.currentTime += MOVE_DURATION;
        this.timeTillZapperIsReady -= MOVE_DURATION;

        if (timeTillZapperIsReady < 0) {
            timeTillZapperIsReady = 0;
        }
    }

    /**
     * Assuming the board is a cartesian coordinate system where the player starts at the origin and plays the
     * game in Quadrant I (+, +).  So if the player move results in them moving into another quadrant,
     * we treat it as the player trying to move off the board.  If the player tries to move off the board,
     * we reset their position back to the previous spot.
     */
    private void movePlayerToNewSpot(Direction direction) {
        // Player chooses to simply stay still on their turn
        if (direction == null) {
            return;
        }
        int xDelta = direction.getXDeltaScale() * CELLS_PER_MOVE;
        int yDelta = direction.getYDeltaScale() * CELLS_PER_MOVE;

        this.playerLocation.updateWithDeltas(xDelta, yDelta);
        if (didPlayerMoveOffTheBoard()) {
            this.playerLocation.undoLastUpdate();
        }
    }

    private boolean didPlayerMoveOffTheBoard() {
        return (playerLocation.getX() < 0 || playerLocation.getY() < 0);
    }

    private void notifyZombieOfCurrentTime(Zombie zombie) {
        zombie.calculateStatus(this.currentTime);
        if (zombie.getStatus() == VisibilityStatus.GONE) {
            this.zombies.remove(zombie);
        }
    }

    private boolean canPlayerSmashZombie(Zombie zombie) {
        return (zombie.getLocation().equals(playerLocation) && timeTillZapperIsReady == 0);
    }

    private void smashZombie(Zombie zombie) {
        zombie.smash();
        this.timeTillZapperIsReady = SMASHER_RECHARGE_DURATION;
    }
}
