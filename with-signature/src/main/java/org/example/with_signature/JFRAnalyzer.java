package org.example.with_signature;

import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedFrame;
import jdk.jfr.consumer.RecordingFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class JFRAnalyzer {
    private static final String METHOD_CONCAT = "::";
    private static final String NATIVE = "Native";
    private static final String ALL = "all";

    private final String fileName;

    public JFRAnalyzer(String fileName) {
        this.fileName = fileName;
    }

    public Map<JFREventType, JFRTreeNode> parseRecords() {
        try (RecordingFile recordingFile = new RecordingFile(Path.of(fileName))) {
            Map<JFREventType, JFRTreeNode> map = new HashMap<>();
            JFRTreeNode cpuAll = new JFRTreeNode(ALL, 0, 0, new HashSet<>());
            JFRTreeNode inTLABAll = new JFRTreeNode(ALL, 0, 0, new HashSet<>());
            JFRTreeNode outTLABAll = new JFRTreeNode(ALL, 0, 0, new HashSet<>());

            while (recordingFile.hasMoreEvents()) {
                RecordedEvent event = recordingFile.readEvent();
                if (JFREventType.EXECUTION_SAMPLE.getName().equals(event.getEventType().getName())) {
                    parseExecutionSample(cpuAll, event);
                } else if (JFREventType.OBJECT_ALLOCATION_IN_NEW_TLAB.getName().equals(event.getEventType().getName())) {
                    parseObjectAllocationInNewTLAB(inTLABAll, event);
                } else if (JFREventType.OBJECT_ALLOCATION_OUTSIDE_TLAB.getName().equals(event.getEventType().getName())) {
                    parseObjectAllocationOutSideTLAB(outTLABAll, event);
                }
            }
            map.put(JFREventType.EXECUTION_SAMPLE, cpuAll);
            map.put(JFREventType.OBJECT_ALLOCATION_IN_NEW_TLAB, inTLABAll);
            map.put(JFREventType.OBJECT_ALLOCATION_OUTSIDE_TLAB, inTLABAll);
            return map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void parseExecutionSample(JFRTreeNode all, RecordedEvent event) {
        List<RecordedFrame> frames = event.getStackTrace().getFrames();
        List<JFRTreeNode> nodes = new ArrayList<>();
        for (RecordedFrame frame : frames) {
            String frameName = getFrameName(frame);
            JFRTreeNode node = new JFRTreeNode(frameName, 1, 1, new HashSet<>());
            nodes.add(node);
        }
        List<JFRTreeNode> reversedNodes = nodes.reversed();
        all.merge(reversedNodes);
    }

    public void parseObjectAllocationOutSideTLAB(JFRTreeNode all, RecordedEvent event) {
        // 单位KB
        List<RecordedFrame> frames = event.getStackTrace().getFrames();
        Long allocationSize = event.getValue("allocationSize");
        List<JFRTreeNode> nodes = new ArrayList<>();
        for (RecordedFrame frame : frames) {
            String frameName = getFrameName(frame);
            JFRTreeNode node = new JFRTreeNode(frameName, 1, allocationSize, new HashSet<>());
            nodes.add(node);
        }
        List<JFRTreeNode> reversedNodes = nodes.reversed();
        all.merge(reversedNodes);
    }

    public void parseObjectAllocationInNewTLAB(JFRTreeNode all, RecordedEvent event) {
        // 单位KB
        List<RecordedFrame> frames = event.getStackTrace().getFrames();
        Long allocationSize = event.getValue("allocationSize");
        List<JFRTreeNode> nodes = new ArrayList<>();
        for (RecordedFrame frame : frames) {
            String frameName = getFrameName(frame);
            JFRTreeNode node = new JFRTreeNode(frameName, 1, allocationSize, new HashSet<>());
            nodes.add(node);
        }
        List<JFRTreeNode> reversedNodes = nodes.reversed();
        all.merge(reversedNodes);
    }

    private String getFrameName(RecordedFrame frame) {
        if (NATIVE.equals(frame.getType())) {
            return frame.getMethod().getName();
        }
        return frame.getMethod().getType().getName() + METHOD_CONCAT + frame.getMethod().getName();
    }
}
