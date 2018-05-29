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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URI;

import static io.jenkins.x.client.util.Strings.notEmpty;

/**
 */
public class OpenURLActionSupport extends AbstractAction {
    private final String url;

    public OpenURLActionSupport(String url, String name) {
        super(name);
        this.url = url;
    }

    public OpenURLActionSupport(String url, String name, Icon icon) {
        super(name, icon);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
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
}
