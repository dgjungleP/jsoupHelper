package script.jsoup.fangtianxia.task;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import script.jsoup.fangtianxia.entity.BaseInfo;
import script.jsoup.fangtianxia.entity.EntityInfo;
import script.jsoup.fangtianxia.entity.FangTianXiaEntity;
import script.jsoup.fangtianxia.mapper.FangTianXiaMapper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SearchTask {
    @Autowired
    private FangTianXiaMapper fangTianXiaMapper;
    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    public static HashMap<String, List<FangTianXiaEntity>> getResultMap(String url) {
        List<FangTianXiaEntity> entityList = new ArrayList<>();
        List<FangTianXiaEntity> errorList = new ArrayList<>();
        while (null != url) {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            url = getResult(url, entityList, errorList);
        }
        HashMap<String, List<FangTianXiaEntity>> hashMap = new HashMap<>();
        entityList.removeIf(data -> null == data.getEntityInfo() || null == data.getEntityInfo().getBaseInfo());
        hashMap.put(SUCCESS, entityList);
        hashMap.put(ERROR, errorList);
        return hashMap;
    }

    /**
     * @param errorList
     * @param retryTime
     */
    public static List<FangTianXiaEntity> fixupError(List<FangTianXiaEntity> errorList, int retryTime) {

        List<FangTianXiaEntity> retryList = new ArrayList<>();
        for (int i = 0; i < retryTime; i++) {
            log.info("第" + (i + 1) + "次重试");
            retryList.addAll(fixupError(errorList));
            if (errorList.isEmpty()) {
                break;
            }
        }
        return retryList;
    }

    public static List<FangTianXiaEntity> fixupError(List<FangTianXiaEntity> errorList) {
        List<FangTianXiaEntity> retrySuccess;
        for (FangTianXiaEntity fangTianXiaEntity : errorList) {
            if (null == fangTianXiaEntity.getEntityInfo()) {
                try {
                    getInfo(fangTianXiaEntity);
                } catch (Exception ignore) {
                }
            } else {
                if (null == fangTianXiaEntity.getEntityInfo().getBaseInfo()) {
                    try {
                        getBaseInfo(fangTianXiaEntity);
                    } catch (Exception ignore) {
                    }
                }
            }
        }
        retrySuccess = errorList.stream().filter(data -> null != data.getEntityInfo() && null != data.getEntityInfo().getBaseInfo()).collect(Collectors.toList());
        errorList.removeIf(data -> null != data.getEntityInfo() && null != data.getEntityInfo().getBaseInfo());
        return retrySuccess;
    }

    private static String getResult(String url, List<FangTianXiaEntity> entityList, List<FangTianXiaEntity> errorList) {
        if (prepareEntity(url, entityList)) return url;
        for (FangTianXiaEntity fangTianXiaEntity : entityList) {
            try {
                getInfo(fangTianXiaEntity);
                getBaseInfo(fangTianXiaEntity);
            } catch (IOException e) {
                errorList.add(fangTianXiaEntity.copy());
            }
        }
        return null;
    }

    private static boolean prepareEntity(String url, List<FangTianXiaEntity> entityList) {
        try {
            Document document = Jsoup.parse(new URL(url), 3000);
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
        } catch (Exception ignore) {
            return true;
        }
        return false;
    }

    private static void getBaseInfo(FangTianXiaEntity entity) throws IOException {
        if (entity.getEntityInfo() != null) {
            Document infoDocument = Jsoup.parse(entity.getEntityInfo().getBaseInfoUrl(), 30000);
            Elements elements = infoDocument.select("div.main-left > div.main-item");
            for (Element element : elements) {
                if (element.text().startsWith("小区规划")) {
                    Elements li = element.getElementsByTag("li").select("div.list-right");
                    entity.getEntityInfo().setBaseInfo(BaseInfo.builder()
                            .greeningRate(li.get(3).text())
                            .plotRatio(li.get(2).text())
                            .totalNumber(li.get(6).text())
                            .build());
                    break;
                }
            }
        }
    }

    private static void getInfo(FangTianXiaEntity fangTianXiaEntity) throws IOException {
        Document infoDocument = Jsoup.parse(fangTianXiaEntity.getUrl(), 30000);
        Elements informations = infoDocument.getElementsByClass("information");
        fangTianXiaEntity.setHouseName(informations.select("div.tit").text().split(" ")[0]);
        Elements elements = informations.select("div.information_li");
        String type = elements.get(2).text().trim().replace(" ", ",").replace("�O", "m2");
        if (type.length() > 10) {
            type = type.substring(6, type.length() - 3);
        }
        fangTianXiaEntity.setEntityInfo(EntityInfo.builder()
                .address(elements.get(3).text().split(" ")[1])
                .openInfo(elements.get(4).text().split(" ")[1])
                .type(type)
                .price(elements.get(1).text().split(" ")[1])
                .build());
        Elements href = infoDocument.getElementById("orginalNaviBox").select("a[href]");
        fangTianXiaEntity.getEntityInfo().setBaseInfoUrl(new URL("http:" + href.get(1).attr("href")));
    }


    @Async
    public void doSearch(String page) {
        HashMap<String, List<FangTianXiaEntity>> resultMap = getResultMap(page);
        List<FangTianXiaEntity> errorList = resultMap.get(ERROR);
        List<FangTianXiaEntity> successList = resultMap.get(SUCCESS);
        log.info("链接：" + page + "成功获取数量：" + successList.size());
        log.info("链接：" + page + "失败数量：" + errorList.size());
        if (!errorList.isEmpty()) {
            log.info("链接：" + page + "开始重试");
            List<FangTianXiaEntity> retryList = fixupError(errorList, 5);
            successList.addAll(retryList);
            log.info("链接：" + page + "重试后成功获取数量：" + successList.size());
            log.info("链接：" + page + "重试后失败数量：" + errorList.size());
        }
        for (FangTianXiaEntity fangTianXiaEntity : successList) {
            fangTianXiaMapper.insert(fangTianXiaEntity.createRepo());
        }
        System.out.println(JSONObject.toJSONString(resultMap));
    }
}
