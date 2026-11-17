package br.com.podedormir;

/** Cores explícitas para textos e botões; a tela de alerta permanece escura. */
final class ThemePalette {
    private ThemePalette() {}
    static boolean isDark(String selected, boolean systemDark) {
        if("claro".equals(selected))return false;
        if("sistema".equals(selected))return systemDark;
        return true;
    }
    static int background(boolean dark){return dark?0xff101b38:0xfff6f8ff;}
    static int text(boolean dark){return dark?0xfff4f6ff:0xff152342;}
    static int hint(boolean dark){return dark?0xffbfc8dd:0xff495a76;}
    static int buttonBackground(boolean dark){return dark?0xffdce6ff:0xff213b70;}
    static int buttonText(boolean dark){return dark?0xff17264a:0xffffffff;}
}
