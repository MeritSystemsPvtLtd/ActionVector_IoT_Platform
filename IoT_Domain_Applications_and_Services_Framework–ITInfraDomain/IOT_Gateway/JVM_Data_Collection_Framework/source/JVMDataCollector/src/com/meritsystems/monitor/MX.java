package com.meritsystems.monitor;

import java.io.Serializable;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import com.meritsystems.core.MetricsEnum;
import com.meritsystems.core.log.Logger;
import com.meritsystems.core.log.LoggerFactory;
import com.meritsystems.monitor.mx.ClassloadingMX;
import com.meritsystems.monitor.mx.CompilationMX;
import com.meritsystems.monitor.mx.GarbageCollectionMX;
import com.meritsystems.monitor.mx.MemoryMX;
import com.meritsystems.monitor.mx.MemoryManagerMX;
import com.meritsystems.monitor.mx.MemoryPoolMX;
import com.meritsystems.monitor.mx.OSMX;
import com.meritsystems.monitor.mx.RunTimeMX;
import com.meritsystems.monitor.mx.ThreadMX;
import com.sun.management.GarbageCollectorMXBean;
import com.sun.management.OperatingSystemMXBean;

/**
 * @author shashi Holds all MX beans
 */
public abstract class MX implements Serializable {

    public static final String CLASSLOADINGMXBEAN = "ClassLoadingMXBean";
    public static final String COMPILATIONMXBEAN = "CompilationMXBean";
    public static final String GARBAGECOLLECTORMXBEAN = "GarbageCollectorMXBean";
    public static final String MEMORYMANAGERMXBEAN = "MemoryManagerMXBean";
    public static final String MEMORYMXBEAN = "MemoryMXBean";
    public static final String MEMORYPOOLMXBEAN = "MemoryPoolMXBean";
    public static final String OPERATINGSYSTEMMXBEAN = "OperatingSystemMXBean";
    public static final String RUNTIMEMXBEAN = "RuntimeMXBean";
    public static final String THREADMXBEAN = "ThreadMXBean";
    public static final String APPMXBEAN = "APPMXBean";
    public static HashMap<MetricsEnum, MXMapper> metricsClass = null;
    static Logger logger = LoggerFactory.getLogger(MX.class);

    static {
        metricsClass = new HashMap<MetricsEnum, MXMapper>();

        metricsClass.put(MetricsEnum.CLASSLOADING_MXBEAN, new MXMapper(ManagementFactory.CLASS_LOADING_MXBEAN_NAME, ClassloadingMX.class, ClassLoadingMXBean.class));
        metricsClass.put(MetricsEnum.COMPILATION_MXBEAN, new MXMapper(ManagementFactory.COMPILATION_MXBEAN_NAME, CompilationMX.class, CompilationMXBean.class));
        metricsClass.put(MetricsEnum.GARBAGECOLLECTOR_MXBEAN, new MXMapper(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE, GarbageCollectionMX.class, GarbageCollectorMXBean.class));
        metricsClass.put(MetricsEnum.MEMORY_MXBEAN, new MXMapper(ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMX.class, MemoryMXBean.class));
        metricsClass.put(MetricsEnum.MEMORYMANAGER_MXBEAN, new MXMapper(ManagementFactory.MEMORY_MANAGER_MXBEAN_DOMAIN_TYPE, MemoryManagerMX.class, MemoryManagerMXBean.class));
        metricsClass.put(MetricsEnum.MEMORYPOOL_MXBEAN, new MXMapper(ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE, MemoryPoolMX.class, MemoryPoolMXBean.class));
        metricsClass.put(MetricsEnum.OPERATINGSYSTEM_MXBEAN, new MXMapper(ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OSMX.class, OperatingSystemMXBean.class));
        metricsClass.put(MetricsEnum.RUNTIME_MXBEAN, new MXMapper(ManagementFactory.RUNTIME_MXBEAN_NAME, RunTimeMX.class, RuntimeMXBean.class));
        metricsClass.put(MetricsEnum.THREAD_MXBEAN, new MXMapper(ManagementFactory.THREAD_MXBEAN_NAME, ThreadMX.class, ThreadMXBean.class));

    }
    /**
     *
     */
    private static final long serialVersionUID = -2829106585954544527L;
    private Object mxBean;
    private String title;
    private String mxConstant;
    private MetricsEnum metricsenum;
    private List<SubMetrics> submetricsList = null;

    /**
     * @param title
     * @param mxConstant
     * @param metricsenum
     */
    public MX(String title, String mxConstant, MetricsEnum metricsenum) {
        super();
        this.title = title;
        this.mxConstant = mxConstant;
        this.metricsenum = metricsenum;
    }

    /**
     * @return
     */
    public Object getMxBean() {
        return mxBean;
    }

    /**
     * @param mxBean
     */
    public void setMxBean(Object mxBean) {
        this.mxBean = mxBean;
    }

    /**
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return
     */
    public String getMxConstant() {
        return mxConstant;
    }

    /**
     * @return
     */
    public MetricsEnum getMetricsenum() {
        return metricsenum;
    }

    /**
     * @return
     */
    public void process() {

        List<String> configSubmetrics = getSubmetricsConfiguration();
        submetricsList = new ArrayList<SubMetrics>();

        for (String submetricsKey : configSubmetrics) {
            try {
                logger.info("submetrics " + submetricsKey + " " + getMetricsenum().getName());
                SubMetrics submetrics = SubMetrics.metricssubmetricsmapper.get(getMetricsenum()).get(submetricsKey).clone();
                submetrics.setValue(PropertyUtils.getProperty(getMxBean(), submetrics.getBeanproperty()));
                submetricsList.add(submetrics);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                logger.severe(e, "getSubMetrics", e.getMessage());
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                logger.severe(e, "getSubMetrics", e.getMessage());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                logger.severe(e, "getSubMetrics", e.getMessage());
            }
        }
    }

    /**
     * @return
     */
    public List<SubMetrics> getSubmetricsList() {
        return submetricsList;
    }

    public abstract List<String> getSubmetricsConfiguration();
}
