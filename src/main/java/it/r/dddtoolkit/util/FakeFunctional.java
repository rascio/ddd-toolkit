package it.r.dddtoolkit.util;

import lombok.Value;

import com.google.common.base.Function;

public class FakeFunctional {

	public static <R, T> R reduce(Iterable<T> iterable, R initialValue, Function<Tuple<R, T>, R> fn) {
		
		R value = initialValue;
		for (T t : iterable) {
			value = fn.apply(new Tuple<R, T>(value, t));
		}
		return value;
	}
	
	@Value
	public static class Tuple<A, B> {
		A a;
		B b;
	}
}
