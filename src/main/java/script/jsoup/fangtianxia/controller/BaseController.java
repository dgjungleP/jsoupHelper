package script.jsoup.fangtianxia.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import script.jsoup.fangtianxia.task.SearchTask;
import script.jsoup.fangtianxia.task.UrlFetchTask;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("script/")
public class BaseController {

	@Autowired
	UrlFetchTask urlFetchTask;
	@Autowired
	SearchTask searchTask;

	@RequestMapping("/test")
	public void startScript() {
		List<String> basePageList = urlFetchTask.getBasePageList("https://www.fang.com/SoufunFamily.html");
		log.info("获取基本路径记录完成 共计有：" + basePageList.size());
		for (String url : basePageList) {
			startTask(url);
		}
	}

	@Async
	void startTask(String url) {
		log.info("开始查询：" + url + " 的子路径");
		List<String> pageList = urlFetchTask.getUrlPageList(url);
		for (String page : pageList) {
			log.info("开始查询：" + page);
			searchTask.doSearch(page);
			log.info("url: " + page + " 查询完成");
		}
	}
}
