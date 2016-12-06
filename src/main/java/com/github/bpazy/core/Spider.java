package com.github.bpazy.core;

import com.github.bpazy.utils.QueueAndRedis;
import com.github.bpazy.utils.SqlFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Ziyuan.
 * 2016/12/5 13:31
 */
public abstract class Spider<T> {
    private static final int MAX_THREAD_NUM = 50;
    private ExecutorService service = Executors.newFixedThreadPool(MAX_THREAD_NUM);
    private QueueAndRedis queueAndRedis = new QueueAndRedis();
    private String[] origin;

    protected Spider(String... origin) {
        this.origin = origin;
    }

    protected abstract SpiderCore<T> spiderCore(String url, QueueAndRedis queueAndRedis);

    public void start() {
        try {
            for (String u : origin) {
                queueAndRedis.queuePut(u);
            }
            while (true) {
                try {
                    String url = queueAndRedis.queueTake();
                    service.execute(() -> {
                        SpiderCore client = spiderCore(url, queueAndRedis);
                        client.run();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            SqlFactory.getSessionFactory().close();
        }
    }
}
