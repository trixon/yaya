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
package org.nbgames.yaya.scorecard;

import java.awt.Color;
import javax.swing.JLabel;

/**
 *
 * @author Patrik Karlström
 */
public class RowLabel extends JLabel {

    private Color mCurrentBackgroundColor;

    public RowLabel() {
    }

    public Color getCurrentBackgroundColor() {
        return mCurrentBackgroundColor;
    }

    public void setBackground() {
        setBackground(mCurrentBackgroundColor);
    }

    public void setCurrentBackgroundColor(Color color) {
        mCurrentBackgroundColor = color;
    }
}
