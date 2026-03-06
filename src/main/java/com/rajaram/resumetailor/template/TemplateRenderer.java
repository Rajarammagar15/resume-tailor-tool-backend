package com.rajaram.resumetailor.template;

import com.lowagie.text.Document;
import com.rajaram.resumetailor.model.StructuredResume;

public interface TemplateRenderer {
    void render(Document document, StructuredResume resume) throws Exception;
}