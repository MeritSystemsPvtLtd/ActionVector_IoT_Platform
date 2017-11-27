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
public class ThreadMX extends MX {

    /**
     *
     */
    private static final long serialVersionUID = 2067474961554475762L;

    public ThreadMX() {
        super(THREADMXBEAN, ManagementFactory.THREAD_MXBEAN_NAME, MetricsEnum.THREAD_MXBEAN);
    }

    @Override
    public List<String> getSubmetricsConfiguration() {
        return ConfigurationReader.getConfiguration().getThreadSubmetrics();
    }
}
