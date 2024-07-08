package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JFRTreeNode {
    private int frameId;
    private int self;
    private int value;
    private Set<JFRTreeNode> children;


    public void merge(List<JFRTreeNode> nodes) {
        if (Objects.isNull(nodes) || nodes.isEmpty()) {
            return;
        }
        JFRTreeNode mergeNode = this;

        for (JFRTreeNode node : nodes) {
            JFRTreeNode matchChild = null;
            for (JFRTreeNode child : mergeNode.children) {
                if (child.frameId == node.frameId) {
                    matchChild = child;
                    break;
                }
            }
            // 处理子节点
            if (Objects.isNull(matchChild)) {
                mergeNode.children.add(node);
                matchChild = node;
            } else {
                matchChild.self++;
                matchChild.value += node.value;
            }
            mergeNode = matchChild;
        }
    }
}
