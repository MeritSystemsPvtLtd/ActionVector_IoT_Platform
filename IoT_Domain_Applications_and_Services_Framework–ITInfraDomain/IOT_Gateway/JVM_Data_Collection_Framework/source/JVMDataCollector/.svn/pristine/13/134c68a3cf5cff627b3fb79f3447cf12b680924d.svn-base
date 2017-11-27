package com.meritsystems.core;

import java.util.Iterator;

import org.apache.commons.lang.enums.Enum;

public class MetricsEnum extends Enum {

    private static final long serialVersionUID = -403971874091427445L;

    protected MetricsEnum(String name) {
        super(name);
    }
    /**
     * ClassLoadingMXBean
     */
    public static final MetricsEnum CLASSLOADING_MXBEAN = new MetricsEnum("CLAS");
    /**
     * CompilationMXBean
     */
    public static final MetricsEnum COMPILATION_MXBEAN = new MetricsEnum("COMP");
    /**
     * GarbageCollectorMXBean
     */
    public static final MetricsEnum GARBAGECOLLECTOR_MXBEAN = new MetricsEnum("GARB");
    /**
     * MemoryManagerMXBean
     */
    public static final MetricsEnum MEMORYMANAGER_MXBEAN = new MetricsEnum("MEMM");
    /**
     * MemoryMXBean
     */
    public static final MetricsEnum MEMORY_MXBEAN = new MetricsEnum("MEMO");
    /**
     * MemoryPoolMXBean
     */
    public static final MetricsEnum MEMORYPOOL_MXBEAN = new MetricsEnum("MEMP");
    /**
     * OperatingSystemMXBean
     */
    public static final MetricsEnum OPERATINGSYSTEM_MXBEAN = new MetricsEnum("OS");
    /**
     * RuntimeMXBean
     */
    public static final MetricsEnum RUNTIME_MXBEAN = new MetricsEnum("RUN");
    /**
     * ThreadMXBean
     */
    public static final MetricsEnum THREAD_MXBEAN = new MetricsEnum("THR");
    /**
     * AppMXBean
     */
    public static final MetricsEnum APP_MXBEAN = new MetricsEnum("APP");

    /**
     * @param code
     * @return
     */
    public static MetricsEnum getEnum(String name) {
        MetricsEnum metricsEnum = null;
        for (final Iterator<MetricsEnum> iterate = MetricsEnum.iterator(); iterate.hasNext();) {
            metricsEnum = (MetricsEnum) iterate.next();
            if (metricsEnum.getName().equals(name)) {
                break;
            }
        }
        return metricsEnum;
    }

    public static Iterator iterator() {
        return iterator(MetricsEnum.class);
    }
}