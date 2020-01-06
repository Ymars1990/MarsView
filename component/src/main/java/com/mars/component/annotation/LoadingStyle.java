package com.mars.component.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 加载样式注解
 *
 * @author Mars
 */
@LoadingStyle.StyleType
public class LoadingStyle {
    /**
     * 加载样式
     */
    private final int styleValue;
    private final String styleName;

    public LoadingStyle(int styleValue, String styleName) {
        this.styleValue = styleValue;
        this.styleName = styleName;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface StyleType {
        LoadingStyle NORMAL = new LoadingStyle(0, "默认圆形");
        LoadingStyle DOT = new LoadingStyle(1, "点状");
        LoadingStyle PILLAR = new LoadingStyle(2, "柱状");
    }

    public int getStyleValue() {
        return styleValue;
    }

    public String getStyleName() {
        return styleName;
    }
}
