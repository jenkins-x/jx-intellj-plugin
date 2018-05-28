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
package io.jenkins.x.idea.plugin.nodes;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.project.Project;
import io.jenkins.x.client.tree.TreeItem;
import io.jenkins.x.idea.plugin.IconHelper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static io.jenkins.x.client.util.Strings.notEmpty;

/**
 */
public class PipelinesNode extends AbstractTreeNode {
    public PipelinesNode(Project project, TreeItem item) {
        super(project, item);
    }

    @NotNull
    @Override
    public Collection<PipelinesNode> getChildren() {
        List<PipelinesNode> children = new ArrayList<>();
        TreeItem treeItem = getTreeItem();
        if (treeItem != null) {
            List<TreeItem> childrenItems = treeItem.getChildrenItems();
            for (TreeItem child : childrenItems) {
                PipelinesNode pipelineChild = new PipelinesNode(getProject(), child);
                children.add(pipelineChild);
            }
        }
        return children;
    }

    public TreeItem getTreeItem() {
        return (TreeItem) getValue();
    }

    @Override
    protected void update(PresentationData presentationData) {
        TreeItem treeItem = getTreeItem();
        if (treeItem != null) {
            presentationData.setPresentableText(treeItem.getLabel());
            presentationData.setTooltip(treeItem.getTooltip());
            String iconPath = treeItem.getIconPath();
            if (notEmpty(iconPath)) {
                Icon icon = IconHelper.getIcon(iconPath);
                if (icon != null) {
                    presentationData.setIcon(icon);
                }
            }
        }
    }
}
