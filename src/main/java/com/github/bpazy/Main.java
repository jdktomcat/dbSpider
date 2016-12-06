package com.github.bpazy;

import com.github.bpazy.core.Spider;
import com.github.bpazy.core.SpiderCore;
import com.github.bpazy.example.MovieSpiderCore;
import com.github.bpazy.utils.QueueAndRedis;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Ziyuan.
 * 2016/12/5 11:53
 */
public class Main {
    public static void main(String[] args) {
        Logger.getLogger("org.hibernate").setLevel(Level.OFF);
        Spider spider = new Spider("https://movie.douban.com/subject/26683290/") {
            @Override
            protected SpiderCore spiderCore(String target, QueueAndRedis queue) {
                return new MovieSpiderCore(target, queue);
            }
        };
        spider.start();
    }
}
