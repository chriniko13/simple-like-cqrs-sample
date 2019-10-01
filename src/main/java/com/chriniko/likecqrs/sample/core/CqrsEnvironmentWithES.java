package com.chriniko.likecqrs.sample.core;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

@Dependent
@Materialization(MaterializationType.ELASTIC_SEARCH)
public class CqrsEnvironmentWithES extends CqrsEnvironment {

    private final WriteSide writeSide;
    private final ReadSide readSide;

    @Inject
    public CqrsEnvironmentWithES(WriteSide writeSide,
                                 @Named("elastic-search") ReadSide readSide) {
        this.writeSide = writeSide;
        this.readSide = readSide;
    }

    @Override
    public WriteSide writeSide() {
        return writeSide;
    }

    @Override
    public ReadSide readSide() {
        return readSide;
    }
}
