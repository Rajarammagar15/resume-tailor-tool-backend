package com.rajaram.resumetailor.template;

import com.lowagie.text.Document;
import com.lowagie.text.Font;

public class CorporateTemplateRenderer extends BaseTemplateRenderer {

    @Override
    protected void configureFonts() {
        nameFont      = createFont(20, Font.BOLD);
        sectionFont   = createFont(14, Font.BOLD);
        subHeaderFont = createFont(12f, Font.BOLD);
        normalFont    = createFont(10.5f, Font.NORMAL);
        boldFont      = createFont(10.5f, Font.BOLD);

        layout = LayoutConfig.corporate();
    }

//    @Override
//    protected void addTopDecoration(Document document) throws Exception {
//        addDivider(document);
//    }
}