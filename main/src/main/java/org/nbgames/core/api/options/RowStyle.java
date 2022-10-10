/* 
 * Copyright 2018 Patrik Karlström.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nbgames.core.api.options;

import java.awt.Color;
import java.awt.Font;

/**
 *
 * @author Patrik Karlström
 */
public class RowStyle {

    private Color mBackground;
    private Font mFont;
    private Color mForeground;

    public RowStyle() {
    }

    public Color getBackground() {
        return mBackground;
    }

    public Font getFont() {
        return mFont;
    }

    public Color getForeground() {
        return mForeground;
    }

    public void setBackground(Color background) {
        mBackground = background;
    }

    public void setFont(Font font) {
        mFont = font;
    }

    public void setForeground(Color foreground) {
        mForeground = foreground;
    }
}
