/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.r.dddtoolkit.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 *
 * @author rascioni
 */
public class Reflections {
	public static Predicate<Method> havingParams(final Class<?>...classes) {
        return input -> Arrays.equals(input.getParameterTypes(), classes);
    }

    public static Predicate<? super Method> methodNamed(final String name) {
        return input -> input.getName().equals(name);
    }

    public static List<Method> methodsOf(Class<?> c) {
        return Arrays.asList(c.getMethods());
    }

    public static Class<?> getGenericOfParent(Class<?> c, int idx) {
        return (Class<?>) ((ParameterizedType) c.getGenericSuperclass()).getActualTypeArguments()[idx];
    }

}
