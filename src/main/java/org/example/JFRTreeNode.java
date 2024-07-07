package org.example;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public record JFRTreeNode(int frameId, int self, int value, Set<JFRTreeNode> children) {
    public void merge(List<JFRTreeNode> nodes, Map<Integer, JFRTreeNode> index2node) {
        if (Objects.isNull(nodes) || nodes.isEmpty()) {
            return;
        }
        JFRTreeNode mergeNode = this;
        for (JFRTreeNode node : nodes) {
            if (index2node.containsKey(node.frameId)) {
                mergeNode = index2node.get(node.frameId);
            } else {
                mergeNode.children.add(node);
            }
        }
    }
}
