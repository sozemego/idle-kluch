package com.soze.idlekluch.aop;

import com.soze.idlekluch.aop.annotations.Profiled;
import com.soze.idlekluch.aop.annotations.ValidForm;
import com.soze.idlekluch.dto.Form;
import com.soze.idlekluch.exception.InvalidFormException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.lang.annotation.Annotation;
import java.util.*;

@Aspect
@Component
public class ValidatingAspect {

  private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

  @Pointcut("execution(* *(.., @com.soze.idlekluch.aop.annotations.ValidForm (*), ..))")
  public void validFormAsArgumentExecution() { }

  @Before("validFormAsArgumentExecution()")
  @Profiled
  public void validateForm(final JoinPoint joinPoint) throws Exception {
    final List<Form> forms = getForms(joinPoint);

    forms.forEach(form -> {
      for(final ConstraintViolation<Form> constraintViolation: VALIDATOR.validate(form)) {
        throw new InvalidFormException(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
      }
    });

  }

  /**
   * Returns a list of method arguments that have {@link ValidForm} annotations.
   */
  private List<Form> getForms(final JoinPoint joinPoint) {
    Objects.requireNonNull(joinPoint);
    final List<Form> forms = new ArrayList<>();
    final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    final Object[] arguments = joinPoint.getArgs();
    final Annotation[][] annotations = methodSignature.getMethod().getParameterAnnotations();

    for(int i = 0; i < arguments.length; i++) {
      if(hasAnnotation(annotations[i], ValidForm.class)) {
        forms.add((Form) arguments[i]);
      }
    }

    return forms;
  }

  private boolean hasAnnotation(final Annotation[] annotations, final Class<? extends Annotation> target) {
    Objects.requireNonNull(annotations);
    Objects.requireNonNull(target);
    for(int i = 0; i < annotations.length; i++) {
      if(annotations[i].annotationType().equals(target)) {
        return true;
      }
    }
    return false;
  }

}
