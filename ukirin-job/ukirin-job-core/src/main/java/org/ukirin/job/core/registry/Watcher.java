package org.ukirin.job.core.registry;

/**
 * @author smxknife
 * 2020/11/12
 */
@FunctionalInterface
public interface Watcher<E> {
	/**
	 * 监听
	 * @param e
	 */
	void watch(E e);
}
