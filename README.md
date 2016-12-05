dbSpider(豆瓣图书爬虫)
================
使用了redis作为缓存，实现分布式爬取数据。

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