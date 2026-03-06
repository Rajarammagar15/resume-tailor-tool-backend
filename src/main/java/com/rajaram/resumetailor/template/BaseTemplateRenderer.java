package com.rajaram.resumetailor.template;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.rajaram.resumetailor.model.*;
import com.rajaram.resumetailor.model.Header;

import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class BaseTemplateRenderer implements TemplateRenderer {

    protected Font nameFont;
    protected Font sectionFont;
    protected Font normalFont;
    protected Font boldFont;

    protected LayoutConfig layout;

    protected abstract void configureFonts();

    protected void addDivider(Document document) throws Exception {
        LineSeparator separator = new LineSeparator();
        separator.setLineWidth(1f);
        separator.setPercentage(100);
        document.add(separator);
        document.add(Chunk.NEWLINE);
    }

    protected void addHeader(Document document, Header header) throws Exception {

        if (header == null) return;

        Font styledNameFont = new Font(
                nameFont.getFamily(),
                nameFont.getSize(),
                nameFont.getStyle(),
                layout.nameColor
        );

        Paragraph name = new Paragraph(header.getName(), styledNameFont);
        name.setAlignment(Element.ALIGN_LEFT);
        name.setSpacingAfter(4);
        document.add(name);

        String contactLine = safeJoin(
                header.getLocation(),
                header.getEmail(),
                header.getPhone(),
                header.getLinkedin(),
                header.getGithub()
        );

        Paragraph contact = new Paragraph(contactLine, normalFont);
        contact.setAlignment(Element.ALIGN_LEFT);
        contact.setSpacingAfter(8);
        document.add(contact);
    }

    protected void addSectionTitle(Document document, String title) throws Exception {

        Font styledSectionFont = new Font(
                sectionFont.getFamily(),
                sectionFont.getSize(),
                sectionFont.getStyle(),
                layout.sectionColor
        );

        Paragraph section = new Paragraph(title.toUpperCase(), styledSectionFont);
        section.setSpacingBefore(layout.sectionSpacingBefore);
        section.setSpacingAfter(layout.sectionSpacingAfter);

        document.add(section);
    }

    protected void addParagraph(Document document, String text) throws Exception {
        if (text == null || text.isBlank()) return;

        Paragraph paragraph = new Paragraph(text, normalFont);
        paragraph.setSpacingAfter(layout.paragraphSpacing);
        document.add(paragraph);
    }

    protected void addSkills(Document document, Skills skills) throws Exception {

        if (skills == null) return;

        addSkillCategory(document, "Languages", skills.getLanguages());
        addSkillCategory(document, "Backend", skills.getBackend());
        addSkillCategory(document, "Frontend", skills.getFrontend());
        addSkillCategory(document, "Databases", skills.getDatabases());
        addSkillCategory(document, "Cloud", skills.getCloud());
        addSkillCategory(document, "Tools", skills.getTools());
        addSkillCategory(document, "Concepts", skills.getConcepts());
    }

    protected void addSkillCategory(Document document,
                                    String title,
                                    java.util.List<String> items) throws Exception {

        if (items == null || items.isEmpty()) return;

        Paragraph paragraph = new Paragraph();
        paragraph.setSpacingAfter(layout.paragraphSpacing);

        paragraph.add(new Chunk(title + ": ", boldFont));
        paragraph.add(new Chunk(String.join(" | ", items), normalFont));

        document.add(paragraph);
    }

    protected void addExperience(Document document,
                                 java.util.List<Experience> experiences) throws Exception {

        if (experiences == null) return;

        for (Experience exp : experiences) {

            addExperienceHeader(document, exp);

            if (exp.getBullets() != null) {
                List list = new List(List.UNORDERED);
                list.setIndentationLeft(layout.bulletIndent);
                list.setListSymbol("• ");

                for (String bullet : exp.getBullets()) {
                    if (bullet != null && !bullet.isBlank()) {
                        list.add(new ListItem(bullet.trim(), normalFont));
                    }
                }
                document.add(list);
            }
        }
    }

    protected void addExperienceHeader(Document document, Experience exp) throws Exception {

        if (exp == null) return;

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{70, 30});
        table.setSpacingBefore(4f);
        table.setSpacingAfter(layout.experienceSpacing);

        Phrase leftPhrase = new Phrase();
        leftPhrase.add(new Chunk(safe(exp.getRole()), boldFont));

        if (exp.getCompany() != null && !exp.getCompany().isBlank()) {
            leftPhrase.add(new Chunk(" | ", normalFont));
            leftPhrase.add(new Chunk(exp.getCompany(), normalFont));
        }

        PdfPCell left = new PdfPCell(leftPhrase);
        left.setBorder(Rectangle.NO_BORDER);
        left.setPadding(0);

        Font dateFont = new Font(
                normalFont.getFamily(),
                normalFont.getSize() - 1,
                normalFont.getStyle(),
                layout.dateColor
        );

        PdfPCell right = new PdfPCell(
                new Phrase(safe(exp.getDuration()), dateFont)
        );
        right.setHorizontalAlignment(Element.ALIGN_RIGHT);
        right.setBorder(Rectangle.NO_BORDER);
        right.setPadding(0);

        table.addCell(left);
        table.addCell(right);

        document.add(table);

        if (exp.getLocation() != null && !exp.getLocation().isBlank()) {
            Paragraph location = new Paragraph(exp.getLocation(), normalFont);
            location.setSpacingAfter(3);
            document.add(location);
        }
    }

    protected void addEducation(Document document,
                                java.util.List<Education> educationList) throws Exception {

        if (educationList == null) return;

        for (Education edu : educationList) {

            Paragraph degreeInst = new Paragraph(
                    safeJoin(edu.getDegree(), edu.getInstitution()),
                    boldFont
            );
            document.add(degreeInst);

            Paragraph meta = new Paragraph(
                    safeJoin(edu.getLocation(), edu.getDuration(), edu.getGrade()),
                    normalFont
            );
            meta.setSpacingAfter(4);
            document.add(meta);
        }
    }

    protected void addCertifications(Document document,
                                     java.util.List<String> certifications) throws Exception {

        if (certifications == null || certifications.isEmpty()) return;

        List list = new List(List.UNORDERED);
        list.setIndentationLeft(16);

        for (String cert : certifications) {
            list.add(new ListItem(cert, normalFont));
        }

        document.add(list);
    }

    protected String safe(String value) {
        return value == null ? "" : value;
    }

    protected String safeJoin(String... values) {
        return Arrays.stream(values)
                .filter(v -> v != null && !v.isBlank())
                .collect(Collectors.joining(" | "));
    }
}