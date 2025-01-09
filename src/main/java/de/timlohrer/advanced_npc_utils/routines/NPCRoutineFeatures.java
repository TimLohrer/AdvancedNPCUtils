package de.timlohrer.advanced_npc_utils.routines;

import de.timlohrer.advanced_npc_utils.entity.AdvancedNPC;

import java.time.Duration;
import java.util.function.Function;

public class NPCRoutineFeatures {
    public static class Pause extends NPCRoutineFeatures {
        public final int pauseTicks;

        public Pause(Duration pauseTime) {
            this.pauseTicks = (int) (pauseTime.getSeconds() * 20);
        }
    }

    public static class Custom extends NPCRoutineFeatures {
        public final Function<AdvancedNPC, Void> action;

        public Custom(Function<AdvancedNPC, Void> action) {
            this.action = action;
        }
    }
}
