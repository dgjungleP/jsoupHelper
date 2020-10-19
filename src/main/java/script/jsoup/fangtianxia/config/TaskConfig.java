package script.jsoup.fangtianxia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync
public class TaskConfig {

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10); //核心线程数
		executor.setMaxPoolSize(20);  //最大线程数
		executor.setQueueCapacity(1000); //队列大小
		executor.setKeepAliveSeconds(300); //线程最大空闲时间
		executor.setThreadNamePrefix("search-Executor-"); //指定用于新创建的线程名称的前缀。
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		return executor;
	}

}
