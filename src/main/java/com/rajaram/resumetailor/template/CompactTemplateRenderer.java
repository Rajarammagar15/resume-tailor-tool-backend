package com.rajaram.resumetailor.template;

import com.lowagie.text.Font;

public class CompactTemplateRenderer extends BaseTemplateRenderer {

    @Override
    protected void configureFonts() {
        nameFont = new Font(Font.HELVETICA, 14, Font.BOLD);
        sectionFont = new Font(Font.HELVETICA, 11, Font.BOLD);
        normalFont = new Font(Font.HELVETICA, 9);
        boldFont = new Font(Font.HELVETICA, 9, Font.BOLD);

        layout = LayoutConfig.compact();
    }
}