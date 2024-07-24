package org.example;

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

    private final String fileName;

    private final Map<String, Integer> signature2index = new HashMap<>();

    private int incrId;

    public JFRAnalyzer(String fileName) {
        this.fileName = fileName;
    }

    public Map<JFREventType, JFRTreeNode> parseRecords() {
        try (RecordingFile recordingFile = new RecordingFile(Path.of(fileName))) {
            Map<JFREventType, JFRTreeNode> map = new HashMap<>();
            JFRTreeNode cpuAll = new JFRTreeNode(-1, 0, 0, new HashMap<>(2));
            JFRTreeNode inTLABAll = new JFRTreeNode(-1, 0, 0, new HashMap<>(2));
            JFRTreeNode outTLABAll = new JFRTreeNode(-1, 0, 0, new HashMap<>(2));
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
            int frameId = getId(frameName);
            JFRTreeNode node = new JFRTreeNode(frameId, 1, 1, new HashMap<>(2));
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
            int frameId = getId(frameName);
            JFRTreeNode node = new JFRTreeNode(frameId, 1, allocationSize, new HashMap<>(2));
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
            int frameId = getId(frameName);
            JFRTreeNode node = new JFRTreeNode(frameId, 1, allocationSize, new HashMap<>(2));
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

    private int getId(String frameName) {
        Integer id = signature2index.get(frameName);
        if (id == null) {
            id = incrId++;
            signature2index.put(frameName, id);
        }
        return id;
    }

    public Map<String, Integer> getSignature2index() {
        return signature2index;
    }
}
