package com.rajaram.resumetailor.template;

import com.rajaram.resumetailor.model.TemplateType;
import org.springframework.stereotype.Component;

@Component
public class TemplateFactory {

    public TemplateRenderer getRenderer(TemplateType type) {

        return switch (type) {
            case CORPORATE -> new CorporateTemplateRenderer();
            case COMPACT -> new CompactTemplateRenderer();
            case MODERN -> new ModernTemplateRenderer();
        };
    }
}