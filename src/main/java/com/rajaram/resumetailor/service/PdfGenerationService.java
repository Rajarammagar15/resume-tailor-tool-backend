package com.rajaram.resumetailor.service;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import com.rajaram.resumetailor.model.StructuredResume;
import com.rajaram.resumetailor.model.TemplateType;
import com.rajaram.resumetailor.template.TemplateFactory;
import com.rajaram.resumetailor.template.TemplateRenderer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class PdfGenerationService {

    private final TemplateFactory templateFactory;

    public byte[] generatePdf(StructuredResume resume, TemplateType templateType) {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4, 40, 40, 40, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            TemplateRenderer renderer = templateFactory.getRenderer(templateType);
            renderer.render(document, resume);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
}