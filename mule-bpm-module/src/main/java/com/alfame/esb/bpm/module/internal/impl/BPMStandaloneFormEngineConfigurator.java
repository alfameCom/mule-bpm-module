package com.alfame.esb.bpm.module.internal.impl;

import org.flowable.form.engine.FormEngine;
import org.flowable.form.engine.configurator.FormEngineConfigurator;

public class BPMStandaloneFormEngineConfigurator extends FormEngineConfigurator {

    private FormEngine formEngine;

    @Override
    protected synchronized FormEngine initFormEngine() {
        this.formEngine = super.initFormEngine();
        return this.formEngine;
    }

    public FormEngine getFormEngine() {
        return this.formEngine;
    }
}
