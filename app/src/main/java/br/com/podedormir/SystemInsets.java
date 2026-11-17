package br.com.podedormir;

import android.graphics.Insets;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;

/** Protege conteúdo das barras quando o Android 15 impõe desenho de borda a borda. */
final class SystemInsets {
 private SystemInsets(){}
 static void apply(View content){
  if(Build.VERSION.SDK_INT<35)return;
  content.setOnApplyWindowInsetsListener((view,insets)->{
   Insets bars=insets.getInsets(WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout());
   view.setPadding(bars.left,bars.top,bars.right,bars.bottom);
   return insets;
  });
  content.requestApplyInsets();
 }
}
