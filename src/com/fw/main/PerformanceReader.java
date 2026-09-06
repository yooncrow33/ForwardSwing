package com.fw.main;

import com.fw.main.utils.graphics.RenderingOption;
import com.fw.main.utils.input.mouse.FwMouseAPI;
import com.fw.main.utils.input.mouse.MouseInterface;
import com.fw.main.utils.platform.system.scene.Scene;
import com.fw.main.utils.platform.system.performance.GraphTab;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

public class PerformanceReader extends Base {
    private final List<GraphTab> tabList = new ArrayList<>();
    private int currentTab = 0;

    private static final int GRAPH_X = 50;
    private static final int GRAPH_Y = 85;
    private static final int GRAPH_WIDTH = 1500;
    private static final int GRAPH_HEIGHT = 740;

    static {
        Core.setConfig(new Config.Builder("Performance Data.")
                .setWindowWidth(1600)
                .setWindowHeight(900)
                .setUseKoreanModule(true)
                .setUseIntegerPhysicalScaling(true)
                .setEncryptionKey("keyforencryption")
                .setUseEncryption(false)
                .build()
        );
    }

    public PerformanceReader() {
        super(new Builder()
                .setIntegerKey(1)
                .setStringKey("1")
                .setCrtJitterEnabled(true)
                .setUseConsole(true)
                .setRenderingOption(RenderingOption.CRT)
        );
    }

    public static class ProfileDataResult {
        public final Map<String, Long[]> dataMap = new LinkedHashMap<>();
        public final Map<Integer, String> phaseMap = new TreeMap<>();
    }

    public static ProfileDataResult loadProfileFromStream(InputStream in) throws IOException {
        ProfileDataResult result = new ProfileDataResult();
        Properties prop = new Properties();
        prop.load(in);

        String phaseStr = prop.getProperty("phases", "");
        if (!phaseStr.isEmpty()) {
            String[] entries = phaseStr.split(";");
            for (String entry : entries) {
                String[] kv = entry.split(":");
                if (kv.length == 2) {
                    result.phaseMap.put(Integer.parseInt(kv[0]), kv[1]);
                }
            }
        }

        for (String key : prop.stringPropertyNames()) {
            if (key.startsWith("data.")) {
                String metricName = key.substring(5);
                String rawValues = prop.getProperty(key);
                if (rawValues == null || rawValues.isEmpty()) {
                    result.dataMap.put(metricName, new Long[0]);
                    continue;
                }

                String[] tokens = rawValues.split(",");
                Long[] longArray = new Long[tokens.length];
                for (int i = 0; i < tokens.length; i++) {
                    longArray[i] = Long.parseLong(tokens[i].trim());
                }
                result.dataMap.put(metricName, longArray);
            }
        }
        return result;
    }

    @Override
    public void init(BaseInit init) {
        assetManager.mallocTexturePool(10);
        assetManager.mallocLazyLoadPool(10);

        init.setInitScene(new Scene.Builder(sceneInit -> {}).setName("DEFAULT").build());

        FileDialog dialog = new FileDialog((Frame) null, "select data(.fwD)", FileDialog.LOAD);
        dialog.setFile("*.fwD");
        dialog.setVisible(true);

        String dir = dialog.getDirectory();
        String file = dialog.getFile();

        if (dir != null && file != null) {
            File selectedFile = new File(dir, file);
            try (InputStream in = new FileInputStream(selectedFile)) {
                ProfileDataResult profile = loadProfileFromStream(in);

                for (Map.Entry<String, Long[]> entry : profile.dataMap.entrySet()) {
                    tabList.add(new GraphTab(entry.getKey(), entry.getValue(), profile.phaseMap));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!tabList.isEmpty()) {
                    int virtualX = (int) (e.getX() / getViewScale());
                    tabList.get(currentTab).onMouseDragged(virtualX, GRAPH_WIDTH);
                }
            }
        });
    }

    @Override
    public void setMouse(Mouse mouse) {
        mouse.registerMouseInterface(new MouseInterface() {
            @Override
            public void mouseClicked(FwMouseAPI e) {
                int vx = getMouseX();
                int vy = getMouseY();

                int startX = GRAPH_X;
                for (int i = 0; i < tabList.size(); i++) {
                    if (vx >= startX && vx <= startX + 180 && vy >= 35 && vy <= 70) {
                        currentTab = i;
                        return;
                    }
                    startX += 190;
                }

                if (!tabList.isEmpty()) {
                    tabList.get(currentTab).onSubTabClick(vx, vy, GRAPH_X, GRAPH_Y, GRAPH_HEIGHT);
                }
            }

            @Override
            public void mousePressed(FwMouseAPI e) {
                if (!tabList.isEmpty()) {
                    tabList.get(currentTab).onMousePressed(getMouseX());
                }
            }

            @Override
            public void mouseReleased(FwMouseAPI e) {
                if (!tabList.isEmpty()) {
                    tabList.get(currentTab).onMouseReleased();
                }
            }

            @Override public void mouseEntered(FwMouseAPI e) {}
            @Override public void mouseExited(FwMouseAPI e) {}

            @Override
            public void mouseWheelMoved(FwMouseAPI e) {
                if (!tabList.isEmpty()) {
                    tabList.get(currentTab).onMouseWheel(e, getMouseX(), GRAPH_WIDTH);
                }
            }
        });
    }

    @Override
    public void update(double dt) {}

    @Override
    public void render(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int tabX = GRAPH_X;
        for (int i = 0; i < tabList.size(); i++) {
            boolean isSelected = (currentTab == i);
            g.setColor(isSelected ? new Color(65, 75, 105) : new Color(35, 36, 45));
            g.fillRect(tabX, 35, 180, 35);
            g.setColor(isSelected ? Color.CYAN : Color.LIGHT_GRAY);
            g.drawRect(tabX, 35, 180, 35);
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            g.drawString(tabList.get(i).getTitle(), tabX + 10, 57);
            tabX += 190;
        }

        if (!tabList.isEmpty()) {
            tabList.get(currentTab).render(g, GRAPH_X, GRAPH_Y, GRAPH_WIDTH, GRAPH_HEIGHT);
        } else {
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.PLAIN, 16));
            g.drawString("data was not selected.", GRAPH_X + 20, GRAPH_Y + 40);
        }
    }


    public static void main(String[] args) {
        new PerformanceReader().launch();
    }
}