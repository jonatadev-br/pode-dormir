package br.com.podedormir;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;

/** Componentes visuais nativos compartilhados; dimensões em dp. */
final class UiStyles {
 private UiStyles(){}
 static int dp(Context c,float value){return Math.round(value*c.getResources().getDisplayMetrics().density);}
 static int surface(boolean dark){return dark?0xff182641:0xffffffff;}
 static int secondary(boolean dark){return dark?0xff223451:0xffe7edf8;}
 static int stroke(boolean dark){return dark?0xff354765:0xffd6dfed;}
 static int muted(boolean dark){return dark?0xffbdcbe1:0xff50617a;}
 static int accent(boolean dark){return dark?0xffcdbfff:0xff514186;}
 static GradientDrawable shape(Context c,int fill,int stroke,int radius){GradientDrawable d=new GradientDrawable();d.setColor(fill);d.setCornerRadius(dp(c,radius));if(stroke!=0)d.setStroke(dp(c,1),stroke);return d;}
 static RippleDrawable button(Context c,boolean dark,boolean primary){int fill=primary?ThemePalette.buttonBackground(dark):secondary(dark);int border=primary?0:stroke(dark);return new RippleDrawable(ColorStateList.valueOf(dark?0x55ffffff:0x33152542),shape(c,fill,border,16),null);}
}
