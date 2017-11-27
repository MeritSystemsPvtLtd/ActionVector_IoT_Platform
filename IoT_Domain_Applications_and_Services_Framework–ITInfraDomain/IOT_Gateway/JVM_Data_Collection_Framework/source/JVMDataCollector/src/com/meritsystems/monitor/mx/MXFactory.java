package com.meritsystems.monitor.mx;

import com.meritsystems.core.ApplicationException;
import com.meritsystems.core.MetricsEnum;
import com.meritsystems.core.log.Logger;
import com.meritsystems.core.log.LoggerFactory;
import com.meritsystems.monitor.MX;

public class MXFactory {

    private static Logger logger = LoggerFactory.getLogger(MXFactory.class);

    /**
     * @param metricName
     * @return
     */
    public static MX getMX(MetricsEnum metricName) throws ApplicationException {
        try {
            return getInstance(MX.metricsClass.get(metricName).getJavaagentMX());
        } catch (Exception e) {
            logger.severe(e, "getMX", "Error while loading " + metricName);
            throw new ApplicationException(e);
        }
    }

    public static MX getInstance(Class classToInstance) throws Exception {
        MX instance = (MX) classToInstance.newInstance();
        return instance;
    }
}
