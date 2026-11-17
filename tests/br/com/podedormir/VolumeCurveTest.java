package br.com.podedormir;
public final class VolumeCurveTest {
 static int count;static void eq(float a,float b){count++;if(Math.abs(a-b)>0.0001)throw new AssertionError(a+" != "+b);}
 public static void main(String[] args){
  eq(VolumeCurve.level(100,false,0),1f);eq(VolumeCurve.level(80,true,0),0.1f);eq(VolumeCurve.level(80,true,30000),0.45f);eq(VolumeCurve.level(80,true,60000),0.8f);eq(VolumeCurve.level(80,true,90000),0.8f);eq(VolumeCurve.level(5,true,0),0.1f);eq(VolumeCurve.level(120,false,0),1f);eq(VolumeCurve.level(50,true,-100),0.1f);
  System.out.println(count+" testes da curva de volume passaram.");
 }
}
