package one.convert;

import java.util.List;

import static one.convert.Frame.TYPE_INTERPRETED;
import static one.convert.Frame.TYPE_NATIVE;

public class JfrMergeBuilder {
    private final Index<String> cpool = new Index<>(String.class, "");
    private final Frame root = new Frame(0, TYPE_NATIVE);

    public JfrMergeBuilder merge(List<FrameTree> trees) {
        if (trees == null || trees.isEmpty()) {
            return this;
        }
        for (FrameTree tree : trees) {
            merge0(root, tree);
        }
        return this;
    }

    public JfrMergeBuilder merge(FrameTree tree) {
        merge0(root, tree);
        return this;
    }

    public void merge0(Frame frame, FrameTree tree) {
        if (tree == null) {
            return;
        }
        if (tree.getChildren() != null) {
            for (FrameTree children : tree.getChildren()) {
                Frame child = addChild(frame, children.getFrame());
                merge0(child, children);
            }
        }
        frame.total += tree.getTotal();
        frame.self += tree.getSelf();
    }

    private Frame addChild(Frame frame, String title) {
        int titleIndex = cpool.index(title);
        return frame.getChild(titleIndex, TYPE_INTERPRETED);
    }

    public FrameTree build() {
        String[] keys = cpool.keys();
        return FrameTree.buildTree(root, keys);
    }
}
