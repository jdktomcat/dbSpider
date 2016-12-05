package com.github.bpazy.main;

import com.github.bpazy.utils.QueueAndRedis;
import com.github.bpazy.client.SpiderClient;
import com.github.bpazy.utils.SqlFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Ziyuan.
 * 2016/12/5 13:31
 */
public class App {
    private static final int MAX_THREAD_NUM = 50;
    private ExecutorService service = Executors.newFixedThreadPool(MAX_THREAD_NUM);
    private QueueAndRedis queueAndRedis = new QueueAndRedis();

    public void start() {
        try {
            queueAndRedis.queuePut("https://book.douban.com/subject/26864983/");
            while (true) {
                try {
                    String url = queueAndRedis.queueTake();
                    service.execute(() -> {
                        SpiderClient client = new SpiderClient(url, queueAndRedis);
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
