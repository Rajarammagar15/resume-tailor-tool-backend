package com.rajaram.resumetailor.template;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.draw.LineSeparator;

public class ModernTemplateRenderer extends BaseTemplateRenderer {

    @Override
    protected void configureFonts() {
        nameFont      = createFont(20, Font.BOLD);
        sectionFont   = createFont(14, Font.BOLD);
        subHeaderFont = createFont(12f, Font.BOLD);
        normalFont    = createFont(10.5f, Font.NORMAL);
        boldFont      = createFont(10.5f, Font.BOLD);

        layout = LayoutConfig.modern();
    }

//    @Override
//    protected void addTopDecoration(Document document) throws Exception {
//
//        if (layout.showDivider) {
//            LineSeparator separator = new LineSeparator();
//            separator.setLineWidth(1.2f);
//            separator.setPercentage(100);
//            separator.setLineColor(layout.dividerColor);
//            document.add(separator);
//        }
//    }
}