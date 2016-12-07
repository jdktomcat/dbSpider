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
                if (shouldQuit) {
                    break;
                }
                String url;
                try {
                    url = queueAndRedis.queueTake();
                    String finalUrl = url;
                    checkShutdown(url);
                    service.execute(() -> {
                        SpiderCore client = spiderCore(finalUrl, queueAndRedis);
                        client.run();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        } finally {
            waitingAllSubThreadEnding();
            SqlFactory.getSessionFactory().close();
        }
    }

    private void waitingAllSubThreadEnding() {
        Lock lock = Application.getLock();
        Condition condition = Application.getCondition();
        while (!service.isTerminated()) {
            lock.lock();
            try {
                condition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    private void checkShutdown(String url) {
        if (service.isShutdown()) {
            if (shouldQuit && url != null) {
                queueAndRedis.redisSetRemove(url);
                queueAndRedis.queuePut(url);
            }
        }
    }

    private void addShutdownHook() {
        Runtime
                .getRuntime()
                .addShutdownHook(new Thread(() -> {
                    shouldQuit = true;
                    service.shutdownNow();
                    waitingAllSubThreadEnding();
                }));
    }
}
