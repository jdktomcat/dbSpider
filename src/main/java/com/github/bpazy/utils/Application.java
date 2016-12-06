package com.github.bpazy.utils;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Ziyuan.
 * 2016/12/6 22:30
 */
public class Application {
    private static Lock lock = new ReentrantLock();
    private static Condition condition = lock.newCondition();

    public static Lock getLock() {
        return lock;
    }

    public static Condition getCondition() {
        return condition;
    }
}
