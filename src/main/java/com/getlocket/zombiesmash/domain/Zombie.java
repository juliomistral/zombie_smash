package com.getlocket.zombiesmash.domain;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Zombie {
    private static final Long ZOMBIE_APPEARANCE_DURATION = 1000L;  // Another var that should be pulled from config
    private VisibilityStatus status;
    private Long nextAppearanceStartTime, nextAppearanceEndTime;
    private Coordinate location;
    private Game myGame;

    private Queue<Long> appearanceSchedule;

    private Boolean hungryForBrains = Boolean.TRUE; // Because zombies are ALWAYS hungry for brains...


    public Zombie(Integer x, Integer y, Long appearanceTime, Game myGame) {
        if (appearanceTime < 0 || appearanceTime >100000000) {
            throw new RuntimeException("Invalid appearance time:  0 <= Mi <= 100000000");
        }
        Collection<Long> schedule = new ArrayList<Long>(1);
        schedule.add(appearanceTime);

        init(x, y, schedule, myGame);
    }

    public Zombie(Integer x, Integer y, Collection<Long> schedule, Game myGame) {
        init(x, y, schedule, myGame);
    }

    private void init(Integer x, Integer y, Collection<Long> schedule, Game myGame) {
        this.status = VisibilityStatus.WAITING;
        this.location = new Coordinate(x, y);
        this.myGame = myGame;

        // Due to time constraints, skipping validation (ie., all numbers in sched are positive) and sorting in
        // ascending order.  Assuming this is done for us
        this.appearanceSchedule = new LinkedBlockingQueue<Long>(schedule);

        calculateNextAppearanceTimeRange();
    }

    public VisibilityStatus getStatus() {
        return status;
    }

    public Coordinate getLocation() {
        return location;
    }

    public void calculateStatus(Long currentTime) {
        if (isCurrentTimeBeforeAppearance(currentTime)) {
            this.status = VisibilityStatus.WAITING;
        }
        else if (isCurrentTimeDuringAppearance(currentTime)) {
            this.status = VisibilityStatus.VISIBLE;
        }
        else if (isCurrentTimeAfterAppearance(currentTime)) {
            if (calculateNextAppearanceTimeRange()) {
                this.status = VisibilityStatus.WAITING;
            } else {
                this.status = VisibilityStatus.GONE;
            }
        }
    }

    private boolean isCurrentTimeAfterAppearance(Long currentTime) {
        return this.nextAppearanceStartTime < currentTime && this.nextAppearanceEndTime < currentTime;
    }

    private boolean isCurrentTimeDuringAppearance(Long currentTime) {
        return this.nextAppearanceStartTime <= currentTime || currentTime <= this.nextAppearanceEndTime;
    }

    private boolean isCurrentTimeBeforeAppearance(Long currentTime) {
        return currentTime < this.nextAppearanceStartTime;
    }

    private boolean calculateNextAppearanceTimeRange() {
        Long appearTime = this.appearanceSchedule.poll();
        if (appearTime != null) {
            this.nextAppearanceStartTime = appearTime;
            this.nextAppearanceEndTime = this.nextAppearanceStartTime + ZOMBIE_APPEARANCE_DURATION;
            return true;
        }
        return false;
    }
}
