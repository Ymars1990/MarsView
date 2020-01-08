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
        LoadingStatus LOADING = new LoadingStatus(0, "加载中");
        LoadingStatus SUCCESS = new LoadingStatus(1, "成功");
        LoadingStatus FAILED = new LoadingStatus(2, "失败");
        LoadingStatus DISMISS = new LoadingStatus(3, "完成");
    }

    public int getStyleValue() {
        return styleValue;
    }

    public String getStyleName() {
        return styleName;
    }

    @Override
    public String toString() {
        return "LoadingStatus{" +
                "styleValue=" + styleValue +
                ", styleName='" + styleName + '\'' +
                '}';
    }
}
