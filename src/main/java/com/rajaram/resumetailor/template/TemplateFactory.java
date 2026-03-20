package com.rajaram.resumetailor.template;

import com.rajaram.resumetailor.model.TemplateType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TemplateFactory {
    private final Map<TemplateType, TemplateRenderer> renderers = Map.of(
            TemplateType.CORPORATE, new CorporateTemplateRenderer(),
            TemplateType.COMPACT,   new CompactTemplateRenderer(),
            TemplateType.MODERN,    new ModernTemplateRenderer()
    );

    public TemplateRenderer getRenderer(TemplateType type) {
        return renderers.get(type);
    }
}