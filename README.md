dbSpider(豆瓣爬虫)
================
使用了redis作为缓存，可实现集群爬取数据。

爬取的结果存在mysql中。

### 豆瓣爬虫原理
```
每次请求附带不同的Cookie, Cookie的内容为"bid=[随机的11位长度字符串]"
例如：
HttpRequest
        .get("https://book.douban.com/subject/26864983/")
        .header("Cookie", "bid=aaaaaaaaaaa")
        .body();
```

### 开发教程
```
在main方法中执行：
Spider spider = new Spider("https://movie.douban.com/subject/26683290/") {
    @Override
    protected SpiderCore spiderCore(String target, QueueAndRedis queue) {
        return new MovieSpiderCore(target, queue);
    }
};
spider.start();
```
其中MovieSpiderCore继承自SpiderCore<Movie>，实现其中的关键方法：
```
/**
 * 相关的URL
 *
 * @return 选择器，例如: "div#recommendations div dl dt a" 电影页面的相关电影
 */
protected abstract String relatedUrlSelect();

/**
 * 用于判断model是否合法
 *
 * @param model model
 * @return 合法返回true
 */
protected abstract boolean isValid(T model);

/**
 * 根据Document对象生成model
 *
 * @param doc Document对象
 * @return model对象
 */
protected abstract T getModel(Document doc);
```
