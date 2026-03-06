package com.rajaram.resumetailor.template;

import com.lowagie.text.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.rajaram.resumetailor.model.StructuredResume;

import java.awt.Color;

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
    public void render(Document document, StructuredResume resume) throws Exception {

        configureFonts();

        addHeader(document, resume.getHeader());

        if (layout.showDivider) {
            LineSeparator separator = new LineSeparator();
            separator.setLineWidth(1.2f);
            separator.setPercentage(100);
            separator.setLineColor(layout.dividerColor);
            document.add(separator);
        }

        addSectionTitle(document, "Professional Summary");
        addParagraph(document, resume.getSummary());

        addSectionTitle(document, "Skills");
        addSkills(document, resume.getSkills());

        addSectionTitle(document, "Experience");
        addExperience(document, resume.getExperience());

        addSectionTitle(document, "Education");
        addEducation(document, resume.getEducation());

        if (resume.getCertifications() != null && !resume.getCertifications().isEmpty()) {
            addSectionTitle(document, "Certifications");
            addCertifications(document, resume.getCertifications());
        }
    }
}