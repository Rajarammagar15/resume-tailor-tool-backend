package com.rajaram.resumetailor.template;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.draw.LineSeparator;

public class ModernTemplateRenderer extends BaseTemplateRenderer {

    @Override
    protected void configureFonts() {
        nameFont = new Font(Font.HELVETICA, 18, Font.BOLD);
        sectionFont = new Font(Font.HELVETICA, 13, Font.BOLD);
        normalFont = new Font(Font.HELVETICA, 11);
        boldFont = new Font(Font.HELVETICA, 11, Font.BOLD);

        layout = LayoutConfig.modern();
    }

    @Override
    protected void addTopDecoration(Document document) throws Exception {

        if (layout.showDivider) {
            LineSeparator separator = new LineSeparator();
            separator.setLineWidth(1.2f);
            separator.setPercentage(100);
            separator.setLineColor(layout.dividerColor);
            document.add(separator);
        }
    }
}