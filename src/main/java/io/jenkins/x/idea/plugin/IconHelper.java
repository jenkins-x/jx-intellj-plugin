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

import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.ScalableIcon;
import com.intellij.util.xmlb.annotations.Transient;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.jenkins.x.client.util.Strings.empty;
import static io.jenkins.x.client.util.URLHelpers.pathJoin;

/**
 */
public class IconHelper {

    public static final int MAX_WIDTH = 24;
    public static final int MAX_HEIGHT = 24;

    @Transient
    private static Map<String, Icon> iconMap = new ConcurrentHashMap<>();

    public static Icon getIcon(String fileName) {
        if (empty(fileName)) {
            return null;
        }
        Icon icon = iconMap.get(fileName);
        if (icon == null) {
            String path = pathJoin("/icons", fileName);
            icon = IconLoader.getIcon(path);
            if (icon != null) {
                int height = icon.getIconHeight();
                int width = icon.getIconWidth();
                if (height > MAX_HEIGHT || width > MAX_WIDTH) {
                    if (icon instanceof ScalableIcon) {
                        ScalableIcon imageIcon = (ScalableIcon) icon;
                        float maxHeightOrWidth = height;
                        if (width > height) {
                            maxHeightOrWidth = width;
                        }
                        float scale = ((float) MAX_HEIGHT) / maxHeightOrWidth;
                        icon = imageIcon.scale(scale);
                    } else if (icon instanceof ImageIcon) {
                        ImageIcon imageIcon = (ImageIcon) icon;
                        Image scaledImage = imageIcon.getImage().getScaledInstance(MAX_WIDTH, MAX_HEIGHT, Image.SCALE_DEFAULT);
                        icon = new ImageIcon(scaledImage);
                    }
                }
                iconMap.put(fileName, icon);
            }
        }
        return icon;
    }
}
