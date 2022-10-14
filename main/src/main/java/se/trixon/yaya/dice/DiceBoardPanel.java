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
package se.trixon.yaya.dice;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import org.nbgames.core.api.NbGames;

/**
 *
 * @author Patrik Karlström
 */
class DiceBoardPanel extends JPanel {

    private static final Dimension PREFERRED_DIMENSION = new Dimension(1000, 200);
    private BufferedImage mBackgroundImage;

    DiceBoardPanel() {
        init();
    }

    public BufferedImage getBackgroundImage() {
        return mBackgroundImage;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        if (mBackgroundImage != null) {
            for (int i = 0; i < (getHeight() / mBackgroundImage.getHeight()) + 1; i++) {
                if (getWidth() < mBackgroundImage.getWidth()) {
                    g2.drawImage(mBackgroundImage, 0, i * mBackgroundImage.getHeight(), null);
                } else {
                    g2.drawImage(mBackgroundImage, 0, i * mBackgroundImage.getHeight(), getWidth(), mBackgroundImage.getHeight(), null);
                }
            }
        }

        super.paint(g);
        g2.dispose();
    }

    public void setBackgroundImage(BufferedImage backgroundImage) {
        mBackgroundImage = backgroundImage;
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    private void init() {
        setBackgroundImage(NbGames.getImage("images/wood_panel1.jpg"));
        setPreferredSize(PREFERRED_DIMENSION);
        setLayout(new GridLayout(1, 1));

        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                repaint();
                revalidate();
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
            }

        });
    }
}
