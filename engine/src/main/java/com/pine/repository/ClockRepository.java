package com.pine.repository;

import com.pine.injection.PBean;
import com.pine.tasks.SyncTask;

@PBean
public class ClockRepository implements SyncTask {
    public long since = 0;
    public float deltaTime = 0;
    public long totalTime = 0;

    @Override
    public void sync() {
        totalTime = System.currentTimeMillis();
        deltaTime = (totalTime - since) / 1000f;
        since = totalTime;
    }
}
