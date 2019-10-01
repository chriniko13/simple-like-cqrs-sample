package com.chriniko.likecqrs.sample.infra.logging;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.io.Serializable;
import java.util.function.Consumer;
import java.util.logging.Logger;

@ApplicationScoped
public class LoggerProducer implements Serializable {

    public interface PassivateConsumer<T> extends Consumer<T>, Serializable {
    }

    @Produces
    @InfoLevel
    @Dependent // Note: not needed, by default it is used.
    public PassivateConsumer<String> exposeInfoLogger(InjectionPoint ip) {
        String clazzName = getClassName(ip);
        return message -> Logger.getLogger(clazzName).info(message);
    }

    @Produces
    @WarnLevel
    @Dependent
    public PassivateConsumer<String> exposeWarnLogger(InjectionPoint ip) {
        String clazzName = getClassName(ip);
        return message -> Logger.getLogger(clazzName).warning(message);
    }

    private String getClassName(InjectionPoint ip) {
        return ip.getMember().getDeclaringClass().getName();
    }

}
