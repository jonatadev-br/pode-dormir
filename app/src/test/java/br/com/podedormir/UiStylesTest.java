package br.com.podedormir;
import org.junit.Test;
import static org.junit.Assert.*;
/** Contraste calculado não substitui inspeção visual ou acessibilidade em aparelho. */
public class UiStylesTest {
 static double channel(int n){double x=n/255.0;return x<=0.04045?x/12.92:Math.pow((x+0.055)/1.055,2.4);}
 static double lum(int c){return .2126*channel((c>>16)&255)+.7152*channel((c>>8)&255)+.0722*channel(c&255);}
 static double contrast(int a,int b){double x=lum(a),y=lum(b);return (Math.max(x,y)+.05)/(Math.min(x,y)+.05);}
 @Test public void textAndAccentRemainReadableOnSurfaces(){for(boolean dark:new boolean[]{true,false}){
  assertTrue(contrast(ThemePalette.text(dark),UiStyles.surface(dark))>=4.5);
  assertTrue(contrast(UiStyles.muted(dark),UiStyles.surface(dark))>=4.5);
  assertTrue(contrast(UiStyles.accent(dark),UiStyles.surface(dark))>=4.5);
  assertTrue(contrast(ThemePalette.text(dark),UiStyles.secondary(dark))>=4.5);
  assertTrue(contrast(UiStyles.muted(dark),ThemePalette.background(dark))>=4.5);
 }}
}
