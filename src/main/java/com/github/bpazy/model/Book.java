package com.github.bpazy.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by Ziyuan.
 * 2016/12/5 16:43
 */
public class Book {
    private int id;
    private String title;
    private String ratingNum;
    private int ratingPeople;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRatingPeople(int ratingPeople) {
        this.ratingPeople = ratingPeople;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", ratingNum='" + ratingNum + '\'' +
                ", ratingPeople=" + ratingPeople +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRatingNum() {
        return ratingNum;
    }

    public void setRatingNum(String ratingNum) {
        this.ratingNum = ratingNum;
    }

    public int getRatingPeople() {
        return ratingPeople;
    }

    public void setRatingPeople(String ratingPeople) {
        if ("".equals(ratingPeople)) {
            this.ratingPeople = 0;
        } else {
            this.ratingPeople = Integer.valueOf(ratingPeople);
        }
    }
}
