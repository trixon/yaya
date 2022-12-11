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
package se.trixon.yaya;

import java.awt.EventQueue;
import org.openide.modules.OnStart;
import se.trixon.almond.util.SystemHelper;
import se.trixon.yaya.scorecard.rules.RuleManager;

/**
 *
 * @author Patrik Karlström <patrik@trixon.se>
 */
@OnStart
public class DoOnStart implements Runnable {

    private final Options mOptions = Options.getInstance();

    @Override
    public void run() {
        SystemHelper.runLaterDelayed(300, () -> {
            EventQueue.invokeLater(() -> {
                RuleManager.getInstance().init();
                new Thread(() -> {
                    App.main(new String[]{});
                }).start();
            });
        });
    }

}
