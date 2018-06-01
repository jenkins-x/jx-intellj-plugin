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

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import javax.swing.*;
import java.awt.*;
import java.net.URI;

import static io.jenkins.x.client.util.Strings.notEmpty;
import static io.jenkins.x.idea.plugin.SwingHelper.runInSwingThread;

/**
 */
public class OpenURLAnActionSupport extends AnAction {
    private final String url;

    public OpenURLAnActionSupport(String url, String name) {
        super(name);
        this.url = url;
    }

    public OpenURLAnActionSupport(String url, String name, String description, Icon icon) {
        super(name, description, icon);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        runInSwingThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Opening URL " + url);
                if (Desktop.isDesktopSupported() && notEmpty(url)) {
                    try {
                        URI uri = new URI(url);
                        Desktop.getDesktop().browse(uri);
                    } catch (Exception e) {
                        System.out.println("Failed: " + e);
                        e.printStackTrace();
                    }
                }

            }
        });
    }
}
