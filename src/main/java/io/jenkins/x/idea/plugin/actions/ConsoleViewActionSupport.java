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

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import io.jenkins.x.idea.plugin.CommandHelper;
import io.jenkins.x.idea.plugin.SwingHelper;

import javax.swing.*;
import java.awt.*;

/**
 */
public abstract class ConsoleViewActionSupport extends AbstractAction {
    protected final Project project;

    public ConsoleViewActionSupport(String name, Project project) {
        super(name);
        this.project = project;
    }

    protected void openCommandInConsoleViewer(String title, String... commandArgs) {
        SwingHelper.runInSwingThread(() -> {
            TextConsoleBuilder builder = TextConsoleBuilderFactory.getInstance().createBuilder(project);
            builder.setViewer(true);
            ConsoleView consoleView = builder.getConsole();

            JPanel panel = new JPanel(new BorderLayout());

            DefaultActionGroup toolbarActions = new DefaultActionGroup();
            ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, toolbarActions, false);
            panel.add(actionToolbar.getComponent(), BorderLayout.WEST);
            panel.add(consoleView.getComponent(), BorderLayout.CENTER);
            actionToolbar.setTargetComponent(panel);

            toolbarActions.addAll(consoleView.createConsoleActions());
            addToolBarActions(toolbarActions);
            panel.updateUI();

            ProcessHandler processHandler = null;
            try {
                processHandler = CommandHelper.runCommand(project, commandArgs);
            } catch (ExecutionException e) {
                Object name = getValue(Action.NAME);
                System.out.println(name + " - Failed: " + e);
                e.printStackTrace();
            }

            if (processHandler != null) {
                consoleView.attachToProcess(processHandler);
            }
            RunContentDescriptor contentDescriptor = new RunContentDescriptor(consoleView, processHandler, panel, title);
            ExecutionManager.getInstance(project).getContentManager().showRunContent(DefaultRunExecutor.getRunExecutorInstance(), contentDescriptor);
        });
    }

    protected void addToolBarActions(DefaultActionGroup toolbarActions) {
        // TODO
        //toolbarActions.addAction(new StopPipelineAction(project, pipeline, build));
    }
}
