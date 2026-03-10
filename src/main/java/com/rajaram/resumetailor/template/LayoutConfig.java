package com.rajaram.resumetailor.template;

import java.awt.*;

public class LayoutConfig {

    public float sectionSpacingBefore;
    public float sectionSpacingAfter;
    public float paragraphSpacing;
    public float experienceSpacing;
    public float bulletIndent;
    public float skillSpacing;
    public boolean showDivider;

    public Color sectionColor;
    public Color nameColor;
    public Color dateColor;
    public Color dividerColor;

    public LayoutConfig(
            float sectionBefore,
            float sectionAfter,
            float paragraphSpacing,
            float experienceSpacing,
            float bulletIndent,
            float skillSpacing,
            boolean showDivider,
            Color sectionColor,
            Color nameColor,
            Color dateColor,
            Color dividerColor
    ) {
        this.sectionSpacingBefore = sectionBefore;
        this.sectionSpacingAfter = sectionAfter;
        this.paragraphSpacing = paragraphSpacing;
        this.experienceSpacing = experienceSpacing;
        this.bulletIndent = bulletIndent;
        this.skillSpacing = skillSpacing;
        this.showDivider = showDivider;
        this.sectionColor = sectionColor;
        this.nameColor = nameColor;
        this.dateColor = dateColor;
        this.dividerColor = dividerColor;
    }

    public static LayoutConfig compact() {
        return new LayoutConfig(
                4f,
                2f,
                1.5f,
                4f,
                10f,
                2f,
                false,
                new Color(40, 40, 40),
                Color.BLACK,
                new Color(80, 80, 80),
                Color.BLACK
        );
    }

    public static LayoutConfig corporate() {
        return new LayoutConfig(
                10f,
                6f,
                4f,
                8f,
                16f,
                4f,
                true,
                Color.BLACK,
                Color.BLACK,
                new Color(60, 60, 60),
                Color.BLACK
        );
    }

    public static LayoutConfig modern() {
        return new LayoutConfig(
                10f,
                6f,
                4f,
                8f,
                14f,
                4f,
                true,
                new Color(25, 60, 120),
                new Color(25, 60, 120),
                new Color(100, 100, 100),
                new Color(25, 60, 120)
        );
    }
}