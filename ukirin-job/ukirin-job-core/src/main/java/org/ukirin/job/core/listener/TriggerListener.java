package org.ukirin.job.core.listener;

import org.ukirin.job.core.sharding.ShardingContext;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author smxknife
 * 2020/11/13
 */
public interface TriggerListener {
	/**
	 * 触发器名称
	 * @return
	 */
	String getName();

	/**
	 * 开始触发
	 * @param jobName
	 * @param shardingContext
	 */
	void triggerFired(String jobName, ShardingContext shardingContext);

	/**
	 * 触发完成
	 * @param jobName
	 * @param shardingContext
	 */
	void triggerComplete(String jobName, ShardingContext shardingContext);

	/**
	 * 错过执行
	 * @param jobName
	 * @param context
	 */
	void triggerMisfired(String jobName, ShardingContext context);



	class Loader {
		public static List<TriggerListener> load(ClassLoader classLoader) {
			List<TriggerListener> listeners = new ArrayList<>();
			ServiceLoader.load(TriggerListener.class, classLoader).forEach(listeners::add);
			return listeners;
		}
	}

}
