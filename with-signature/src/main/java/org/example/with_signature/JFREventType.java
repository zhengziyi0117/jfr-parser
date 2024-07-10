package org.example.with_signature;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JFREventType {
    UNKNOWN("unknown"),
    EXECUTION_SAMPLE("jdk.ExecutionSample"),
    JAVA_MONITOR_ENTER("jdk.JavaMonitorEnter"),
    THREAD_PARK("jdk.ThreadPark"),
    OBJECT_ALLOCATION_IN_NEW_TLAB("jdk.ObjectAllocationInNewTLAB"),
    OBJECT_ALLOCATION_OUTSIDE_TLAB("jdk.ObjectAllocationOutsideTLAB"),;

    private final String name;
}