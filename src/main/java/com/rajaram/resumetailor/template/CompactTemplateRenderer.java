package com.rajaram.resumetailor.template;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.rajaram.resumetailor.model.StructuredResume;

import java.awt.*;

public class CompactTemplateRenderer extends BaseTemplateRenderer {

    @Override
    protected void configureFonts() {
        nameFont = new Font(Font.HELVETICA, 14, Font.BOLD);
        sectionFont = new Font(Font.HELVETICA, 11, Font.BOLD);
        normalFont = new Font(Font.HELVETICA, 9);
        boldFont = new Font(Font.HELVETICA, 9, Font.BOLD);

        layout = LayoutConfig.compact();
    }

    @Override
    public void render(Document document, StructuredResume resume) throws Exception {

        configureFonts();

        addHeader(document, resume.getHeader());

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