/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jenkins.x.idea.plugin;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPopupMenu;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import io.jenkins.x.client.tree.BranchNode;
import io.jenkins.x.client.tree.BuildNode;
import io.jenkins.x.client.tree.PipelineTreeModel;
import io.jenkins.x.client.tree.StageNode;
import io.jenkins.x.client.tree.TreeItem;
import io.jenkins.x.idea.plugin.actions.GetBuildLogsAction;
import io.jenkins.x.idea.plugin.actions.OpenURLActionSupport;
import io.jenkins.x.idea.plugin.actions.StartPipelineAction;
import io.jenkins.x.idea.plugin.actions.StopPipelineAction;
import io.jenkins.x.idea.plugin.actions.TreeItemActionGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static io.jenkins.x.client.util.Strings.notEmpty;

/**
 */
public class JenkinsXToolWindowFactory implements ToolWindowFactory {
    @Override

    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JenkinsXTreeModel model = new JenkinsXTreeModel(PipelineTreeModel.newInstance());
        Tree tree = new Tree(model);
        tree.setCellRenderer(new JenkinsXTreeCellRenderer());
        tree.addMouseListener(new JXTreeMouseListener(tree, project));
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(tree, "", true);
        toolWindow.getContentManager().addContent(content);
    }

    @Override
    public void init(ToolWindow window) {
    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return true;
    }

    @Override
    public boolean isDoNotActivateOnStart() {
        return false;
    }

    private static class JXTreeMouseListener extends MouseAdapter {
        private final Tree tree;
        private Project project;

        public JXTreeMouseListener(@NotNull Tree tree, @NotNull Project project) {
            this.tree = tree;
            this.project = project;
        }

        public void mousePressed(MouseEvent e) {
            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
            if (path == null) {
                return;
            }

            Object component = path.getLastPathComponent();
            if (component instanceof TreeItem) {
                TreeItem treeItem = (TreeItem) component;

                if (SwingUtilities.isRightMouseButton(e)) {
/*
                    ActionGroup group = new TreeItemActionGroup(treeItem);
                    ActionPopupMenu popupMenu = ActionManager.getInstance().createActionPopupMenu("JenkinsXPopup", group);
                    popupMenu.setTargetComponent(tree);
                    JPopupMenu actionPopup = popupMenu.getComponent();
                    //actionPopup.setVisible(true);
*/


                    JPopupMenu popup = new JPopupMenu();
                    if (addPopupMenusForItem(popup, treeItem)) {
                        popup.show(tree, e.getX(), e.getY());
                    }
                }
            }
        }

        protected boolean addPopupMenusForItem(JPopupMenu popup, TreeItem treeItem) {
            String buildUrl = null;
            String buildLogsUrl = null;
            String gitUrl = null;
            String releaseNotesUrl = null;
            String pipeline = null;
            String build = null;
            String appUrl = null;
            String prUrl = null;
            String prName = "";
            String updateUrl = null;
            BuildNode buildNode = null;
            if (treeItem instanceof BuildNode) {
                buildNode = (BuildNode) treeItem;
                buildUrl = buildNode.getBuildUrl();
                buildLogsUrl = buildNode.getBuildLogsUrl();
                build = buildNode.getBuild();
            } else if (treeItem instanceof BranchNode) {
                BranchNode branchNode = (BranchNode) treeItem;
                List<BuildNode> children = branchNode.getChildren();
                if (!children.isEmpty()) {
                    buildNode = children.get(0);
                }
            } else if (treeItem instanceof StageNode) {
                StageNode stageNode = (StageNode) treeItem;
                appUrl = stageNode.getApplicationURL();
                prUrl = stageNode.getPullRequestURL();
                prName = stageNode.getPullRequestName();
                updateUrl = stageNode.getUpdatePipelineURL();
            }
            if (buildNode != null) {
                gitUrl = buildNode.getGitUrl();
                releaseNotesUrl = buildNode.getReleaseNotesUrl();
                pipeline = buildNode.getSpec().getPipeline();
            }
            if (notEmpty(pipeline)) {
                if (notEmpty(build)) {
                    popup.add(new JMenuItem(new GetBuildLogsAction(project, pipeline, build)));
                    popup.addSeparator();
                }
                popup.add(new JMenuItem(new StartPipelineAction(project, pipeline)));
                if (notEmpty(build)) {
                    popup.add(new JMenuItem(new StopPipelineAction(project, pipeline, build)));
                }
                popup.addSeparator();
            }
            if (notEmpty(appUrl)) {
                popup.add(new JMenuItem(new OpenURLActionSupport(appUrl, "Open Application")));
            }
            if (notEmpty(prUrl)) {
                popup.add(new JMenuItem(new OpenURLActionSupport(prUrl, "Open Pull Request" + prName)));
            }
            if (notEmpty(updateUrl)) {
                popup.add(new JMenuItem(new OpenURLActionSupport(updateUrl, "Open Update Pipeline")));
            }
            if (notEmpty(buildLogsUrl)) {
                popup.add(new JMenuItem(new OpenURLActionSupport(buildLogsUrl, "Open Build Log")));
            }
            if (notEmpty(buildUrl)) {
                popup.add(new JMenuItem(new OpenURLActionSupport(buildUrl, "Open Build in Jenkins")));
            }
            if (notEmpty(gitUrl)) {
                popup.add(new JMenuItem(new OpenURLActionSupport(gitUrl, "Open Repository")));
            }
            if (notEmpty(releaseNotesUrl)) {
                popup.add(new JMenuItem(new OpenURLActionSupport(releaseNotesUrl, "Open Release Notes")));
            }
            return popup.getComponentCount() > 0;
        }
    }
}
