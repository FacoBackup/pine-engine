package com.pine.repository;

import com.pine.injection.PBean;
import com.pine.tasks.SyncTask;

@PBean
public class ClockRepository implements SyncTask {
    public final long startupTime = System.currentTimeMillis();
    public long since = 0;
    public long elapsedTime = 0;
    public long totalTime = 0;

    @Override
    public void sync() {
        long newSince = System.currentTimeMillis();
        totalTime = newSince - startupTime;
        elapsedTime = newSince - since;
        since = newSince;
    }
}
