package com.rajaram.resumetailor.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.rajaram.resumetailor.model.*;

import com.rajaram.resumetailor.model.Header;
import com.rajaram.resumetailor.template.TemplateFactory;
import com.rajaram.resumetailor.template.TemplateRenderer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class PdfGenerationService {

    private final TemplateFactory templateFactory;

    public byte[] generatePdf(StructuredResume resume, TemplateType templateType) {

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
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