package com.meritsystems.core;

import java.util.Iterator;
import org.apache.commons.lang.enums.Enum;

public class SubMetricsEnum extends Enum {

    private static final long serialVersionUID = -4275089696260113888L;

    protected SubMetricsEnum(String name) {
        super(name);
    }
    /**
     * ClassLoadingMXBean.getLoadedClassCount()
     */
    public static final SubMetricsEnum CLASSLOADING_LOADEDCLASSCOUNT = new SubMetricsEnum("CLAS_LCC");
    /**
     * ClassLoadingMXBean.getTotalLoadedClassCount()
     */
    public static final SubMetricsEnum CLASSLOADING_TOTALLOADEDCLASSCOUNT = new SubMetricsEnum("CLAS_TLCC");
    /**
     * ClassLoadingMXBean.getUnloadedClassCount()
     */
    public static final SubMetricsEnum CLASSLOADING_UNLOADEDCLASSCOUNT = new SubMetricsEnum("CLAS_UNCC");
    /**
     * CompilationMXBean.getName()
     */
    public static final SubMetricsEnum COMPILATION_NAME = new SubMetricsEnum("COMP_NAME");
    /**
     * CompilationMXBean.getTotalCompilationTime()
     */
    public static final SubMetricsEnum COMPILATION_TOTALCOMPILATIONTIME = new SubMetricsEnum("COMP_TCOMT");

    /**
     * GarbageCollectorMXBean
     *//*
     public static final SubMetricsEnum GARBAGECOLLECTOR_MXBEAN = new SubMetricsEnum("GARB");

     */

    /**
     * MemoryManagerMXBean
     *//*
     public static final SubMetricsEnum MEMORYMANAGER_MXBEAN = new SubMetricsEnum("MEMM");

     */

    /**
     * MemoryMXBean
     *//*
     public static final SubMetricsEnum MEMORY_MXBEAN = new SubMetricsEnum("MEMO");

     */

    /**
     * MemoryPoolMXBean
     *//*
     public static final SubMetricsEnum MEMORYPOOL_MXBEAN = new SubMetricsEnum("MEMP");

     */

    /**
     * OperatingSystemMXBean
     *//*
     public static final SubMetricsEnum OPERATINGSYSTEM_MXBEAN = new SubMetricsEnum("OS");

     */

    /**
     * RuntimeMXBean
     *//*
     public static final SubMetricsEnum RUNTIME_MXBEAN = new SubMetricsEnum("RUN");

     */

    /**
     * ThreadMXBean
     *//*
     public static final SubMetricsEnum THREAD_MXBEAN = new SubMetricsEnum("THR");
     */

    /**
     * @param code
     * @return
     */
    public static SubMetricsEnum getEnum(String name) {
        SubMetricsEnum metricsEnum = null;
        for (final Iterator<SubMetricsEnum> iterate = SubMetricsEnum.iterator(); iterate.hasNext();) {
            metricsEnum = (SubMetricsEnum) iterate.next();
            if (metricsEnum.getName().equals(name)) {
                break;
            }
        }
        return metricsEnum;
    }

    public static Iterator iterator() {
        return iterator(SubMetricsEnum.class);
    }
}
