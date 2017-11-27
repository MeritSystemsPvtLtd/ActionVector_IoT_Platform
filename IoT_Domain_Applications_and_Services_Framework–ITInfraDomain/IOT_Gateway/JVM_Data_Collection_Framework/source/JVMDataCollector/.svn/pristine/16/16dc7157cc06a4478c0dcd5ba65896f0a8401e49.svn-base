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
public class OSMX extends MX {

    private static final long serialVersionUID = -6581271024529756181L;

    public OSMX() {
        super(OPERATINGSYSTEMMXBEAN, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, MetricsEnum.OPERATINGSYSTEM_MXBEAN);
    }

    @Override
    public List<String> getSubmetricsConfiguration() {
        return ConfigurationReader.getConfiguration().getOsSubmetrics();
    }
}