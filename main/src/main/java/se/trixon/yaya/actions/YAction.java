/*
 * Copyright 2022 Patrik Karlström.
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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import org.apache.commons.lang3.SystemUtils;
import org.controlsfx.control.action.Action;
import se.trixon.almond.util.fx.FxHelper;
import se.trixon.yaya.App;
import se.trixon.yaya.Options;
import se.trixon.yaya.Yaya;

/**
 *
 * @author Patrik Karlström
 */
public class YAction extends Action {

    protected static final boolean IS_MAC = SystemUtils.IS_OS_MAC;
    protected Options mOptions = Options.getInstance();
    protected Yaya mYaya = Yaya.getInstance();
    private Runnable mPostInitRunnable;

    public YAction(String text) {
        super(text);
        mYaya.stageProperty().addListener((ObservableValue<? extends Stage> observable, Stage oldValue, Stage newValue) -> {
            if (mPostInitRunnable != null) {
                mPostInitRunnable.run();
            }
        });
    }

    public void setPostInitRunnable(Runnable postInitRunnable) {
        mPostInitRunnable = postInitRunnable;
    }

    protected void addTooltipKeyCode(KeyCodeCombination keyCodeCombination) {
        FxHelper.setTooltip(this, keyCodeCombination);
    }

    protected String category() {
        var description = getClass().getAnnotation(Description.class);
        if (description != null) {
            return description.category();
        }
        return getClass().getName();
    }

    protected ObservableMap<KeyCombination, Runnable> getAccelerators() {
        return getStage().getScene().getAccelerators();
    }

    protected App getApplication() {
        return mYaya.getApplication();
    }

    protected Stage getStage() {
        return mYaya.getStage();
    }

    protected String id() {
        var description = getClass().getAnnotation(Description.class);
        if (description != null) {
            return description.id();
        }
        return getClass().getName();
    }

    protected void setAcceleratorForStage(KeyCodeCombination keyCodeCombination) {
        getAccelerators().put(keyCodeCombination, () -> {
            handle(null);
        });
    }

    @Retention(value = RetentionPolicy.RUNTIME)
    @Target(value = ElementType.TYPE)
    public @interface Description {

        String category() default "";

        String id();
    }
}
