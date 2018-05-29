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
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 */
public abstract class CommandActionSupport extends AbstractAction {
    protected Project project;

    public CommandActionSupport(String name, Project project) {
        super(name);
        this.project = project;
    }

    public void actionPerformed(ActionEvent event) {
        Object name = getValue(Action.NAME);
        System.out.println("Invoking " + name);
        try {
            doRunCommand();
        } catch (ExecutionException e) {
            System.out.println(name + " - Failed: " + e);
            e.printStackTrace();
        }
    }

    protected abstract void doRunCommand() throws ExecutionException;
}
