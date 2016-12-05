package com.github.bpazy.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Created by Ziyuan.
 * 2016/12/5 16:49
 */
public class SqlFactory {
    private SqlFactory() {

    }
    public static SessionFactory getSessionFactory() {
        return Instance.factory;
    }

    private static class Instance {
        private static Configuration cfg = new Configuration().configure();
        private static SessionFactory factory = cfg.buildSessionFactory();
    }
}
