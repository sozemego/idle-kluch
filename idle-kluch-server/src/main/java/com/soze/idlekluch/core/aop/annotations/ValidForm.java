package com.soze.idlekluch.core.aop.annotations;

import com.soze.idlekluch.core.aop.ValidatingAspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks method parameters as a target for {@link ValidatingAspect}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ValidForm {

}
