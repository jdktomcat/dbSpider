package com.github.bpazy.core;

import com.github.bpazy.utils.QueueAndRedis;
import com.github.bpazy.utils.SqlFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

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
                if (!service.isShutdown()) {
                    service.execute(() -> {
                        SpiderCore client = spiderCore(finalUrl, queueAndRedis);
                        client.run();
                    });
                } else {
                    if (url != null) {
                        queueAndRedis.redisSetRemove(url);
                        queueAndRedis.queuePut(url);
                    }
                }
            } catch (InterruptedException | RejectedExecutionException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void addShutdownHook() {
        Runtime
                .getRuntime()
                .addShutdownHook(new Thread(() -> {
                    shouldQuit = true;
                    service.shutdownNow();
                    while (!service.isTerminated()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    SqlFactory.getSessionFactory().close();
                }));
    }
}
