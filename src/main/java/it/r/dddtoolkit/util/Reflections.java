/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.r.dddtoolkit.util;

import com.google.common.base.Predicate;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author rascioni
 */
@Slf4j
public class Reflections {
	public static Predicate<Method> havingParams(final Class<?>...classes) {
        return new Predicate<Method>() {
            @Override
            public boolean apply(Method input) {
				System.out.println(String.format("Arrays.equals(%s, %s); //%s", Arrays.toString(input.getParameterTypes()), Arrays.toString(classes), Arrays.equals(input.getParameterTypes(), classes)));
                return Arrays.equals(input.getParameterTypes(), classes);
            }
        };
    }

    public static Predicate<? super Method> methodNamed(final String name) {
        return new Predicate<Method>() {
            @Override
            public boolean apply(Method input) {
				System.out.println(String.format("\"%s\".equals(%s);", input.getName(), name));
                return input.getName().equals(name);
            }
        };
    }

    public static List<Method> methodsOf(Class<?> c) {
        return Arrays.asList(c.getMethods());
    }
}
