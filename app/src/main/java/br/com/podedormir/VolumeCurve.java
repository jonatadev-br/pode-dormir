package br.com.podedormir;
/** Volume relativo ao fluxo de alarme do aparelho, não altera o volume do sistema. */
final class VolumeCurve {
 private VolumeCurve(){}
 static float level(int targetPercent, boolean gradual, long elapsedMs){
  float target=Math.max(10,Math.min(100,targetPercent))/100f;
  if(!gradual)return target;
  float start=Math.min(0.10f,target);
  float progress=Math.max(0f,Math.min(1f,elapsedMs/60000f));
  return start+(target-start)*progress;
 }
}
