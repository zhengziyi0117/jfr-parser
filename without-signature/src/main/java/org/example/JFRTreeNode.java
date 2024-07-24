package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JFRTreeNode {
    private int frameId;
    private int self;
    private double value;
    private Map<Integer, JFRTreeNode> childrenMap;


    public void merge(List<JFRTreeNode> nodes) {
        if (Objects.isNull(nodes) || nodes.isEmpty()) {
            return;
        }
        JFRTreeNode mergeNode = this;

        for (JFRTreeNode node : nodes) {
            JFRTreeNode matchChild = mergeNode.getChildrenMap().get(node.frameId);
            // 处理子节点
            if (Objects.isNull(matchChild)) {
                mergeNode.getChildrenMap().put(node.frameId, node);
                matchChild = node;
            } else {
                matchChild.self++;
                matchChild.value += node.value;
            }
            mergeNode = matchChild;
        }
    }
}
