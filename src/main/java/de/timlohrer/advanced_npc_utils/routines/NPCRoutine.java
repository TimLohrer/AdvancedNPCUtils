package de.timlohrer.advanced_npc_utils.routines;

import java.util.List;

public class NPCRoutine {
    public List<NPCLocation> locations;
    public NPCRoutineType type;
    private int currentLocationIndex = 0;

    public NPCRoutine(List<NPCLocation> locations, NPCRoutineType type) {
        this.locations = locations;
        this.type = type;
    }

    public NPCLocation getNextLocation() {
        if (type == NPCRoutineType.FIXED && currentLocationIndex < locations.size() - 1) {
            currentLocationIndex++;
        } else if (type == NPCRoutineType.LOOP) {
            currentLocationIndex = (currentLocationIndex + 1) % locations.size();
            return getCurrentLocation();
        }
        return null;
    }

    public NPCLocation getCurrentLocation() {
        return locations.get(currentLocationIndex);
    }
}
