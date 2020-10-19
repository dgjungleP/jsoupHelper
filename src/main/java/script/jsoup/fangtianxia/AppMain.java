package script.jsoup.fangtianxia;

import com.alibaba.fastjson.JSON;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import script.jsoup.fangtianxia.entity.BaseInfo;
import script.jsoup.fangtianxia.entity.EntityInfo;
import script.jsoup.fangtianxia.entity.FangTianXiaEntity;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @version: v1.0
 * @date: 2020/10/19
 * @author: dgj
 */
public class AppMain {

    public static void main(String[] args) throws IOException {
        List<FangTianXiaEntity> entityList = new ArrayList<>();
        Document document = Jsoup.parse(new URL("https://cd.newhouse.fang.com/house/s/"), 3000);
        Elements nhouseList = document.getElementsByClass("nhouse_list").select("div.nlcd_name");

        for (Element element : nhouseList) {
            Elements hrefList = element.getElementsByTag("a");
            for (Element href : hrefList) {
                if (href.hasAttr("href")) {
                    String hrefStr = href.attr("href");
                    entityList.add(FangTianXiaEntity.builder().url(new URL("https:" + hrefStr)).build());
                }
            }
        }
        for (FangTianXiaEntity fangTianXiaEntity : entityList) {
            try {
                Document infoDocument = Jsoup.parse(fangTianXiaEntity.getUrl(), 3000);
                Elements informations = infoDocument.getElementsByClass("information");
                fangTianXiaEntity.setHouseName(informations.select("div.tit").text().split(" ")[0]);
                Elements elements = informations.select("div.information_li");
                fangTianXiaEntity.setInfo(EntityInfo.builder()
                        .address(elements.get(3).text().split(" ")[1])
                        .openInfo(elements.get(4).text().split(" ")[1])
                        .type(elements.get(2).text())
                        .price(elements.get(1).text().split(" ")[1])
                        .build());
                Elements href = infoDocument.getElementById("orginalNaviBox").select("a[href]");
                fangTianXiaEntity.getInfo().setBaseInfoUrl(new URL("http:" + href.get(1).attr("href")));
            } catch (Exception ignore) {
            }
        }
        for (FangTianXiaEntity entity : entityList) {
            if (entity.getInfo() != null) {
                try {
                    Document infoDocument = Jsoup.parse(entity.getInfo().getBaseInfoUrl(), 3000);
                    Elements elements = infoDocument.select("div.main-left > div.main-item");
                    for (Element element : elements) {
                        if (element.text().startsWith("小区规划")) {
                            Elements li = element.getElementsByTag("li").select("div.list-right");
                            entity.getInfo().setBaseInfo(BaseInfo.builder()
                                    .greeningRate(li.get(3).text())
                                    .plotRatio(li.get(2).text())
                                    .totalNumber(li.get(6).text())
                                    .build());
                            break;
                        }
                    }
                } catch (Exception ignore) {
                }
            }

        }
        entityList.stream().map(JSON::toJSONString).forEach(System.out::println);
    }
}

