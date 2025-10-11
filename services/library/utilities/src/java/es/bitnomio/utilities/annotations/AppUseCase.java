package es.bitnomio.utilities.annotations;

import jakarta.inject.Singleton;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Singleton
@Documented
@Retention(RUNTIME)
@Target(ElementType.TYPE)
public @interface AppUseCase {

  String info() default "App use case";

}
