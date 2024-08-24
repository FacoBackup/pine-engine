package com.pine.app.view.core.window;

/**
 * Font Awesome icons codepoints, can be used to render icons with imgui in any place where text can be rendered.
 * <p>
 * To preview and search for icons use: https://fontawesome.com/icons?d=gallery&p=2&m=free
 */
public enum Icons {
    HOME("\ue88a"),
    SAVE("\ue161");

    private final String codepoint;

    Icons(String codepoint) {
        this.codepoint = codepoint;
    }

    public String getCodepoint() {
        return codepoint;
    }
}
