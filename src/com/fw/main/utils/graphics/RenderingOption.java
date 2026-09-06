package com.fw.main.utils.graphics;

public enum RenderingOption {
    DEFAULT,
    EXPERIMENTAL,
    /**
     * Designed for macOS. The DEFAULT option crashes on macOS when resized at low FPS while drawing images.
     * This option should be used for testing only; DEFAULT works perfectly on Windows.
     */
    LEGACY,
    CRT
}
