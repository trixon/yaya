/*
 * Copyright 2022 Patrik Karlström <patrik@trixon.se>.
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
package se.trixon.yaya.themes;

import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.openide.util.Exceptions;
import se.trixon.almond.util.SystemHelper;
import se.trixon.yaya.Yaya;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public abstract class ThemeProvider {

    private final String mId;

    public ThemeProvider(String id) {
        mId = id;
    }

    public String getId() {
        return mId;
    }

    public Theme load() throws JsonSyntaxException {
        var theme = Yaya.GSON.fromJson(getTheme(), Theme.class);

        return theme;
    }

    private String getTheme() {
        try ( var inputStream = getClass().getResourceAsStream("/" + SystemHelper.getPackageAsPath(getClass()) + mId)) {
            return IOUtils.toString(inputStream, "UTF-8");
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return "";
    }

}
