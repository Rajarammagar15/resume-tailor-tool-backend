package com.rajaram.resumetailor.template;

import com.lowagie.text.Document;
import com.lowagie.text.Font;

public class CorporateTemplateRenderer extends BaseTemplateRenderer {

    @Override
    protected void configureFonts() {
        nameFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        sectionFont = new Font(Font.HELVETICA, 13, Font.BOLD);
        normalFont = new Font(Font.HELVETICA, 11);
        boldFont = new Font(Font.HELVETICA, 11, Font.BOLD);

        layout = LayoutConfig.corporate();
    }

    @Override
    protected void addTopDecoration(Document document) throws Exception {
        addDivider(document);
    }
}