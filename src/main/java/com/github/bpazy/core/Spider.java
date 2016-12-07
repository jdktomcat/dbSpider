package com.github.bpazy.core;

import com.github.bpazy.utils.Application;
import com.github.bpazy.utils.QueueAndRedis;
import com.github.bpazy.utils.SqlFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created by Ziyuan.
 * 2016/12/5 13:31
 */
public abstract class Spider<T> {
    private static final int MAX_THREAD_NUM = 50;
    private ExecutorService service = Executors.newFixedThreadPool(MAX_THREAD_NUM);
    private QueueAndRedis queueAndRedis = new QueueAndRedis();
    private String[] origin;
    private boolean shouldQuit;

    protected Spider(String... origin) {
        this.origin = origin;
    }

    protected abstract SpiderCore<T> spiderCore(String url, QueueAndRedis queueAndRedis);

    public void start() {
        addShutdownHook();
        try {
            for (String u : origin) {
                queueAndRedis.queuePut(u);
            }
            while (true) {
                try {
                    String url = null;
                    while (url == null) {
                        url = queueAndRedis.queueTake();
                        if (shouldQuit) {
                            break;
                        }
                    }
                    if (shouldShutdown()) break;
                    String finalUrl = url;
                    service.execute(() -> {
                        SpiderCore client = spiderCore(finalUrl, queueAndRedis);
                        client.run();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            Lock lock = Application.getLock();
            lock.lock();
            try {
                while (!service.isTerminated()) {
                    Application.getCondition().await();
                }
                SqlFactory.getSessionFactory().close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    private boolean shouldShutdown() {
        if (shouldQuit) {
            if (!service.isShutdown()) {
                service.shutdown();
            }
            return true;
        }
        return false;
    }

    private void addShutdownHook() {
        Runtime
                .getRuntime()
                .addShutdownHook(new Thread(() -> {
                    Lock lock = Application.getLock();
                    Condition condition = Application.getCondition();
                    lock.lock();
                    try {
                        shouldQuit = true;
                        while (!service.isTerminated()) {
                            condition.await();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                }));
    }
}
