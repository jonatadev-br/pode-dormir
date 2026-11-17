package br.com.podedormir;

import android.app.AlertDialog;
import android.content.SharedPreferences;

/** Tutorial de leitura, com navegação simples; não altera nem agenda dados. */
final class TutorialGuide {
 private static final String STORE="tutorial_inicial";
 private final MainActivity activity;private AlertDialog dialog;
 TutorialGuide(MainActivity activity){this.activity=activity;}
 private SharedPreferences prefs(){return activity.getSharedPreferences(STORE,0);}
 private void show(AlertDialog.Builder builder){
  AlertDialog next=builder.setCancelable(false).create();dialog=next;
  next.setOnDismissListener(d->{if(dialog==next)dialog=null;});next.show();
 }
 void onResume(){
  if(dialog!=null&&dialog.isShowing())return;
  if(prefs().getBoolean("visto",false))return;
  int step=prefs().getInt("etapa",0);
  if(step>0)showStep(step);else intro();
 }
 void restart(){prefs().edit().putBoolean("visto",false).putInt("etapa",0).commit();dialog=null;intro();}
 private void intro(){
  show(new AlertDialog.Builder(activity).setTitle("Conheça o Pode Dormir!")
   .setMessage("Veja um tutorial rápido com as principais funções. Você pode revê-lo depois em Mais opções.")
   .setNegativeButton("Agora não",(d,w)->finish()).setPositiveButton("Começar",(d,w)->showStep(1)));
 }
 private void finish(){prefs().edit().putBoolean("visto",true).putInt("etapa",0).commit();}
 private void showStep(int n){
  if(n<1||n>4){finish();return;}prefs().edit().putInt("etapa",n).commit();dialog=null;
  String[] titles={"1 de 4 · Cadastre um compromisso","2 de 4 · Confira os ajustes","3 de 4 · Quando o alarme tocar","4 de 4 · Próximo compromisso"};
  String[] text={
   "Na tela inicial, toque em Cadastrar compromisso. Informe o nome e escolha o dia e horário de acordar. Se precisar, abra Mais opções do compromisso para ajustar reservas e repetição.",
   "Abra Verificação de segurança para conferir permissões, volume e avisos. Depois faça um teste no seu aparelho. A verificação é parcial e não garante que o alarme tocará.",
   "Se a tela do alarme aparecer, arraste para cima em qualquer lugar dela ou toque em desativar. Isso confirma o despertar e solicita o cancelamento das reservas deste compromisso. A soneca mantém as reservas ativas.",
   "A notificação pode mostrar o nome do próximo compromisso e o dia e horário de acordar. Esses dados podem ficar visíveis na tela bloqueada. Se a tela do alarme não abrir, toque na notificação."
  };
  AlertDialog.Builder b=new AlertDialog.Builder(activity).setTitle(titles[n-1]).setMessage(text[n-1])
   .setNeutralButton("Sair",(d,w)->finish());
  if(n>1)b.setNegativeButton("Voltar",(d,w)->showStep(n-1));
  if(n<4)b.setPositiveButton("Próximo",(d,w)->showStep(n+1));
  else b.setPositiveButton("Concluir",(d,w)->finish());
  show(b);
 }
}
