package com.mars.component.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 加载状态注解
 *
 * @author Mars
 */
@LoadingStatus.StatusType
public class LoadingStatus {
    /**
     * 加载状态
     */
    private final int styleValue;
    private final String styleName;

    public LoadingStatus(int styleValue, String styleName) {
        this.styleValue = styleValue;
        this.styleName = styleName;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface StatusType {
        LoadingStatus loading = new LoadingStatus(0, "加载中");
        LoadingStatus success = new LoadingStatus(1, "成功");
        LoadingStatus failed = new LoadingStatus(2, "失败");
    }

    public int getStyleValue() {
        return styleValue;
    }

    public String getStyleName() {
        return styleName;
    }
}
