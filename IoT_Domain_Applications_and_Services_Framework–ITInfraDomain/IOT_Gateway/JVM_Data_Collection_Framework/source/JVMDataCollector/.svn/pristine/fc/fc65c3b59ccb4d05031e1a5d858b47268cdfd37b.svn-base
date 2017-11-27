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
public class MemoryManagerMX extends MX {

    /**
     *
     */
    private static final long serialVersionUID = 2757939230125112644L;

    public MemoryManagerMX() {
        super(MEMORYMANAGERMXBEAN, ManagementFactory.MEMORY_MANAGER_MXBEAN_DOMAIN_TYPE, MetricsEnum.MEMORYMANAGER_MXBEAN);
    }

    @Override
    public List<String> getSubmetricsConfiguration() {
        return ConfigurationReader.getConfiguration().getMemorymanagerSubmetrics();
    }
}
