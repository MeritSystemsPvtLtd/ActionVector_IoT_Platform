package com.meritsystems.monitor.mx;

import java.lang.management.ManagementFactory;
import java.util.List;

import com.meritsystems.core.MetricsEnum;
import com.meritsystems.core.configuration.ConfigurationReader;
import com.meritsystems.monitor.MX;

/**
 * @author shashi
 *
 */
public class ClassloadingMX extends MX {

    /**
     *
     */
    private static final long serialVersionUID = 5755092469219634807L;

    public ClassloadingMX() {
        super(CLASSLOADINGMXBEAN, ManagementFactory.CLASS_LOADING_MXBEAN_NAME, MetricsEnum.CLASSLOADING_MXBEAN);
    }

    @Override
    public List<String> getSubmetricsConfiguration() {
        return ConfigurationReader.getConfiguration().getClassloadingSubmetrics();
    }
}
