package com.rajaram.resumetailor.template;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.draw.LineSeparator;

import com.rajaram.resumetailor.model.*;
import com.rajaram.resumetailor.model.Header;
import com.rajaram.resumetailor.model.builder.ExperienceType;

import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class BaseTemplateRenderer implements TemplateRenderer {

    protected Font nameFont;
    protected Font sectionFont;
    protected Font subHeaderFont;
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
        separator.setLineWidth(1.2f);
        separator.setPercentage(100);
        document.add(separator);
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
        section.setSpacingAfter(1f);
        section.setLeading(sectionFont.getSize());
        document.add(section);

        Paragraph ruleParagraph = new Paragraph();
        ruleParagraph.setLeading(0f);
        ruleParagraph.setSpacingBefore(2.5f);
        ruleParagraph.setSpacingAfter(layout.sectionSpacingAfter-2f);

        LineSeparator rule = new LineSeparator();
        rule.setLineWidth(0.5f);
        rule.setPercentage(100);
        rule.setLineColor(layout.sectionColor);
        ruleParagraph.add(new Chunk(rule));

        document.add(ruleParagraph);
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

        Font skillLabelFont = new Font(
                subHeaderFont.getFamily(),
                subHeaderFont.getSize(),
                Font.BOLD,
                layout.sectionColor
        );

        float labelColumnWidth = 80f;

        Paragraph labelPara = new Paragraph(title + ":", skillLabelFont);
        labelPara.setSpacingAfter(2f);
        labelPara.setSpacingBefore(layout.skillSpacing-2f);
        document.add(labelPara);

        Paragraph valuePara = new Paragraph(String.join(", ", items), normalFont);
        valuePara.setIndentationLeft(labelColumnWidth);
        valuePara.setSpacingBefore(-normalFont.getSize() - 7f);
        valuePara.setSpacingAfter(layout.skillSpacing+2f);
        document.add(valuePara);
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

        Font experienceFont = new Font(
                subHeaderFont.getFamily(),
                subHeaderFont.getSize(),
                Font.BOLD,
                layout.sectionColor
        );
        Font dateFont = new Font(
                normalFont.getFamily(),
                normalFont.getSize() - 1,
                normalFont.getStyle(),
                layout.dateColor
        );

        Paragraph roleLine = new Paragraph();
        roleLine.setSpacingBefore(0f);
        roleLine.setSpacingAfter(0f);
        roleLine.setIndentationRight(95f);
        roleLine.add(new Chunk(safe(exp.getRole()), experienceFont));

        if (exp.getCompany() != null && !exp.getCompany().isBlank())
            roleLine.add(new Chunk(" | " + exp.getCompany(), experienceFont));

        if (exp.getLocation() != null && !exp.getLocation().isBlank())
            roleLine.add(new Chunk(" | " + exp.getLocation(), normalFont));

        document.add(roleLine);

        Paragraph dateLine = new Paragraph(safe(exp.getDuration()), dateFont);
        dateLine.setAlignment(Element.ALIGN_RIGHT);
        dateLine.setSpacingBefore(-normalFont.getSize() - 2.5f);
        dateLine.setSpacingAfter(1f);
        document.add(dateLine);
    }

    protected void addEducation(Document document,
                                java.util.List<Education> educationList) throws Exception {

        if (educationList == null) return;

        for (int i = 0; i < educationList.size(); i++) {

            Education edu = educationList.get(i);

            String degreeLine;
            if (edu.getSpecialization() != null && !edu.getSpecialization().isBlank()) {
                degreeLine = edu.getDegree() + " in " + edu.getSpecialization();
            } else {
                degreeLine = edu.getDegree();
            }

            Font educationSubHeaderFont = new Font(
                    subHeaderFont.getFamily(),
                    subHeaderFont.getSize(),
                    Font.BOLD,
                    layout.sectionColor
            );
            Font dateFont = new Font(
                    normalFont.getFamily(),
                    normalFont.getSize() - 1,
                    normalFont.getStyle(),
                    layout.dateColor
            );

            // Degree + Institution line (left)
            Paragraph degreeParagraph = new Paragraph();
            degreeParagraph.setSpacingBefore(0f);
            degreeParagraph.setSpacingAfter(0f);
            degreeParagraph.setIndentationRight(95f);
            degreeParagraph.add(new Chunk(safeJoin(degreeLine, edu.getInstitution()), educationSubHeaderFont));
            document.add(degreeParagraph);

            // Date pulled up to same visual line (right-aligned)
            Paragraph dateLine = new Paragraph(safe(edu.getDuration()), dateFont);
            dateLine.setAlignment(Element.ALIGN_RIGHT);
            dateLine.setSpacingBefore(-normalFont.getSize() - 2.5f);
            dateLine.setSpacingAfter(1f);
            document.add(dateLine);

            // Location + GPA meta line
            String gradeDisplay = edu.getGrade() == null ? null
                    : (edu.getGrade().toLowerCase().contains("gpa")
                    ? edu.getGrade()
                    : "GPA: " + edu.getGrade());

            Paragraph meta = new Paragraph(
                    safeJoin(edu.getLocation(), gradeDisplay),
                    normalFont
            );
            meta.setSpacingAfter(i < educationList.size() - 1 ? 8f : 2f);
            document.add(meta);
        }
    }

    protected void addProjects(Document document,
                               java.util.List<Project> projects) throws Exception {

        if (projects == null || projects.isEmpty()) return;

        int count = 0;
        for (Project project : projects) {

            if (project == null) continue;

            Font projectTitleFont = new Font(
                    subHeaderFont.getFamily(),
                    subHeaderFont.getSize(),
                    Font.BOLD,
                    layout.sectionColor
            );
            Paragraph projectTitle = new Paragraph(safe(project.getName()), projectTitleFont);

            if (count > 0) projectTitle.setSpacingBefore(3f);
            else projectTitle.setSpacingBefore(-1.5f);

            projectTitle.setSpacingAfter(1f);
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

            count++;
        }
    }

    protected void addCertifications(Document document,
                                     java.util.List<String> certifications) throws Exception {

        if (certifications == null || certifications.isEmpty()) return;

        List list = new List(List.UNORDERED);
        list.setIndentationLeft(layout.bulletIndent);
        list.setListSymbol("• ");

        for (String cert : certifications) {
            String cleaned = cert.replaceAll("^[-–•\\s]+", "").trim();
            list.add(new ListItem(cleaned, normalFont));
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

    //helper method to setting fonts:
    protected Font createFont(float size, int style) {
        try {
            String fontPath = style == Font.BOLD || style == Font.BOLDITALIC
                    ? "/fonts/OpenSans-Bold.ttf"
                    : "/fonts/OpenSans-Regular.ttf";

            BaseFont bf = BaseFont.createFont(
                    fontPath,
                    BaseFont.WINANSI,
                    BaseFont.EMBEDDED
            );
            return new Font(bf, size, style);
        } catch (Exception e) {

            return new Font(Font.HELVETICA, size, style);
        }
    }
}