package org.ukirin.job.core.util;

import lombok.Getter;

import java.util.Objects;

/**
 * @author smxknife
 * 2020/11/15
 */
@Getter
public class Tuple2<E1, E2> {
	private E1 e1;
	private E2 e2;

	public Tuple2(E1 e1, E2 e2) {
		this.e1 = e1;
		this.e2 = e2;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;
		return Objects.equals(e1, tuple2.e1) &&
				Objects.equals(e2, tuple2.e2);
	}

	@Override
	public int hashCode() {
		return Objects.hash(e1, e2);
	}
}
