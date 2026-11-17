package br.com.podedormir;
public final class ThemePaletteTest {
 static int count;
 static void ok(boolean b){count++;if(!b)throw new AssertionError("Falha na paleta ou contraste");}
 static double channel(int n){double s=n/255.0;return s<=0.04045?s/12.92:Math.pow((s+0.055)/1.055,2.4);}
 static double lum(int c){return 0.2126*channel((c>>16)&255)+0.7152*channel((c>>8)&255)+0.0722*channel(c&255);}
 static double contrast(int a,int b){double x=lum(a),y=lum(b);return (Math.max(x,y)+0.05)/(Math.min(x,y)+0.05);}
 public static void main(String[] args){
  ok(ThemePalette.isDark("escuro",false));ok(!ThemePalette.isDark("claro",true));ok(ThemePalette.isDark("sistema",true));ok(!ThemePalette.isDark("sistema",false));
  for(boolean dark:new boolean[]{true,false}){ok(contrast(ThemePalette.background(dark),ThemePalette.text(dark))>=4.5);ok(contrast(ThemePalette.background(dark),ThemePalette.hint(dark))>=4.5);ok(contrast(ThemePalette.buttonBackground(dark),ThemePalette.buttonText(dark))>=4.5);}
  System.out.println(count+" testes de tema e contraste passaram.");
 }
}
