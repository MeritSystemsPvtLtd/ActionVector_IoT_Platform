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
public class CompilationMX extends MX {

    /**
     *
     */
    private static final long serialVersionUID = -198178184563024054L;

    public CompilationMX() {
        super(COMPILATIONMXBEAN, ManagementFactory.COMPILATION_MXBEAN_NAME, MetricsEnum.COMPILATION_MXBEAN);
    }

    @Override
    public List<String> getSubmetricsConfiguration() {
        return ConfigurationReader.getConfiguration().getCompileSubmetrics();
    }
}
