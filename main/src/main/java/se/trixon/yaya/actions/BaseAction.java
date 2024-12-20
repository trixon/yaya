/*
 * Copyright 2024 Patrik Karlström <patrik@trixon.se>.
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
package se.trixon.yaya.actions;

import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import org.apache.commons.lang3.SystemUtils;
import org.openide.util.NbBundle;
import se.trixon.yaya.Options;
import se.trixon.yaya.Yaya;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
public abstract class BaseAction implements ActionListener {

    protected static final boolean IS_MAC = SystemUtils.IS_OS_MAC;
    protected final ResourceBundle mBundle = NbBundle.getBundle(BaseAction.class);
    protected Options mOptions = Options.getInstance();
    protected Yaya mYaya = Yaya.getInstance();
}
