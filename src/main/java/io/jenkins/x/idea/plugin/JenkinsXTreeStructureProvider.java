package io.jenkins.x.idea.plugin;

import com.intellij.ide.projectView.TreeStructureProvider;
import com.intellij.ide.projectView.ViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import io.jenkins.x.client.tree.PipelineTreeModel;
import io.jenkins.x.idea.plugin.nodes.PipelinesNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

/**
 */
public class JenkinsXTreeStructureProvider implements TreeStructureProvider {
    private PipelineTreeModel treeModel;

    public JenkinsXTreeStructureProvider() {
        try {
            this.treeModel = PipelineTreeModel.newInstance();
        } catch (Exception e) {
            System.out.println("Failed: " + e);
            e.printStackTrace();
        }
    }

    @NotNull
    @Override
    public Collection<AbstractTreeNode> modify(@NotNull AbstractTreeNode parent,
                                               @NotNull Collection<AbstractTreeNode> children,
                                               ViewSettings settings) {

        ArrayList<AbstractTreeNode> nodes = new ArrayList<AbstractTreeNode>();
        for (AbstractTreeNode child : children) {
            nodes.add(child);
        }
        if (parent != null && parent.getParent() == null && !(parent instanceof PipelinesNode)) {
            nodes.add(createPipelinesNode(parent.getProject()));
        }
        return nodes;
    }

    private AbstractTreeNode createPipelinesNode(Project project) {
        return new PipelinesNode(project, treeModel);
    }

    @Nullable
    @Override
    public Object getData(Collection<AbstractTreeNode> collection, String s) {
        return null;
    }
}