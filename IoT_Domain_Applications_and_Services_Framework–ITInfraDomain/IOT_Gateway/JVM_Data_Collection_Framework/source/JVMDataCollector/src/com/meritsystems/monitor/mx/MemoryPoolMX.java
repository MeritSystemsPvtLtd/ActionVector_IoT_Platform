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
public class MemoryPoolMX extends MX {

    private static final long serialVersionUID = -8751856062313644059L;

    public MemoryPoolMX() {
        super(MEMORYPOOLMXBEAN, ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE, MetricsEnum.MEMORYPOOL_MXBEAN);
    }

    @Override
    public List<String> getSubmetricsConfiguration() {
        return ConfigurationReader.getConfiguration().getMemoryPoolSubmetrics();
    }
}