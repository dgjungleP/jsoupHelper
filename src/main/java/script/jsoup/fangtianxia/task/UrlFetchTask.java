package script.jsoup.fangtianxia.task;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class UrlFetchTask {
	public static final int INIT_NUMBER = 91;
	public static final String NEW_HOUSE = "newhouse";

	public static final String FIX = "house/s/";

	public List<String> getUrlPageList(String url) {
		List<String> urlPageList = new ArrayList<>();
		try {
			Document document = Jsoup.parse(new URL(url), 3000);
			String totalStr = document.getElementsByClass("page").select("b").text();
			int total = Integer.parseInt(totalStr);
			int loop = total / 20 + 1;
			for (int i = 0; i < loop; i++) {
				urlPageList.add(url + "b" + (INIT_NUMBER + i));
			}
		} catch (Exception ignore) {
			log.error(url + " 查询子路径失败");
		}
		return urlPageList;
	}

	public List<String> getBasePageList(String url) {
		List<String> baselPageList = new ArrayList<>();
		try {
			Document document = Jsoup.parse(new URL(url), 3000);
			Elements elements = document.getElementsByClass("onCont").select("tbody").select("a[href]");
			for (Element element : elements) {
				String baseHref = element.attr("href");
				int i = baseHref.indexOf(".");
				baselPageList.add(baseHref.substring(0, i + 1) + NEW_HOUSE + baseHref.substring(i) + FIX);
			}
		} catch (Exception ignore) {
		}
		return baselPageList;
	}
}
