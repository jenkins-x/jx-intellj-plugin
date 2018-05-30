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

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

/**
 */
public class OpenDevPodAction extends ConsoleViewAnActionSupport {

    public OpenDevPodAction() {
        super("Open Jenkins X DevPod", "Opens the DevPod in a terminal", null);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        if (project != null) {
            String name = project.getName();
            System.out.println("Opening JX DevPod for: " + name + " at " + project.getBasePath());

            openCommandInConsoleViewer(project, "jx sync", "jx", "sync", "--watch-only");
            openCommandInConsoleViewer(project, "jx devpod: " + name, "jx", "create", "devpod", "--reuse", "--sync");
        }
    }
}
