package com.chriniko.likecqrs.sample.core;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, TYPE, PARAMETER, FIELD})
public @interface Materialization {

    MaterializationType value();
}
