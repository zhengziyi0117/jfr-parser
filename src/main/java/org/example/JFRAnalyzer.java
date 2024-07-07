package org.example;

import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedFrame;
import jdk.jfr.consumer.RecordingFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class JFRAnalyzer {
    private static final String HYPHEN = " - ";

    private final String fileName;

    private final Map<String, Integer> signature2index = new HashMap<>();
    private final Map<Integer, JFRTreeNode> index2Node = new HashMap<>();

    private int incrId;

    private final JFRTreeNode all = new JFRTreeNode(-1, 0, 0, new HashSet<>());

    public JFRAnalyzer(String fileName) throws IOException {
        this.fileName = fileName;
    }

    public void records() {
        try (RecordingFile recordingFile = new RecordingFile(Path.of(fileName))) {
            while (recordingFile.hasMoreEvents()) {
                RecordedEvent event = recordingFile.readEvent();
                if (JFREventType.EXECUTION_SAMPLE.getName().equals(event.getEventType().getName())) {
                    parseExecutionSample(event);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void parseExecutionSample(RecordedEvent event) {
        List<RecordedFrame> frames = event.getStackTrace().getFrames();
        List<JFRTreeNode> nodes = new ArrayList<>();
        JFRTreeNode preNode = null;
        for (RecordedFrame frame : frames) {
            String frameName = getFrameName(frame);
            int frameId = getId(frameName);
            JFRTreeNode node = new JFRTreeNode(frameId, 1, 1, new HashSet<>());
            index2Node.put(frameId, node);
            if (preNode != null) {
                preNode.children().add(node);
            }
            preNode = node;
            nodes.add(node);
        }
        all.merge(nodes, index2Node);
    }

    private String getFrameName(RecordedFrame frame) {
        if (frame.isJavaFrame()) {
            return frame.getMethod().getName() + HYPHEN + frame.getLineNumber();
        } else {
            return frame.getMethod().getName();
        }
    }

    private int getId(String frameName) {
        Integer id = signature2index.get(frameName);
        if (id == null) {
            id = incrId++;
            signature2index.put(frameName, id);
        }
        return id;
    }
}
