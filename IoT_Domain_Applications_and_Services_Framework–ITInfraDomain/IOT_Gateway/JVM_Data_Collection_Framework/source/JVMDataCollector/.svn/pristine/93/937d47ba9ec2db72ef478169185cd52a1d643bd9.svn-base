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
public class MemoryMX extends MX {

    /**
     *
     */
    private static final long serialVersionUID = -3857569366561523492L;

    public MemoryMX() {
        super(MEMORYMXBEAN, ManagementFactory.MEMORY_MXBEAN_NAME, MetricsEnum.MEMORY_MXBEAN);
    }

    @Override
    public List<String> getSubmetricsConfiguration() {
        return ConfigurationReader.getConfiguration().getMemorySubmetrics();
    }
}
