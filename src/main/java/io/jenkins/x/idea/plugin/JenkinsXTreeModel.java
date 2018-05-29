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

import io.jenkins.x.client.tree.PipelineTreeModel;
import io.jenkins.x.client.tree.TreeItem;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 */
public class JenkinsXTreeModel implements TreeModel {
    private final PipelineTreeModel treeModel;
    private List<TreeModelListener> listeners = new ArrayList<>();


    public JenkinsXTreeModel(PipelineTreeModel treeModel) {
        this.treeModel = treeModel;
        treeModel.addListener(new io.jenkins.x.client.tree.TreeModelListener() {
            @Override
            public void itemAdded(TreeItem treeItem) {
                TreeModelEvent event = createTreeModelEvent(treeItem);
                for (TreeModelListener listener : listeners) {
                    listener.treeNodesInserted(event);
                }
            }

            @Override
            public void itemUpdated(TreeItem treeItem) {
                TreeModelEvent event = createTreeModelEvent(treeItem);
                for (TreeModelListener listener : listeners) {
                    listener.treeStructureChanged(event);
                }
            }

            @Override
            public void itemDeleted(TreeItem treeItem) {
                TreeModelEvent event = createTreeModelEvent(treeItem);
                for (TreeModelListener listener : listeners) {
                    listener.treeNodesInserted(event);
                }
            }
        });
    }

    protected TreeModelEvent createTreeModelEvent(TreeItem treeItem) {
        TreePath path = new TreePath(treeItem.getPath());
        return new TreeModelEvent(this, path);
    }

    @Override
    public Object getRoot() {
        return treeModel;
    }

    public List<TreeItem> getChildren(Object parent) {
        if (parent instanceof TreeItem) {
            TreeItem treeItem = (TreeItem) parent;
            return treeItem.getChildrenItems();
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public Object getChild(Object parent, int index) {
        List<TreeItem> list = getChildren(parent);
        if (index >= 0 && index < list.size()) {
            return list.get(index);
        }
        return null;
    }

    @Override
    public int getChildCount(Object parent) {
        List<TreeItem> list = getChildren(parent);
        return list.size();
    }

    @Override
    public boolean isLeaf(Object node) {
        return getChildren(node).isEmpty();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        // TODO

    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        List<TreeItem> list = getChildren(parent);
        return list.indexOf(child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener listener) {
        listeners.remove(listener);
    }
}
