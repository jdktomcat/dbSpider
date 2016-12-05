package com.github.bpazy;

import com.github.bpazy.main.App;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Ziyuan.
 * 2016/12/5 11:53
 */
public class Main {
    public static void main(String[] args) {
        Logger.getLogger("org.hibernate").setLevel(Level.OFF);
        App app = new App();
        app.start();
    }
}
