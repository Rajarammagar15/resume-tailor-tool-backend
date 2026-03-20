package com.rajaram.resumetailor.template;

import com.lowagie.text.Font;

public class CompactTemplateRenderer extends BaseTemplateRenderer {

    @Override
    protected void configureFonts() {
        nameFont      = createFont(18, Font.BOLD);
        sectionFont   = createFont(12, Font.BOLD);
        subHeaderFont = createFont(11f, Font.BOLD);
        normalFont    = createFont(9.5f, Font.NORMAL);
        boldFont      = createFont(9.5f, Font.BOLD);

        layout = LayoutConfig.compact();
    }
}