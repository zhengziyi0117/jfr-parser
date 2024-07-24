package org.example.with_signature;

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
    private String frame;
    private Integer self;
    private double value;
    private Map<String, JFRTreeNode> childrenMap; // frame 2 childNode


    public void merge(List<JFRTreeNode> nodes) {
        if (Objects.isNull(nodes) || nodes.isEmpty()) {
            return;
        }
        JFRTreeNode mergeNode = this;

        for (JFRTreeNode node : nodes) {
            JFRTreeNode matchChild = mergeNode.getChildrenMap().get(node.frame);
            // 处理子节点
            if (Objects.isNull(matchChild)) {
                mergeNode.getChildrenMap().put(node.frame, node);
                matchChild = node;
            } else {
                matchChild.self++;
                matchChild.value += node.value;
            }
            mergeNode = matchChild;
        }
    }
}
