package com.magicliang.experiments.Util;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by magicliang on 2016/6/26.
 */
public class CASLock {
    private AtomicBoolean locked = new AtomicBoolean(false);

    public boolean lock() {
        return locked.compareAndSet(false, true);
    }

    public boolean unlock() {
        return locked.compareAndSet(true, false);
    }

}
