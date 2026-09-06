package com.fw.main.utils.input;

import com.fw.main.utils.input.korean.TextManager;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public abstract class KeyBindingBase {

    public KeyBindingBaseOption keyBindingBaseOption;

    public void registerKeyBindingBaseOption(KeyBindingBaseOption keyBindingBaseOption) {
        this.keyBindingBaseOption = keyBindingBaseOption;
    }

    public enum KBKey {
        // A-Z
        A(KeyEvent.VK_A), B(KeyEvent.VK_B), C(KeyEvent.VK_C), D(KeyEvent.VK_D),
        E(KeyEvent.VK_E), F(KeyEvent.VK_F), G(KeyEvent.VK_G), H(KeyEvent.VK_H),
        I(KeyEvent.VK_I), J(KeyEvent.VK_J), K(KeyEvent.VK_K), L(KeyEvent.VK_L),
        M(KeyEvent.VK_M), N(KeyEvent.VK_N), O(KeyEvent.VK_O), P(KeyEvent.VK_P),
        Q(KeyEvent.VK_Q), R(KeyEvent.VK_R), S(KeyEvent.VK_S), T(KeyEvent.VK_T),
        U(KeyEvent.VK_U), V(KeyEvent.VK_V), W(KeyEvent.VK_W), X(KeyEvent.VK_X),
        Y(KeyEvent.VK_Y), Z(KeyEvent.VK_Z),

        // 0-9
        NUM0(KeyEvent.VK_0), NUM1(KeyEvent.VK_1), NUM2(KeyEvent.VK_2),
        NUM3(KeyEvent.VK_3), NUM4(KeyEvent.VK_4), NUM5(KeyEvent.VK_5),
        NUM6(KeyEvent.VK_6), NUM7(KeyEvent.VK_7), NUM8(KeyEvent.VK_8),
        NUM9(KeyEvent.VK_9),

        // F keys
        F1(KeyEvent.VK_F1), F2(KeyEvent.VK_F2), F3(KeyEvent.VK_F3),
        F4(KeyEvent.VK_F4), F5(KeyEvent.VK_F5), F6(KeyEvent.VK_F6),
        F7(KeyEvent.VK_F7), F8(KeyEvent.VK_F8), F9(KeyEvent.VK_F9),
        F10(KeyEvent.VK_F10), F11(KeyEvent.VK_F11), F12(KeyEvent.VK_F12),

        // Control
        SHIFT(KeyEvent.VK_SHIFT),
        CTRL(KeyEvent.VK_CONTROL),
        ALT(KeyEvent.VK_ALT),
        SPACE(KeyEvent.VK_SPACE),
        TAB(KeyEvent.VK_TAB),
        ENTER(KeyEvent.VK_ENTER),
        ESC(KeyEvent.VK_ESCAPE),
        BACKSPACE(KeyEvent.VK_BACK_SPACE),

        // Arrows
        UP(KeyEvent.VK_UP),
        DOWN(KeyEvent.VK_DOWN),
        LEFT(KeyEvent.VK_LEFT),
        RIGHT(KeyEvent.VK_RIGHT);

        public final int code;
        KBKey(int code) { this.code = code; }
    }

    private final KBKey[] keyTable = new KBKey[65536];

    protected KeyBindingBase(Canvas comp) {
        comp.setFocusable(true);

        for (KBKey k : KBKey.values()) {
            if (k.code >= 0 && k.code < keyTable.length) {
                keyTable[k.code] = k;
            }
        }

        comp.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (code >= 0 && code < keyTable.length) {
                    KBKey k = keyTable[code];
                    if (k != null) {
                        dispatchPress(k);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int code = e.getKeyCode();
                if (code >= 0 && code < keyTable.length) {
                    KBKey k = keyTable[code];
                    if (k != null) {
                        dispatchRelease(k);
                    }
                }
            }
        });
    }

    private void dispatchPress(KBKey k) {
        if (keyBindingBaseOption != null && !keyBindingBaseOption.isOn()) {
            return;
        }
        if (!TextManager.isActiveKoreanObjectIsEmpty()) {
            return;
        }
        switch (k) {
            case A -> onKeyAPress();
            case B -> onKeyBPress();
            case C -> onKeyCPress();
            case D -> onKeyDPress();
            case E -> onKeyEPress();
            case F -> onKeyFPress();
            case G -> onKeyGPress();
            case H -> onKeyHPress();
            case I -> onKeyIPress();
            case J -> onKeyJPress();
            case K -> onKeyKPress();
            case L -> onKeyLPress();
            case M -> onKeyMPress();
            case N -> onKeyNPress();
            case O -> onKeyOPress();
            case P -> onKeyPPress();
            case Q -> onKeyQPress();
            case R -> onKeyRPress();
            case S -> onKeySPress();
            case T -> onKeyTPress();
            case U -> onKeyUPress();
            case V -> onKeyVPress();
            case W -> onKeyWPress();
            case X -> onKeyXPress();
            case Y -> onKeyYPress();
            case Z -> onKeyZPress();

            case NUM0 -> onKey0Press();
            case NUM1 -> onKey1Press();
            case NUM2 -> onKey2Press();
            case NUM3 -> onKey3Press();
            case NUM4 -> onKey4Press();
            case NUM5 -> onKey5Press();
            case NUM6 -> onKey6Press();
            case NUM7 -> onKey7Press();
            case NUM8 -> onKey8Press();
            case NUM9 -> onKey9Press();

            case F1 -> onKeyF1Press();
            case F2 -> onKeyF2Press();
            case F3 -> onKeyF3Press();
            case F4 -> onKeyF4Press();
            case F5 -> onKeyF5Press();
            case F6 -> onKeyF6Press();
            case F7 -> onKeyF7Press();
            case F8 -> onKeyF8Press();
            case F9 -> onKeyF9Press();
            case F10 -> onKeyF10Press();
            case F11 -> onKeyF11Press();
            case F12 -> onKeyF12Press();

            case SHIFT -> onKeyShiftPress();
            case CTRL -> onKeyCtrlPress();
            case ALT -> onKeyAltPress();
            case SPACE -> onKeySpacePress();
            case ENTER -> onKeyEnterPress();
            case ESC -> onKeyEscPress();
            case TAB -> onKeyTabPress();
            case BACKSPACE -> onKeyBackspacePress();

            case UP -> onKeyUpPress();
            case DOWN -> onKeyDownPress();
            case LEFT -> onKeyLeftPress();
            case RIGHT -> onKeyRightPress();
        }
    }

    private void dispatchRelease(KBKey k) {
        if (keyBindingBaseOption != null && !keyBindingBaseOption.isOn()) {
            return;
        }
        if (!TextManager.isActiveKoreanObjectIsEmpty()) {
            return;
        }
        switch (k) {
            case A -> onKeyARelease();
            case B -> onKeyBRelease();
            case C -> onKeyCRelease();
            case D -> onKeyDRelease();
            case E -> onKeyERelease();
            case F -> onKeyFRelease();
            case G -> onKeyGRelease();
            case H -> onKeyHRelease();
            case I -> onKeyIRelease();
            case J -> onKeyJRelease();
            case K -> onKeyKRelease();
            case L -> onKeyLRelease();
            case M -> onKeyMRelease();
            case N -> onKeyNRelease();
            case O -> onKeyORelease();
            case P -> onKeyPRelease();
            case Q -> onKeyQRelease();
            case R -> onKeyRRelease();
            case S -> onKeySRelease();
            case T -> onKeyTRelease();
            case U -> onKeyURelease();
            case V -> onKeyVRelease();
            case W -> onKeyWRelease();
            case X -> onKeyXRelease();
            case Y -> onKeyYRelease();
            case Z -> onKeyZRelease();

            case NUM0 -> onKey0Release();
            case NUM1 -> onKey1Release();
            case NUM2 -> onKey2Release();
            case NUM3 -> onKey3Release();
            case NUM4 -> onKey4Release();
            case NUM5 -> onKey5Release();
            case NUM6 -> onKey6Release();
            case NUM7 -> onKey7Release();
            case NUM8 -> onKey8Release();
            case NUM9 -> onKey9Release();

            case F1 -> onKeyF1Release();
            case F2 -> onKeyF2Release();
            case F3 -> onKeyF3Release();
            case F4 -> onKeyF4Release();
            case F5 -> onKeyF5Release();
            case F6 -> onKeyF6Release();
            case F7 -> onKeyF7Release();
            case F8 -> onKeyF8Release();
            case F9 -> onKeyF9Release();
            case F10 -> onKeyF10Release();
            case F11 -> onKeyF11Release();
            case F12 -> onKeyF12Release();

            case SHIFT -> onKeyShiftRelease();
            case CTRL -> onKeyCtrlRelease();
            case ALT -> onKeyAltRelease();
            case SPACE -> onKeySpaceRelease();
            case ENTER -> onKeyEnterRelease();
            case ESC -> onKeyEscRelease();
            case TAB -> onKeyTabRelease();
            case BACKSPACE -> onKeyBackspaceRelease();

            case UP -> onKeyUpRelease();
            case DOWN -> onKeyDownRelease();
            case LEFT -> onKeyLeftRelease();
            case RIGHT -> onKeyRightRelease();
        }
    }

    // ===== PRESS =====
    protected void onKeyAPress() {}
    protected void onKeyBPress() {}
    protected void onKeyCPress() {}
    protected void onKeyDPress() {}
    protected void onKeyEPress() {}
    protected void onKeyFPress() {}
    protected void onKeyGPress() {}
    protected void onKeyHPress() {}
    protected void onKeyIPress() {}
    protected void onKeyJPress() {}
    protected void onKeyKPress() {}
    protected void onKeyLPress() {}
    protected void onKeyMPress() {}
    protected void onKeyNPress() {}
    protected void onKeyOPress() {}
    protected void onKeyPPress() {}
    protected void onKeyQPress() {}
    protected void onKeyRPress() {}
    protected void onKeySPress() {}
    protected void onKeyTPress() {}
    protected void onKeyUPress() {}
    protected void onKeyVPress() {}
    protected void onKeyWPress() {}
    protected void onKeyXPress() {}
    protected void onKeyYPress() {}
    protected void onKeyZPress() {}

    protected void onKey0Press() {}
    protected void onKey1Press() {}
    protected void onKey2Press() {}
    protected void onKey3Press() {}
    protected void onKey4Press() {}
    protected void onKey5Press() {}
    protected void onKey6Press() {}
    protected void onKey7Press() {}
    protected void onKey8Press() {}
    protected void onKey9Press() {}

    protected void onKeyF1Press() {}
    protected void onKeyF2Press() {}
    protected void onKeyF3Press() {}
    protected void onKeyF4Press() {}
    protected void onKeyF5Press() {}
    protected void onKeyF6Press() {}
    protected void onKeyF7Press() {}
    protected void onKeyF8Press() {}
    protected void onKeyF9Press() {}
    protected void onKeyF10Press() {}
    protected void onKeyF11Press() {}
    protected void onKeyF12Press() {}

    protected void onKeyShiftPress() {}
    protected void onKeyCtrlPress() {}
    protected void onKeyAltPress() {}
    protected void onKeySpacePress() {}
    protected void onKeyEnterPress() {}
    protected void onKeyEscPress() {}
    protected void onKeyTabPress() {}
    protected void onKeyBackspacePress() {}

    protected void onKeyUpPress() {}
    protected void onKeyDownPress() {}
    protected void onKeyLeftPress() {}
    protected void onKeyRightPress() {}

    // ===== RELEASE =====
    protected void onKeyARelease() {}
    protected void onKeyBRelease() {}
    protected void onKeyCRelease() {}
    protected void onKeyDRelease() {}
    protected void onKeyERelease() {}
    protected void onKeyFRelease() {}
    protected void onKeyGRelease() {}
    protected void onKeyHRelease() {}
    protected void onKeyIRelease() {}
    protected void onKeyJRelease() {}
    protected void onKeyKRelease() {}
    protected void onKeyLRelease() {}
    protected void onKeyMRelease() {}
    protected void onKeyNRelease() {}
    protected void onKeyORelease() {}
    protected void onKeyPRelease() {}
    protected void onKeyQRelease() {}
    protected void onKeyRRelease() {}
    protected void onKeySRelease() {}
    protected void onKeyTRelease() {}
    protected void onKeyURelease() {}
    protected void onKeyVRelease() {}
    protected void onKeyWRelease() {}
    protected void onKeyXRelease() {}
    protected void onKeyYRelease() {}
    protected void onKeyZRelease() {}

    protected void onKey0Release() {}
    protected void onKey1Release() {}
    protected void onKey2Release() {}
    protected void onKey3Release() {}
    protected void onKey4Release() {}
    protected void onKey5Release() {}
    protected void onKey6Release() {}
    protected void onKey7Release() {}
    protected void onKey8Release() {}
    protected void onKey9Release() {}

    protected void onKeyF1Release() {}
    protected void onKeyF2Release() {}
    protected void onKeyF3Release() {}
    protected void onKeyF4Release() {}
    protected void onKeyF5Release() {}
    protected void onKeyF6Release() {}
    protected void onKeyF7Release() {}
    protected void onKeyF8Release() {}
    protected void onKeyF9Release() {}
    protected void onKeyF10Release() {}
    protected void onKeyF11Release() {}
    protected void onKeyF12Release() {}

    protected void onKeyShiftRelease() {}
    protected void onKeyCtrlRelease() {}
    protected void onKeyAltRelease() {}
    protected void onKeySpaceRelease() {}
    protected void onKeyEnterRelease() {}
    protected void onKeyEscRelease() {}
    protected void onKeyTabRelease() {}
    protected void onKeyBackspaceRelease() {}

    protected void onKeyUpRelease() {}
    protected void onKeyDownRelease() {}
    protected void onKeyLeftRelease() {}
    protected void onKeyRightRelease() {}
}