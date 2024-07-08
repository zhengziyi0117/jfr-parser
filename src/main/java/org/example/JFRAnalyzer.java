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
//    private static final String LINE_CONCAT = ":";
    private static final String METHOD_CONCAT = "::";

    private final String fileName;

    private final Map<String, Integer> signature2index = new HashMap<>();

    private int incrId;

    private final JFRTreeNode all = new JFRTreeNode(-1, 0, 0, new HashSet<>());

    public JFRAnalyzer(String fileName) {
        this.fileName = fileName;
    }

    public JFRTreeNode parseRecords() {
        try (RecordingFile recordingFile = new RecordingFile(Path.of(fileName))) {
            while (recordingFile.hasMoreEvents()) {
                RecordedEvent event = recordingFile.readEvent();
                if (JFREventType.EXECUTION_SAMPLE.getName().equals(event.getEventType().getName())) {
                    parseExecutionSample(event);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return all;
    }

    public void parseExecutionSample(RecordedEvent event) {
        List<RecordedFrame> frames = event.getStackTrace().getFrames();
        List<JFRTreeNode> nodes = new ArrayList<>();
        for (RecordedFrame frame : frames) {
            String frameName = getFrameName(frame);
            int frameId = getId(frameName);
            JFRTreeNode node = new JFRTreeNode(frameId, 1, 1, new HashSet<>());
            nodes.add(node);
        }
        List<JFRTreeNode> reversedNodes = nodes.reversed();
        all.merge(reversedNodes);
    }

    private String getFrameName(RecordedFrame frame) {
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
