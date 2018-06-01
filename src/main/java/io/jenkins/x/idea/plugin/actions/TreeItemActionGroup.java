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
package io.jenkins.x.idea.plugin.actions;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import io.jenkins.x.client.tree.BranchNode;
import io.jenkins.x.client.tree.BuildNode;
import io.jenkins.x.client.tree.StageNode;
import io.jenkins.x.client.tree.TreeItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static io.jenkins.x.client.util.Strings.notEmpty;

/**
 */
public class TreeItemActionGroup extends ActionGroup {
    private final TreeItem treeItem;

    public TreeItemActionGroup(TreeItem treeItem) {
        this.treeItem = treeItem;
    }

    public TreeItem getTreeItem() {
        return treeItem;
    }

    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent anActionEvent) {
        List<AnAction> list = new ArrayList<>();
        TreeItem treeItem = getTreeItem();
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
/*
                    if (notEmpty(pipeline)) {
                        if (notEmpty(build)) {
                            list.add(new GetBuildLogsAction(project, pipeline, build)));
                            popup.addSeparator();
                        }
                        list.add(new StartPipelineAction(project, pipeline)));
                        if (notEmpty(build)) {
                            list.add(new StopPipelineAction(project, pipeline, build)));
                        }
                        popup.addSeparator();
                    }
*/
        if (notEmpty(appUrl)) {
            list.add(new OpenURLAnActionSupport(appUrl, "Open Application"));
        }
        if (notEmpty(prUrl)) {
            list.add(new OpenURLAnActionSupport(prUrl, "Open Pull Request" + prName));
        }
        if (notEmpty(updateUrl)) {
            list.add(new OpenURLAnActionSupport(updateUrl, "Open Update Pipeline"));
        }
        if (notEmpty(buildLogsUrl)) {
            list.add(new OpenURLAnActionSupport(buildLogsUrl, "Open Build Log"));
        }
        if (notEmpty(buildUrl)) {
            list.add(new OpenURLAnActionSupport(buildUrl, "Open Build in Jenkins"));
        }
        if (notEmpty(gitUrl)) {
            list.add(new OpenURLAnActionSupport(gitUrl, "Open Repository"));
        }
        if (notEmpty(releaseNotesUrl)) {
            list.add(new OpenURLAnActionSupport(releaseNotesUrl, "Open Release Notes"));
        }

        return list.toArray(new AnAction[list.size()]);
    }
}
