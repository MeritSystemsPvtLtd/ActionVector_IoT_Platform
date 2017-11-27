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
public class GarbageCollectionMX extends MX {

    /**
     *
     */
    private static final long serialVersionUID = 3128625409287655522L;

    public GarbageCollectionMX() {
        super(GARBAGECOLLECTORMXBEAN, ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE, MetricsEnum.GARBAGECOLLECTOR_MXBEAN);
    }

    @Override
    public List<String> getSubmetricsConfiguration() {
        return ConfigurationReader.getConfiguration().getGarbageSubmetrics();
    }
}
