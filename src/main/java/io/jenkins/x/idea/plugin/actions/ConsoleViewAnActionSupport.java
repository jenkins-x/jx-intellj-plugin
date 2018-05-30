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

import com.intellij.execution.ExecutionManager;
import com.intellij.execution.configurations.PtyCommandLine;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.terminal.TerminalExecutionConsole;
import io.jenkins.x.idea.plugin.SwingHelper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.Charset;


/**
 */
public abstract class ConsoleViewAnActionSupport extends AnAction {
    private final Charset myDefaultCharset = CharsetToolkit.UTF8_CHARSET;

    public ConsoleViewAnActionSupport() {
    }

    public ConsoleViewAnActionSupport(Icon icon) {
        super(icon);
    }

    public ConsoleViewAnActionSupport(@Nullable String text) {
        super(text);
    }

    public ConsoleViewAnActionSupport(@Nullable String text, @Nullable String description, @Nullable Icon icon) {
        super(text, description, icon);
    }


    protected void openCommandInConsoleViewer(Project project, String title, String exe, String... commandArgs) {
        SwingHelper.runInSwingThread(() -> {
            ProcessHandler processHandler;
            try {
                PtyCommandLine cli = new PtyCommandLine();
                cli.setCharset(Charset.forName("UTF-8"));
                cli.setWorkDirectory(project.getBasePath());
                cli.setExePath(exe);
                cli.withParameters(commandArgs);
                processHandler = new OSProcessHandler(cli);

            } catch (Exception e) {
                System.out.println("" + this + " Failed: " + e);
                e.printStackTrace();
                return;
            }


            TerminalExecutionConsole consoleView = new TerminalExecutionConsole(project, processHandler);
            JPanel panel = new JPanel(new BorderLayout());

            DefaultActionGroup toolbarActions = new DefaultActionGroup();
            ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, toolbarActions, false);
            panel.add(actionToolbar.getComponent(), BorderLayout.WEST);
            panel.add(consoleView.getComponent(), BorderLayout.CENTER);
            actionToolbar.setTargetComponent(panel);

            toolbarActions.addAll(consoleView.createConsoleActions());
            addToolBarActions(project, toolbarActions);
            panel.updateUI();

            processHandler.startNotify();

            RunContentDescriptor contentDescriptor = new RunContentDescriptor(consoleView, processHandler, panel, title);
            ExecutionManager.getInstance(project).getContentManager().showRunContent(DefaultRunExecutor.getRunExecutorInstance(), contentDescriptor);
        });
    }

    protected void addToolBarActions(Project project, DefaultActionGroup toolbarActions) {
        // TODO
        //toolbarActions.addAction(new StopPipelineAction(project, pipeline, build));
    }
}
