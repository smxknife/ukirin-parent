package org.ukirin.job.core.instance;

import java.util.Collection;
import java.util.List;

/**
 * @author smxknife
 * 2020/11/13
 */
public interface InstanceSelectedStrategy {
	/**
	 * 实例选择
	 * @param instanceStatuses
	 * @return
	 */
	List<Instance> select(Collection<InstanceStatus> instanceStatuses);
}
