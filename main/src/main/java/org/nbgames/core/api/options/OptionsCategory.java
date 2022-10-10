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

import javax.swing.ImageIcon;
import org.nbgames.core.api.GameCategory;
import se.trixon.almond.nbp.Almond;
import se.trixon.almond.util.Dict;
import se.trixon.almond.util.icons.material.MaterialIcon;

/**
 *
 * @author Patrik Karlström
 */
public enum OptionsCategory {

    PLAYERS(MaterialIcon._Social.PEOPLE, Dict.Game.PLAYERS.toString()),
    ARCADE(MaterialIcon._Editor.LINEAR_SCALE, GameCategory.ARCADE.getString()),
    BOARD(MaterialIcon._Editor.LINEAR_SCALE, GameCategory.BOARD.getString()),
    CARD(MaterialIcon._Editor.LINEAR_SCALE, GameCategory.CARD.getString()),
    DICE(MaterialIcon._Editor.LINEAR_SCALE, GameCategory.DICE.getString()),
    LOGIC(MaterialIcon._Editor.LINEAR_SCALE, GameCategory.LOGIC.getString()),
    STRATEGY(MaterialIcon._Editor.LINEAR_SCALE, GameCategory.STRATEGY.getString()),
    SYSTEM(MaterialIcon._Action.BUILD, Dict.SYSTEM.toString());

//    private final IconColor mIconColor = IconColor.getDefault();
    private ImageIcon mImageIcon;
    private String mTitle;

    private OptionsCategory(MaterialIcon.IconGetter iconGetter, String title) {
        mImageIcon = iconGetter.getImageIcon(Almond.ICON_SMALL);
        mTitle = title;
    }

    public ImageIcon getImageIcon() {
        return mImageIcon;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setImageIcon(ImageIcon imageIcon) {
        mImageIcon = imageIcon;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
