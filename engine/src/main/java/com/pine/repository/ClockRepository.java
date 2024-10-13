package com.pine.repository;

import com.pine.injection.PBean;
import com.pine.tasks.SyncTask;

@PBean
public class ClockRepository implements SyncTask {
    public final long startupTime = System.currentTimeMillis();
    public long since = 0;
    public float deltaTime = 0;
    public long totalTime = 0;

    @Override
    public void sync() {
        long newSince = System.currentTimeMillis();
        totalTime = newSince - startupTime;
        deltaTime = (newSince - since) / 1000f;
        since = newSince;
    }
}
