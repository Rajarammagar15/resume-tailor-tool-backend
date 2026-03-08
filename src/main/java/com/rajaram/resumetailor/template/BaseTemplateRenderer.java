package com.rajaram.resumetailor.template;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.rajaram.resumetailor.model.*;
import com.rajaram.resumetailor.model.Header;
import com.rajaram.resumetailor.model.builder.ExperienceType;

import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class BaseTemplateRenderer implements TemplateRenderer {

    protected Font nameFont;
    protected Font sectionFont;
    protected Font normalFont;
    protected Font boldFont;

    protected LayoutConfig layout;

    protected abstract void configureFonts();

    @Override
    public final void render(Document document, StructuredResume resume) throws Exception {

        configureFonts();

        addHeader(document, resume.getHeader());
        addTopDecoration(document);

        java.util.List<ResumeSection> order = determineSectionOrder(resume);

        for (ResumeSection section : order) {
            switch (section) {
                case SUMMARY -> addSummarySection(document, resume);
                case SKILLS -> addSkillsSection(document, resume);
                case EXPERIENCE -> addExperienceSections(document, resume);
                case INTERNSHIPS -> addInternshipSection(document, resume);
                case PROJECTS -> addProjectsSection(document, resume);
                case EDUCATION -> addEducationSection(document, resume);
                case CERTIFICATIONS -> addCertificationsSection(document, resume);
            }
        }
    }

    protected void addTopDecoration(Document document) throws Exception {
        // default no decoration
    }

    protected void addSummarySection(Document document, StructuredResume resume) throws Exception {

        if (resume.getSummary() == null || resume.getSummary().isBlank()) return;

        addSectionTitle(document, "Professional Summary");
        addParagraph(document, resume.getSummary());
    }

    protected void addSkillsSection(Document document, StructuredResume resume) throws Exception {

        if (resume.getSkills() == null) return;

        addSectionTitle(document, "Skills");
        addSkills(document, resume.getSkills());
    }

    protected void addEducationSection(Document document, StructuredResume resume) throws Exception {

        if (resume.getEducation() == null || resume.getEducation().isEmpty()) return;

        addSectionTitle(document, "Education");
        addEducation(document, resume.getEducation());
    }

    protected void addExperienceSections(Document document,
                                         StructuredResume resume) throws Exception {

        if (resume.getExperience() == null) return;

        java.util.List<Experience> fullTime = resume.getExperience()
                .stream()
                .filter(e -> e.getType() == ExperienceType.FULL_TIME)
                .toList();

        if (fullTime.isEmpty()) return;

        addSectionTitle(document, "Experience");
        addExperience(document, fullTime);
    }

    protected void addInternshipSection(Document document,
                                        StructuredResume resume) throws Exception {

        if (resume.getExperience() == null) return;

        java.util.List<Experience> internships = resume.getExperience()
                .stream()
                .filter(e -> e.getType() == ExperienceType.INTERNSHIP)
                .toList();

        if (internships.isEmpty()) return;

        addSectionTitle(document, "Internships");
        addExperience(document, internships);
    }

    protected void addProjectsSection(Document document, StructuredResume resume) throws Exception {

        if (resume.getProjects() == null || resume.getProjects().isEmpty()) return;

        addSectionTitle(document, "Projects");
        addProjects(document, resume.getProjects());
    }

    protected void addCertificationsSection(Document document, StructuredResume resume) throws Exception {

        if (resume.getCertifications() == null || resume.getCertifications().isEmpty()) return;

        addSectionTitle(document, "Certifications");
        addCertifications(document, resume.getCertifications());
    }

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

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{70, 30});
            table.setSpacingBefore(4f);
            table.setSpacingAfter(layout.experienceSpacing);

            Phrase leftPhrase = new Phrase();
            leftPhrase.add(new Chunk(safeJoin(edu.getDegree(), edu.getInstitution()), boldFont));

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
                    new Phrase(safe(edu.getDuration()), dateFont)
            );
            right.setHorizontalAlignment(Element.ALIGN_RIGHT);
            right.setBorder(Rectangle.NO_BORDER);
            right.setPadding(0);

            table.addCell(left);
            table.addCell(right);

            document.add(table);

            Paragraph meta = new Paragraph(
                    safeJoin(edu.getLocation(), "GPA: " + edu.getGrade()),
                    normalFont
            );
            meta.setSpacingAfter(4);
            document.add(meta);
        }
    }

    protected void addProjects(Document document,
                               java.util.List<Project> projects) throws Exception {

        if (projects == null || projects.isEmpty()) return;

        for (Project project : projects) {

            if (project == null) continue;

            Paragraph projectTitle = new Paragraph(
                    safe(project.getName()),
                    boldFont
            );
            projectTitle.setSpacingBefore(4);
            document.add(projectTitle);

            if (project.getBullets() != null && !project.getBullets().isEmpty()) {

                List list = new List(List.UNORDERED);
                list.setIndentationLeft(layout.bulletIndent);
                list.setListSymbol("• ");

                for (String bullet : project.getBullets()) {
                    if (bullet != null && !bullet.isBlank()) {
                        list.add(new ListItem(bullet.trim(), normalFont));
                    }
                }

                document.add(list);
            }
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

    protected java.util.List<ResumeSection> determineSectionOrder(StructuredResume resume) {

        boolean hasExperience = resume.getExperience() != null &&
                resume.getExperience().stream()
                        .anyMatch(e -> e.getType() == ExperienceType.FULL_TIME);

        if (hasExperience) {
            return java.util.List.of(
                    ResumeSection.SUMMARY,
                    ResumeSection.SKILLS,
                    ResumeSection.EXPERIENCE,
                    ResumeSection.PROJECTS,
                    ResumeSection.EDUCATION,
                    ResumeSection.CERTIFICATIONS
            );
        }

        // Fresher layout
        return java.util.List.of(
                ResumeSection.SUMMARY,
                ResumeSection.SKILLS,
                ResumeSection.PROJECTS,
                ResumeSection.INTERNSHIPS,
                ResumeSection.EDUCATION,
                ResumeSection.CERTIFICATIONS
        );
    }
}