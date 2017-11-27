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
public class RunTimeMX extends MX {

    private static final long serialVersionUID = -6146231865840739839L;

    public RunTimeMX() {
        super(RUNTIMEMXBEAN, ManagementFactory.RUNTIME_MXBEAN_NAME, MetricsEnum.RUNTIME_MXBEAN);
    }

    @Override
    public List<String> getSubmetricsConfiguration() {
        return ConfigurationReader.getConfiguration().getRuntimeSubmetrics();
    }
}
