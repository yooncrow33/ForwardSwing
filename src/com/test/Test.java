package com.test;

import com.fw.main.api.io.DynamicIoLoadObject;
import com.fw.main.*;
import com.fw.main.api.io.IoInterface;
import com.fw.main.utils.graphics.RU;
import com.fw.main.utils.graphics.RenderingOption;
import com.fw.main.utils.input.korean.TextObject;
import com.fw.main.utils.input.korean.TextObjectEventListener;
import com.fw.main.utils.input.mouse.FwMouseAPI;
import com.fw.main.utils.input.mouse.MouseInterface;
import com.fw.main.utils.io.IoUtils;
import com.fw.main.utils.platform.system.asset.AssetManager;
import com.fw.main.utils.platform.system.asset.Sound;
import com.fw.main.utils.platform.system.asset.internal.sound.kuusisto.tinysound.internal.SoundAsset;
import com.fw.main.utils.platform.system.performance.PerformanceRecorder;
import com.fw.main.utils.platform.system.scene.Bgm;
import com.fw.main.utils.platform.system.scene.Scene;
import com.fw.main.utils.platform.system.scene.Sfx;
import com.fw.main.utils.platform.system.scene.Sprite;

import java.awt.*;
import java.util.Properties;

public class Test extends Base {
    TextObject ko = new TextObject();
    float updatable;

    Sfx mouseClickSound;
    Bgm bgm;
    Sprite tex1;
    Sprite tex2;
    SoundAsset test;

    static {
        Core.setConfig(new
                Config.Builder("CraftCanvas Engine"). // = folder name.
                setWindowWidth(1280).
                setWindowHeight(720).
                setUseKoreanModule(true).
                setUseIntegerPhysicalScaling(true).
                setLoadingScreenTexture(IoUtils.getEngineResourceStream("Beisiq1.png")).
                setEncryptionKey("keyforencryption").
                setUseEncryption(false).build()
        );
    }

    public Test() {
        super(new Builder().
                setIntegerKey(1).
                setStringKey("1").
                setUseConsole(true).
                setCloseWindowWithKillVM(false).
                setPerformanceRecorderOption(PerformanceRecorder.CaptureMode.EVERY_FRAME,"test").
                setRenderingOption(RenderingOption.CRT)
        );
    }

    @Override
    public void setConsole(Base.ConsoleInit c) {
        c.registerConsoleCMD(args -> {

        });
    }

    @Override
    public void setMouse(Mouse mouse) {
        mouse.registerMouseInterface(new MouseInterface() {
            @Override
            public void mouseClicked(FwMouseAPI e) {
                //test.play();
                System.out.println("ds");
            }

            @Override
            public void mousePressed(FwMouseAPI e) {

            }

            @Override
            public void mouseReleased(FwMouseAPI e) {

            }

            @Override
            public void mouseEntered(FwMouseAPI e) {

            }

            @Override
            public void mouseExited(FwMouseAPI e) {

            }

            @Override
            public void mouseWheelMoved(FwMouseAPI e) {

            }
        });
    }

    @Override
    public void init(BaseInit init) {
        new TestBindingLegacy(this);

        ko.setFocused(true);
        ko.registerKoreanObjectEventListener(new TextObjectEventListener() {
            @Override
            public void enter() {
                System.out.println(ko.getInputText());
                ko.clear();
            }
            @Override
            public void tab() {

            }
        });

        assetManager.mallocTexturePool(3000);
        assetManager.mallocLazyLoadPool(500);

        for (int i = 0; i < 1000; i++) {
            init.getAssetInit().registerBootTexture("temp_" + i, IoUtils.getEngineResourceStream("CCE.png"));
        }

        init.getAssetInit();

        Fw.Debugger.atlasDebugger = true;

        init.setInitScene(new Scene.Builder(sceneInit -> {
            mouseClickSound = sceneInit.registerSound(IoUtils.getGameResourceStream("rr.wav"));
            bgm = sceneInit.registerMusic(IoUtils.getGameResourceStream("music.wav"),true);

            sceneInit.createAtlas("DEFAULT_ATLAS", 2, (Scene.AtlasBinderCallback) binder -> {
                tex1 = binder.registerSprite(IoUtils.getGameResourceStream("Beisiq.png"),"tex1");
                tex2 = binder.registerSprite(IoUtils.getGameResourceStream("Beisiq2.png"),"tex2");
            });

        }).setName("DEFAULT").build());


        init.getOperatorManager().exitOperatorPack.addOperator(() -> System.out.println("exit"));

        init.getIo().addIoObject("default", new IoInterface() {
            @Override
            public void save(Properties p) {
                p.setProperty("float", Float.toString(updatable));
            }

            @Override
            public void load(Properties p) {
                updatable = Float.parseFloat((String) p.get("float"));
            }

            @Override
            public void initLoad(Properties p) {
                updatable = (float) Math.random();
            }
        });

        new DynamicIoLoadObject("full path....", p -> {
            //loads...
        }).launch();
    }

    @Override
    public void update(double dt) {
        updatable = (float) Math.random();
    }

    @Override
    public void render(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(Color.WHITE);
        g.setFont(new Font("", Font.BOLD, 18));
        g.drawString("Do Test", 50, 80);

        g.setColor(Color.CYAN);
        RU.drawStringWithCursor(g, ko, 50, 130, 5, RU.CursorPosition.BOTTOM);
    }

    public static void main(String[] args) {
        new Test().launch();
    }
}