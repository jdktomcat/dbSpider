package com.github.bpazy.example;

import com.github.bpazy.core.SpiderCore;
import com.github.bpazy.model.Movie;
import com.github.bpazy.utils.QueueAndRedis;
import org.jsoup.nodes.Document;

/**
 * Created by Ziyuan.
 * 2016/12/6 16:14
 */
public class BookSpiderCore extends SpiderCore<Movie> {
    public BookSpiderCore(String target, QueueAndRedis queueAndRedis) {
        super(target, queueAndRedis);
    }

    @Override
    protected String relatedUrlSelect() {
        return "div#db-rec-section div dl dt a";
    }

    @Override
    protected boolean isValid(Movie model) {
        return "".equals(model.getTitle());
    }

    @Override
    protected Movie getModel(Document doc) {
        String title = infoSelect(doc, "h1 span");
        String ratingNum = infoSelect(doc, ".ll.rating_num");
        String ratingPeople = infoSelect(doc, ".rating_people span");
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setRatingNum(ratingNum);
        movie.setRatingPeople(ratingPeople);
        return movie;
    }
}
