package com.github.bpazy.client;

import com.github.bpazy.model.Book;
import com.github.bpazy.utils.Helper;
import com.github.bpazy.utils.QueueAndRedis;
import com.github.bpazy.utils.SqlFactory;
import com.github.kevinsawicki.http.HttpRequest;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by Ziyuan.
 * 2016/12/5 11:33
 */
public class SpiderClient {

    private String target;
    private QueueAndRedis queueAndRedis;
    private SessionFactory factory = SqlFactory.getSessionFactory();

    public SpiderClient(String target, QueueAndRedis queueAndRedis) {
        this.target = target;
        this.queueAndRedis = queueAndRedis;
    }

    public void run() {
        String body = HttpRequest
                .get(target)
                .header("Cookie", "bid=" + Helper.getRandomString(11))
                .readTimeout(1000 * 10)
                .connectTimeout(1000 * 10)
                .body();
        Document doc = Jsoup.parse(body);
        saveBook(doc, target);
        Elements hrefElements = doc.select("div#db-rec-section div dl dt a");
        hrefElements.forEach(e -> {
            try {
                String href = e.attr("href");
                queueAndRedis.queuePut(href);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        });
    }

    private void saveBook(Document doc, String target) {
        String title = doc.select("h1 span").text();
        String ratingNum = doc.select(".ll.rating_num").text();
        String ratingPeople = doc.select(".rating_people span").text();
        Book book = new Book();
        book.setTitle(title);
        book.setRatingNum(ratingNum);
        book.setRatingPeople(ratingPeople);
        System.out.println(book);
        if (isBookEmpty(book)) {
            try {
                queueAndRedis.queuePut(target);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            Session session = factory.openSession();
            session.beginTransaction();
            session.save(book);
            session.getTransaction().commit();
        }
    }

    private boolean isBookEmpty(Book book) {
        return book.getTitle().equals("");
    }
}
