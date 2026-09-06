package com.fw.internal.sys.base.view;

public class ViewMetrics implements IViewMetrics, IMouse {
    private static final int VIRTUAL_WIDTH = 1920;
    private static final int VIRTUAL_HEIGHT = 1080;
    private static final double SCALE_EPSILON = 0.000_001;

    private static final float CURVE_X = 0.05f;
    private static final float CURVE_Y = 0.06f;
    private static final float CRT_MARGIN = 0.04f;

    private final IFrameSize size;
    private final boolean useIntegerPhysicalScaling;

    private volatile Snapshot snapshot = new Snapshot(
            0,
            0,
            0.0,
            0.0,
            1.0,
            1.0,
            0,
            0,
            1.0,
            1.0,
            false
    );
    private volatile int virtualMouseX;
    private volatile int virtualMouseY;

    public ViewMetrics(IFrameSize size) {
        this(size, false);
    }

    public ViewMetrics(IFrameSize size, boolean useIntegerPhysicalScaling) {
        this.size = size;
        this.useIntegerPhysicalScaling = useIntegerPhysicalScaling;
    }

    public void calculateViewMetrics() {
        int windowWidth = size.getComponentWidth();
        int windowHeight = size.getComponentHeight();

        double scaleX = windowWidth / (double) VIRTUAL_WIDTH;
        double scaleY = windowHeight / (double) VIRTUAL_HEIGHT;
        double requestedScale = Math.min(scaleX, scaleY);

        double deviceScaleX = sanitizeDeviceScale(size.getDeviceScaleX());
        double deviceScaleY = sanitizeDeviceScale(size.getDeviceScaleY());
        double currentScale = requestedScale;
        boolean scaleSnapped = false;

        if (useIntegerPhysicalScaling &&
                requestedScale > 0.0 &&
                Math.abs(deviceScaleX - deviceScaleY) < SCALE_EPSILON) {
            double physicalScale = requestedScale * deviceScaleX;

            // 1배 이상의 물리 배율에서는 화면 안에 완전히 들어오는 가장 큰 정수 배율을 사용한다.
            // 1배 미만을 억지로 스냅하면 화면이 지나치게 작아지므로 원래 배율을 유지한다.
            if (physicalScale >= 1.0 - SCALE_EPSILON) {
                double integerPhysicalScale = Math.max(
                        1.0,
                        Math.ceil(physicalScale + SCALE_EPSILON)
                );
                double snappedScale = integerPhysicalScale / deviceScaleX;

                if (snappedScale <= requestedScale + SCALE_EPSILON) {
                    currentScale = snappedScale;
                    scaleSnapped =
                            Math.abs(currentScale - requestedScale) >= SCALE_EPSILON;
                }
            }
        }

        int xOffset = (int) Math.round(
                (windowWidth - VIRTUAL_WIDTH * currentScale) / 2.0
        );
        int yOffset = (int) Math.round(
                (windowHeight - VIRTUAL_HEIGHT * currentScale) / 2.0
        );

        snapshot = new Snapshot(
                windowWidth,
                windowHeight,
                scaleX,
                scaleY,
                requestedScale,
                currentScale,
                xOffset,
                yOffset,
                deviceScaleX,
                deviceScaleY,
                scaleSnapped
        );
    }

    public int getVirtualX(int mouseX) {
        Snapshot metrics = snapshot;
        return toVirtualCoordinate(
                mouseX,
                metrics.currentXOffset(),
                metrics.currentScale()
        );
    }

    public int getVirtualY(int mouseY) {
        Snapshot metrics = snapshot;
        return toVirtualCoordinate(
                mouseY,
                metrics.currentYOffset(),
                metrics.currentScale()
        );
    }

    public void updateVirtualMouse(int mouseX, int mouseY) {
        Snapshot metrics = snapshot;
        virtualMouseX = toVirtualCoordinate(
                mouseX,
                metrics.currentXOffset(),
                metrics.currentScale()
        );
        virtualMouseY = toVirtualCoordinate(
                mouseY,
                metrics.currentYOffset(),
                metrics.currentScale()
        );
    }

    public void updateVirtualMouseCrt(int mouseX, int mouseY) {
        int windowW = size.getComponentWidth();
        int windowH = size.getComponentHeight();
        if (windowW <= 0 || windowH <= 0) return;

        float drawX = windowW * CRT_MARGIN;
        float drawY = windowH * CRT_MARGIN;
        float drawW = windowW - drawX * 2.0f;
        float drawH = windowH - drawY * 2.0f;

        if (drawW <= 0 || drawH <= 0) return;

        float nx = ((mouseX - drawX) / drawW) * 2.0f - 1.0f;
        float ny = ((mouseY - drawY) / drawH) * 2.0f - 1.0f;

        /* original
        float factorX = 1.0f - (ny * ny * CURVE_X);
        float unwarpedNx = (factorX != 0.0f) ? (nx / factorX) : nx;

        float factorY = 1.0f - (unwarpedNx * unwarpedNx * CURVE_Y);
        float unwarpedNy = (factorY != 0.0f) ? (ny / factorY) : ny;

         */

        float factorY = 1.0f - (nx * nx * CURVE_Y);
        float unwarpedNy = (factorY != 0.0f) ? (ny / factorY) : ny;

        float factorX = 1.0f - (unwarpedNy * unwarpedNy * CURVE_X);
        float unwarpedNx = (factorX != 0.0f) ? (nx / factorX) : nx;

        float u = (unwarpedNx + 1.0f) * 0.5f;
        float v = (unwarpedNy + 1.0f) * 0.5f;

        virtualMouseX = Math.round(u * VIRTUAL_WIDTH);
        virtualMouseY = Math.round(v * VIRTUAL_HEIGHT);
    }

    private int toVirtualCoordinate(int coordinate, int offset, double scale) {
        if (scale <= 0.0) return coordinate;
        return (int) ((coordinate - offset) / scale);
    }

    private double sanitizeDeviceScale(double deviceScale) {
        if (!Double.isFinite(deviceScale) || deviceScale <= 0.0) {
            return 1.0;
        }
        return deviceScale;
    }

    public Snapshot getSnapshot() {
        return snapshot;
    }

    public double getRequestedScale() {
        return snapshot.requestedScale();
    }

    public double getPhysicalScale() {
        Snapshot metrics = snapshot;
        return metrics.currentScale() * metrics.deviceScaleX();
    }

    public boolean isScaleSnapped() {
        return snapshot.scaleSnapped();
    }

    @Override public int getVirtualMouseX() { return virtualMouseX; }
    @Override public int getVirtualMouseY() { return virtualMouseY; }

    @Override public int getWindowWidth() { return snapshot.windowWidth(); }
    @Override public int getWindowHeight() { return snapshot.windowHeight(); }
    @Override public double getScaleX() { return snapshot.scaleX(); }
    @Override public double getScaleY() { return snapshot.scaleY(); }
    @Override public double getCurrentScale() { return snapshot.currentScale(); }
    @Override public int getCurrentXOffset() { return snapshot.currentXOffset(); }
    @Override public int getCurrentYOffset() { return snapshot.currentYOffset(); }

    public record Snapshot(
            int windowWidth,
            int windowHeight,
            double scaleX,
            double scaleY,
            double requestedScale,
            double currentScale,
            int currentXOffset,
            int currentYOffset,
            double deviceScaleX,
            double deviceScaleY,
            boolean scaleSnapped
    ) {}
}
