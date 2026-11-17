package br.com.podedormir;

import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.view.View;
import android.view.Gravity;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.Ringtone;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.widget.*;
import org.json.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
 LinearLayout root; private SetupGuide setupGuide; private TutorialGuide tutorial; final SimpleDateFormat fmt=new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm",new Locale("pt","BR"));
 @Override public void onCreate(Bundle b){super.onCreate(b);deleteSharedPreferences("contato");Alarms.restore(this);UpcomingNotification.refresh(this);home();tutorial=new TutorialGuide(this);setupGuide=new SetupGuide(this);}
 @Override protected void onResume(){super.onResume();UpcomingNotification.refresh(this);if(setupGuide!=null&&!setupGuide.onResume())maybeStartTutorial();}
 @Override public void onRequestPermissionsResult(int request,String[] permissions,int[] results){super.onRequestPermissionsResult(request,permissions,results);UpcomingNotification.refresh(this);if(request==SetupGuide.NOTIFICATIONS_REQUEST&&setupGuide!=null)setupGuide.permissionResult();}
 void maybeStartTutorial(){if(tutorial!=null)tutorial.onResume();}
 boolean darkTheme(){String choice=getSharedPreferences("configuracoes",0).getString("tema","escuro");boolean systemDark=(getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)==Configuration.UI_MODE_NIGHT_YES;return ThemePalette.isDark(choice,systemDark);}

 int dp(float value){return UiStyles.dp(this,value);}
 TextView text(String content,int size){TextView t=new TextView(this);t.setText(content);t.setTextSize(Math.max(16,size));t.setTextColor(ThemePalette.text(darkTheme()));t.setLineSpacing(dp(2),1.08f);t.setPadding(0,dp(6),0,dp(6));return t;}
 TextView heading(String content,int size){TextView t=text(content,size);t.setTypeface(Typeface.create("sans-serif-medium",Typeface.NORMAL));return t;}
 TextView caption(String content){TextView t=heading(content,14);t.setTextColor(UiStyles.muted(darkTheme()));t.setLetterSpacing(0.06f);return t;}
 LinearLayout card(){LinearLayout v=new LinearLayout(this);v.setOrientation(1);v.setPadding(dp(20),dp(17),dp(20),dp(17));v.setBackground(UiStyles.shape(this,UiStyles.surface(darkTheme()),UiStyles.stroke(darkTheme()),22));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,dp(6),0,dp(10));root.addView(v,p);return v;}
 void screen(String title){ScrollView sc=new ScrollView(this);sc.setFillViewport(true);root=new LinearLayout(this);root.setOrientation(1);root.setPadding(dp(22),dp(22),dp(22),dp(32));boolean dark=darkTheme();root.setBackgroundColor(ThemePalette.background(dark));sc.setBackgroundColor(ThemePalette.background(dark));sc.setClipToPadding(true);sc.addView(root);setContentView(sc);SystemInsets.apply(sc);getWindow().setStatusBarColor(ThemePalette.background(dark));getWindow().setNavigationBarColor(ThemePalette.background(dark));getWindow().getDecorView().setSystemUiVisibility(dark?0:View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR|View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);if(title!=null){TextView brand=caption("PODE DORMIR!");brand.setTextColor(UiStyles.accent(dark));root.addView(brand);TextView pageTitle=heading(title,29);pageTitle.setPadding(0,dp(3),0,dp(20));root.addView(pageTitle);}}
 Button button(String title,Runnable action){boolean primary=title.equals("Cadastrar compromisso")||title.equals("Salvar compromisso")||title.equals("Salvar configurações")||title.equals("Boa noite! Vou descansar.");Button b=new Button(this);b.setText(title);b.setAllCaps(false);b.setTextSize(18);b.setTypeface(Typeface.create("sans-serif-medium",Typeface.NORMAL));b.setTextColor(primary?ThemePalette.buttonText(darkTheme()):ThemePalette.text(darkTheme()));b.setBackgroundTintList(null);b.setBackground(UiStyles.button(this,darkTheme(),primary));b.setPadding(dp(16),dp(8),dp(16),dp(8));b.setMinHeight(dp(56));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,dp(5),0,dp(5));root.addView(b,p);b.setOnClickListener(v->action.run());return b;}
 LinearLayout optionalSection(String title){
  LinearLayout part=new LinearLayout(this);part.setOrientation(LinearLayout.VERTICAL);part.setVisibility(View.GONE);
  final Button[] toggle={null};toggle[0]=button("Mostrar "+title,()->{boolean show=part.getVisibility()!=View.VISIBLE;part.setVisibility(show?View.VISIBLE:View.GONE);toggle[0].setText((show?"Ocultar ":"Mostrar ")+title);});
  part.setTag(toggle[0]);root.addView(part);return part;
 }
 EditText field(String label,String value,int type){TextView labelView=heading(label,16);labelView.setPadding(0,dp(12),0,dp(6));root.addView(labelView);EditText e=new EditText(this);e.setId(View.generateViewId());labelView.setLabelFor(e.getId());e.setTextColor(ThemePalette.text(darkTheme()));e.setHintTextColor(UiStyles.muted(darkTheme()));e.setBackgroundTintList(null);e.setBackground(UiStyles.shape(this,UiStyles.surface(darkTheme()),UiStyles.stroke(darkTheme()),14));e.setPadding(dp(15),dp(12),dp(15),dp(12));e.setMinHeight(dp(56));e.setTextSize(17);e.setInputType(type);e.setText(value);e.setMinHeight(dp(56));e.setMinimumHeight(dp(56));root.addView(e);return e;}
 void section(String title){TextView h=heading(title,18);h.setPadding(0,dp(18),0,dp(5));root.addView(h);}
 void home(){
  screen(null);
  LinearLayout brandRow=new LinearLayout(this);brandRow.setOrientation(LinearLayout.HORIZONTAL);brandRow.setGravity(Gravity.CENTER_VERTICAL);
  ImageView logo=new ImageView(this);logo.setImageResource(br.com.podedormir.R.drawable.icone_aplicativo);logo.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);LinearLayout.LayoutParams icon=new LinearLayout.LayoutParams(dp(52),dp(52));icon.setMargins(0,0,dp(14),0);brandRow.addView(logo,icon);
  LinearLayout brandCopy=new LinearLayout(this);brandCopy.setOrientation(LinearLayout.VERTICAL);TextView brand=heading("Pode Dormir!",22);brand.setPadding(0,0,0,0);brandCopy.addView(brand);TextView slogan=text("Está tudo preparado. Agora pode descansar.",16);slogan.setTextColor(UiStyles.muted(darkTheme()));slogan.setPadding(0,dp(3),0,0);brandCopy.addView(slogan);brandRow.addView(brandCopy,new LinearLayout.LayoutParams(0,-2,1));root.addView(brandRow);
  String nomeUsuario=getSharedPreferences("configuracoes",0).getString("nome","").trim();TextView hello=heading(nomeUsuario.isEmpty()?"Olá!":"Olá, "+nomeUsuario+"!",25);hello.setPadding(0,dp(30),0,dp(4));root.addView(hello);
  JSONArray a=Alarms.load(this);JSONObject next=null;for(int i=0;i<a.length();i++){JSONObject e=a.optJSONObject(i);if(e!=null&&!e.optBoolean("teste")&&!e.optBoolean("confirmado")&&!e.optBoolean("horarioInvalido")&&e.optLong("hora")>System.currentTimeMillis()&&(next==null||e.optLong("hora")<next.optLong("hora")))next=e;}
  LinearLayout eventCard=card();eventCard.addView(caption("PRÓXIMO COMPROMISSO"));
  if(!Alarms.storageValid(this)){eventCard.addView(heading("Não foi possível ler os compromissos",20));eventCard.addView(text("Os dados foram preservados. Use outro despertador.",16));}
  else if(next==null){eventCard.addView(heading("Ainda não há compromissos",20));TextView hint=text("Cadastre um compromisso para começar a organizar seus alarmes.",16);hint.setTextColor(UiStyles.muted(darkTheme()));eventCard.addView(hint);}
  else {long ms=next.optLong("hora"),mins=Math.max(0,(ms-System.currentTimeMillis())/60000L);eventCard.addView(heading(next.optString("nome"),23));TextView time=heading(new SimpleDateFormat("HH:mm",new Locale("pt","BR")).format(new Date(ms)),36);time.setTextColor(UiStyles.accent(darkTheme()));eventCard.addView(time);TextView date=text("Despertar em "+new SimpleDateFormat("dd/MM/yyyy",new Locale("pt","BR")).format(new Date(ms))+" · faltam aproximadamente "+(mins/60)+" h e "+(mins%60)+" min",15);date.setTextColor(UiStyles.muted(darkTheme()));eventCard.addView(date);}
  LinearLayout status=card();status.addView(caption("VERIFICAÇÃO DE SEGURANÇA"));Readiness.Level level=readiness();status.addView(heading(readinessLabel(level),17));TextView statusInfo=text("Versão em testes. Não use este aplicativo como único despertador.",15);statusInfo.setTextColor(UiStyles.muted(darkTheme()));status.addView(statusInfo);
  LinearLayout homeRoot=root;root=status;button("Verificação de segurança",this::checks);root=homeRoot;
  button("Cadastrar compromisso",()->form(null,false));button("Meus alarmes",this::list);button("Mais opções",this::more);
 }
 void more(){screen("Mais opções");root.addView(text("Encontre testes, histórico e ajustes do aplicativo.",16));button("Preparar para dormir",this::bedtime);button("Ver tutorial",()->tutorial.restart());button("Testar meu despertador",this::test);button("Histórico",this::history);button("Configurações",this::settings);button("Privacidade",this::privacy);button("Voltar",this::home);}

 void form(JSONObject original,boolean duplicate){
  screen(original==null?"Cadastrar compromisso":duplicate?"Duplicar compromisso":"Editar compromisso");
  section("Sobre o compromisso");
  EditText name=field("Nome do compromisso",original==null?"":original.optString("nome"),1);
  section("Quando você precisa acordar?");
  Calendar wake=Calendar.getInstance();wake.setTimeInMillis(original==null?System.currentTimeMillis()+3600000L:original.optLong("hora"));wake.set(Calendar.SECOND,0);wake.set(Calendar.MILLISECOND,0);
  TextView selected=text("Despertar: "+fmt.format(wake.getTime()),18);root.addView(selected);
  button("Escolher data do despertar",()->new DatePickerDialog(this,(v,y,m,d)->{wake.set(y,m,d);selected.setText("Despertar: "+fmt.format(wake.getTime()));},wake.get(Calendar.YEAR),wake.get(Calendar.MONTH),wake.get(Calendar.DAY_OF_MONTH)).show());
  button("Escolher horário do despertar",()->new TimePickerDialog(this,(v,h,m)->{wake.set(Calendar.HOUR_OF_DAY,h);wake.set(Calendar.MINUTE,m);selected.setText("Despertar: "+fmt.format(wake.getTime()));},wake.get(Calendar.HOUR_OF_DAY),wake.get(Calendar.MINUTE),true).show());
  TextView backupSummary=text("",16);root.addView(backupSummary);
  final String[] recurrence={original==null?"nenhuma":original.optString("recorrencia","nenhuma")};
  final TextView repeat=text("Repetição: "+(recurrence[0].equals("diaria")?"Todos os dias":recurrence[0].equals("semanal")?"Toda semana":"Nenhuma"),16);root.addView(repeat);
  if(original!=null&&!duplicate&&original.optBoolean("proximaGerada"))root.addView(text("Esta ocorrência já gerou a próxima. Editá-la não altera as outras. Para parar todas as ocorrências, use Excluir série na lista.",16));
  LinearLayout pageRoot=root,advanced=optionalSection("mais opções do compromisso");root=advanced;
  EditText notes=field("Observações (opcional)",original==null?"":original.optString("notas"),1|131072);
  section("Planejamento da saída");
  Calendar departure=(Calendar)wake.clone();if(original!=null&&original.has("saida"))departure.setTimeInMillis(original.optLong("saida"));else departure.add(Calendar.HOUR_OF_DAY,1);
  final boolean[] hasDeparture={original!=null&&original.has("saida")};TextView dep=text("Saída: "+(hasDeparture[0]?fmt.format(departure.getTime()):"não informada"),16);root.addView(dep);
  button("Escolher data e horário de saída",()->new DatePickerDialog(this,(v,y,m,d)->{departure.set(y,m,d);new TimePickerDialog(this,(v2,h,min)->{departure.set(Calendar.HOUR_OF_DAY,h);departure.set(Calendar.MINUTE,min);departure.set(Calendar.SECOND,0);departure.set(Calendar.MILLISECOND,0);hasDeparture[0]=true;dep.setText("Saída: "+fmt.format(departure.getTime()));},departure.get(Calendar.HOUR_OF_DAY),departure.get(Calendar.MINUTE),true).show();},departure.get(Calendar.YEAR),departure.get(Calendar.MONTH),departure.get(Calendar.DAY_OF_MONTH)).show());
  EditText prep=field("Tempo de preparo em minutos",original==null?"60":String.valueOf(original.optInt("preparo",60)),2);
  button("Calcular despertar pela saída",()->{try{int minutes=Integer.parseInt(prep.getText().toString());if(!hasDeparture[0]||minutes<0||minutes>1440)throw new Exception();wake.setTimeInMillis(departure.getTimeInMillis()-minutes*60000L);selected.setText("Despertar: "+fmt.format(wake.getTime()));}catch(Exception ex){toast("Informe a saída e um tempo de preparo entre 0 e 1440 minutos.");}});
  section("Repetição e alarmes de reserva");
  button("Escolher repetição",()->new AlertDialog.Builder(this).setTitle("Repetição").setItems(new String[]{"Nenhuma", "Todos os dias", "Toda semana"},(d,i)->{recurrence[0]=i==1?"diaria":i==2?"semanal":"nenhuma";repeat.setText("Repetição: "+(i==1?"Todos os dias":i==2?"Toda semana":"Nenhuma"));}).setNegativeButton("Cancelar",null).show());
  EditText backups=field("Número de alarmes de reserva",original==null?String.valueOf(getSharedPreferences("configuracoes",0).getInt("reservas",2)):String.valueOf(original.optInt("reservas",2)),2);
  root.addView(text("A quantidade que pode ser programada depende do aparelho. Se o pedido ficar incompleto, use outro despertador.",15));
  EditText interval=field("Intervalo entre alarmes em minutos (1 a 60)",original==null?String.valueOf(getSharedPreferences("configuracoes",0).getInt("intervalo",5)):String.valueOf(original.optInt("intervalo",5)),2);
  Runnable refreshSummary=()->backupSummary.setText("Reservas: "+backups.getText()+" · intervalo: "+interval.getText()+" min");
  android.text.TextWatcher watcher=new android.text.TextWatcher(){public void beforeTextChanged(CharSequence x,int a,int c,int f){}public void onTextChanged(CharSequence x,int a,int before,int count){refreshSummary.run();}public void afterTextChanged(android.text.Editable x){}};
  backups.addTextChangedListener(watcher);interval.addTextChangedListener(watcher);refreshSummary.run();root=pageRoot;
  button("Salvar compromisso",()->{try{
   String n=name.getText().toString().trim();int b=Integer.parseInt(backups.getText().toString());int gap=Integer.parseInt(interval.getText().toString());int preparation=Integer.parseInt(prep.getText().toString());
   if(n.isEmpty()||wake.getTimeInMillis()<=System.currentTimeMillis()||b<0||gap<1||gap>60||preparation<0||preparation>1440||hasDeparture[0]&&departure.getTimeInMillis()<wake.getTimeInMillis())throw new IllegalArgumentException();
   JSONObject e=new JSONObject();e.put("id",original==null||duplicate?UUID.randomUUID().toString():original.optString("id"));e.put("nome",n);e.put("notas",notes.getText().toString());e.put("hora",wake.getTimeInMillis());e.put("preparo",preparation);e.put("reservas",b);e.put("intervalo",gap);e.put("confirmado",false);e.put("recorrencia",recurrence[0]);if(original!=null&&!duplicate&&original.has("serieId"))e.put("serieId",original.optString("serieId"));else if(!recurrence[0].equals("nenhuma"))e.put("serieId",e.optString("id"));if(original!=null&&!duplicate&&original.optBoolean("proximaGerada"))e.put("proximaGerada",true);String zone=TimeZone.getDefault().getID();e.put("fuso",zone);e.put("despertarLocal",LocalTimeRules.localAt(wake.getTimeInMillis(),zone));if(hasDeparture[0]){e.put("saida",departure.getTimeInMillis());e.put("saidaLocal",LocalTimeRules.localAt(departure.getTimeInMillis(),zone));}
   if(!Alarms.put(this,e)){toast("Não foi possível salvar o compromisso.");return;}
   int count=Alarms.schedule(this,e);boolean recurrenceOk=Recurrences.advance(this,e.optString("id"),System.currentTimeMillis());History.add(this,"Compromisso: "+n+". Solicitações de alarme aceitas pelo Android: "+count+" de "+((long)b+1)+".");toast(!recurrenceOk?"Atenção: não foi possível preparar a próxima ocorrência.":count==(long)b+1?"Solicitações de alarmes enviadas ao Android. Confira as permissões.":"Atenção: nem todos os alarmes puderam ser solicitados. Use outro despertador e confira permissões e quantidade.");home();
  }catch(Exception ex){advanced.setVisibility(View.VISIBLE);((Button)advanced.getTag()).setText("Ocultar mais opções do compromisso");toast("Confira o nome, a data futura, a saída e os intervalos informados.");}});
  button("Voltar",this::home);
 }
 void list(){screen("Meus alarmes");if(!Alarms.storageValid(this)){root.addView(text("Não foi possível ler os compromissos. Os dados foram preservados. Use outro despertador.",18));button("Voltar",this::home);return;}JSONArray a=Alarms.load(this);int shown=0;for(int i=0;i<a.length();i++){JSONObject e=a.optJSONObject(i);if(e==null||e.optBoolean("teste"))continue;shown++;
  root.addView(text(e.optString("nome")+" — "+(e.optBoolean("horarioInvalido")?e.optString("despertarLocal")+" (horário inválido neste fuso; edite o compromisso)":fmt.format(new Date(e.optLong("hora"))))+"\n"+(e.optBoolean("horarioInvalido")?"ATENÇÃO: alarmes não foram programados para este horário.":e.optBoolean("confirmado")?"Despertar confirmado.":e.optInt("reservas",2)+" reserva(s), a cada "+e.optInt("intervalo",5)+" minuto(s).")+"\nRepetição: "+Recurrences.label(e),18));
  button("Opções: "+e.optString("nome"),()->eventOptions(e));
 }if(shown==0)root.addView(text("Nenhum compromisso cadastrado.",18));button("Voltar",this::home);}
 void eventOptions(JSONObject e){
  String[] options=e.has("serieId")?new String[]{"Editar", "Duplicar", "Excluir compromisso", "Excluir série"}:new String[]{"Editar", "Duplicar", "Excluir compromisso"};
  new AlertDialog.Builder(this).setTitle(e.optString("nome")).setItems(options,(dialog,which)->{
   if(which==0)form(e,false);else if(which==1)form(e,true);else if(which==2)deleteEvent(e);else deleteSeries(e);
  }).setNegativeButton("Voltar",null).show();
 }
 void deleteEvent(JSONObject e){
  new AlertDialog.Builder(this).setMessage("Excluir apenas esta ocorrência e cancelar seus alarmes? Outras ocorrências já criadas continuarão ativas.").setNegativeButton("Não",null).setPositiveButton("Excluir",(d,w)->{if(!Recurrences.advance(this,e.optString("id"),System.currentTimeMillis())){toast("Não foi possível preparar a próxima ocorrência; a exclusão foi cancelada.");return;}if(Alarms.remove(this,e.optString("id"))){History.add(this,"Compromisso excluído: "+e.optString("nome")+". Cancelamento solicitado.");Alarms.cancel(this,e);try{startService(new Intent(this,AlarmService.class).setAction(AlarmService.CONFIRM).putExtra("id",e.optString("id")));}catch(RuntimeException ignored){}list();}else toast("Não foi possível excluir.");}).show();
 }
 void deleteSeries(JSONObject e){
  new AlertDialog.Builder(this).setTitle("Excluir série?").setMessage("Todas as ocorrências desta série serão excluídas e seus alarmes serão cancelados. O histórico permanece até ser apagado.").setNegativeButton("Cancelar",null).setPositiveButton("Excluir série",(d,w)->{if(Recurrences.removeSeries(this,e.optString("serieId"))){toast("Série excluída; cancelamentos solicitados.");list();}else toast("Não foi possível excluir a série.");}).show();
 }
 void checks(){screen("Verificação de segurança — parcial");root.addView(text("Situação: "+readinessLabel(readiness()),20));if(!Alarms.storageValid(this))root.addView(text("⚠ Não foi possível ler os compromissos. Os dados foram preservados e não podemos verificar seus alarmes. Use outro despertador.",18));boolean exact=Alarms.allowed(this);NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);AudioManager am=(AudioManager)getSystemService(AUDIO_SERVICE);PowerManager pm=(PowerManager)getSystemService(POWER_SERVICE);
 section("Permissões e notificações");button("Configurar permissões",()->setupGuide.start());History.add(this,"Verificação parcial: alarmes exatos "+(exact?"permitidos":"não permitidos")+", notificações "+(nm.areNotificationsEnabled()?"ativadas":"desativadas")+", volume do alarme "+am.getStreamVolume(AudioManager.STREAM_ALARM)+" de "+am.getStreamMaxVolume(AudioManager.STREAM_ALARM)+". Esta verificação não garante a entrega.");
 root.addView(text((exact?"✓":"⚠")+" Permissão para alarmes exatos: "+(exact?"permitida":"não permitida"),18));if(!exact&&Build.VERSION.SDK_INT>=31)button("Permitir alarmes exatos",()->open(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,Uri.parse("package:"+getPackageName()))));
 root.addView(text((nm.areNotificationsEnabled()?"✓":"⚠")+" Notificações do aplicativo: "+(nm.areNotificationsEnabled()?"ativadas":"desativadas"),18));
 NotificationChannel channel=nm.getNotificationChannel("alarmes_continuos_v1");
 if(channel!=null&&channel.getImportance()==NotificationManager.IMPORTANCE_NONE)root.addView(text("⚠ O canal de alarmes está desativado. Ative-o nos ajustes de notificações.",18));
 NotificationChannel reminder=nm.getNotificationChannel(UpcomingNotification.CHANNEL);
 if(reminder!=null&&reminder.getImportance()==NotificationManager.IMPORTANCE_NONE)root.addView(text("⚠ A notificação do próximo compromisso está desativada nos ajustes do aparelho.",18));
 root.addView(text("A notificação do próximo compromisso pode mostrar nome e horário na tela bloqueada. O aparelho pode ocultá-los conforme as configurações de privacidade.",16));
 int dnd=nm.getCurrentInterruptionFilter();
 root.addView(text("Não perturbe: "+(dnd==NotificationManager.INTERRUPTION_FILTER_ALL?"o filtro geral não está ativo":"pode restringir alertas")+". O efeito sobre alarmes depende das regras do aparelho.",18));
 if(Build.VERSION.SDK_INT>=33&&checkSelfPermission("android.permission.POST_NOTIFICATIONS")!=PackageManager.PERMISSION_GRANTED)button("Solicitar permissão de notificações",()->new AlertDialog.Builder(this).setMessage("As notificações exibem o alerta quando o despertador toca. Sem elas, o alerta pode não aparecer.").setNegativeButton("Agora não",null).setPositiveButton("Continuar",(d,w)->requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"},1)).show());
 button("Abrir ajustes de notificações",()->open(new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE,getPackageName())));
 section("Som e descanso");root.addView(text("Volume do alarme: "+am.getStreamVolume(AudioManager.STREAM_ALARM)+" de "+am.getStreamMaxVolume(AudioManager.STREAM_ALARM)+". "+(am.getStreamVolume(AudioManager.STREAM_ALARM)==0?"⚠ O volume está zerado. ":"")+"Verifique se consegue ouvir.",18));android.content.SharedPreferences soundPrefs=getSharedPreferences("configuracoes",0);int appVolume=soundPrefs.getInt("volume_app",100);boolean ramp=soundPrefs.getBoolean("volume_gradual",false);root.addView(text("Volume do aplicativo: "+appVolume+"%"+(ramp?" — aumento gradual ativado; o som começa baixo.":"")+" O resultado também depende do volume do aparelho.",18));button("Abrir ajustes de som",()->open(new Intent(Settings.ACTION_SOUND_SETTINGS)));
 section("Bateria e execução");root.addView(text("Otimização de bateria: "+(pm.isIgnoringBatteryOptimizations(getPackageName())?"sem otimização":"pode estar ativa")+". Fabricantes podem aplicar outras restrições.",18));button("Abrir ajustes de bateria",()->open(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)));button("Ajuda para o fabricante",this::batteryHelp);
 Intent battery=registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));if(battery!=null){int level=battery.getIntExtra(BatteryManager.EXTRA_LEVEL,-1),scale=battery.getIntExtra(BatteryManager.EXTRA_SCALE,-1);if(level>=0&&scale>0)root.addView(text("Bateria: "+(level*100/scale)+"%. Mantenha o aparelho carregado.",18));}
 if(Build.VERSION.SDK_INT>=34){root.addView(text("Tela cheia: "+(nm.canUseFullScreenIntent()?"permitida":"não permitida")+". O Android pode mostrar uma notificação em vez da tela.",18));button("Abrir ajustes de tela cheia",()->open(new Intent(Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT,Uri.parse("package:"+getPackageName()))));}
 section("Horários cadastrados");JSONArray events=Alarms.load(this);int upcoming=0;
 for(int j=0;j<events.length();j++){
  JSONObject item=events.optJSONObject(j);if(item==null||item.optBoolean("teste")||item.optBoolean("confirmado"))continue;
  if(item.optBoolean("recorrenciaFalhou")||item.optBoolean("pedidoIncompleto"))root.addView(text("⚠ "+item.optString("nome")+": houve falha ao preparar a repetição ou solicitar alarmes. Confira permissões e edite o compromisso.",18));
  if(item.optBoolean("horarioInvalido")){root.addView(text("⚠ "+item.optString("nome")+": horário inválido neste fuso. Edite o compromisso; os alarmes não foram programados.",18));continue;}
  if(item.optLong("hora")<=System.currentTimeMillis())continue;
  upcoming++;StringBuilder dates=new StringBuilder("Compromisso: "+item.optString("nome")+"\nPrincipal: "+fmt.format(new Date(item.optLong("hora"))));
  int count=Math.max(0,item.optInt("reservas",2)),gap=Math.max(1,Math.min(60,item.optInt("intervalo",5)));
  if(count>0)dates.append("\nReservas: ").append(count).append(" · a cada ").append(gap).append(" minuto(s).");
  dates.append("\nHorários cadastrados; registro atual no Android não verificado.");root.addView(text(dates.toString(),18));
 }
 if(upcoming==0&&Alarms.storageValid(this)){root.addView(text("⚠ Nenhum compromisso futuro disponível. Cadastre ou corrija um compromisso.",18));button("Cadastrar compromisso",()->form(null,false));}
 root.addView(text("Não é possível confirmar aqui se todos os alarmes continuam registrados, se o Não perturbe permitirá o som ou se o aparelho estará ligado. A verificação não está completa.",16));button("Atualizar verificações",this::checks);button("Voltar",this::home);}
 void test(){screen("Testar meu despertador");root.addView(text("O teste inicia o som e abre a tela de alerta. Ele não comprova a entrega de um alarme com o aplicativo fechado. Toque em “Desativar” ou arraste para cima para parar.",18));button("Iniciar teste",()->{try{JSONObject e=new JSONObject();e.put("id",UUID.randomUUID().toString());e.put("nome","Teste do despertador");e.put("hora",System.currentTimeMillis());e.put("teste",true);if(!Alarms.put(this,e)){toast("Não foi possível salvar o teste.");return;}Intent s=new Intent(this,AlarmService.class).setAction(AlarmService.START).putExtra("id",e.optString("id"));startForegroundService(s);startActivity(new Intent(this,AlarmActivity.class).putExtra("id",e.optString("id")));}catch(Exception ex){toast("Não foi possível iniciar o teste.");}});button("Voltar",this::home);}


 Readiness.Level readiness(){
  if(!Alarms.storageValid(this))return Readiness.Level.CRITICAL;
  boolean future=false,invalid=false;long now=System.currentTimeMillis();JSONArray all=Alarms.load(this);
  for(int i=0;i<all.length();i++){JSONObject event=all.optJSONObject(i);if(event==null||event.optBoolean("teste")||event.optBoolean("confirmado"))continue;if(event.optBoolean("horarioInvalido")||event.optBoolean("recorrenciaFalhou")||event.optBoolean("pedidoIncompleto"))invalid=true;if(!event.optBoolean("horarioInvalido")&&event.optLong("hora")>now)future=true;}
  NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
  NotificationChannel ch=nm.getNotificationChannel("alarmes_continuos_v1");
  boolean blocked=ch!=null&&ch.getImportance()==NotificationManager.IMPORTANCE_NONE;
  boolean fullscreen=Build.VERSION.SDK_INT<34||nm.canUseFullScreenIntent();
  AudioManager am=(AudioManager)getSystemService(AUDIO_SERVICE);
  PowerManager pm=(PowerManager)getSystemService(POWER_SERVICE);
  Intent battery=registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));int pct=-1;
  if(battery!=null){int l=battery.getIntExtra(BatteryManager.EXTRA_LEVEL,-1),scale=battery.getIntExtra(BatteryManager.EXTRA_SCALE,-1);if(l>=0&&scale>0)pct=l*100/scale;}
  return Readiness.evaluate(future,Alarms.allowed(this),nm.areNotificationsEnabled(),blocked,am.getStreamVolume(AudioManager.STREAM_ALARM),invalid,fullscreen,pm.isIgnoringBatteryOptimizations(getPackageName()),pct,nm.getCurrentInterruptionFilter()==NotificationManager.INTERRUPTION_FILTER_ALL,false);
 }
 String readinessLabel(Readiness.Level level){
  if(level==Readiness.Level.CRITICAL)return "🔴 Há itens importantes para corrigir.";
  if(level==Readiness.Level.ATTENTION)return "🟡 Atenção: verificação incompleta.";
  return "🟢 Verificações concluídas.";
 }
 void batteryHelp(){
  screen("Ajuda para bateria e execução");
  root.addView(text("Aparelho identificado: "+Build.MANUFACTURER+". Os nomes e caminhos podem mudar conforme o modelo e a versão do Android.",18));
  root.addView(text("Samsung: procure Pode Dormir! em Ajustes > Aplicativos > Bateria. Verifique se o aplicativo não foi colocado em suspensão profunda e consulte as opções de execução em segundo plano.",18));
  root.addView(text("Motorola: procure Pode Dormir! em Configurações > Apps > Bateria do app. Verifique as opções de uso em segundo plano e economia de bateria.",18));
  root.addView(text("Xiaomi, Redmi ou POCO: procure Pode Dormir! em Configurações > Apps > Gerenciar apps > Bateria. Verifique restrições de bateria e, se houver, inicialização automática.",18));
  root.addView(text("Outros fabricantes: procure o aplicativo nos ajustes de Apps e Bateria. Ajustes sem restrição podem aumentar o consumo e não garantem o alarme. Mantenha o aparelho ligado e carregado.",18));
  button("Abrir ajustes do aplicativo",()->open(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,Uri.parse("package:"+getPackageName()))));
  button("Abrir ajustes de bateria",()->open(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)));
  button("Voltar à verificação",this::checks);
 }

 void bedtime(){
  screen("Preparar para dormir");TextView subtitle=text("Uma última olhada no que está configurado.",16);subtitle.setTextColor(UiStyles.muted(darkTheme()));root.addView(subtitle);
  JSONArray a=Alarms.load(this);JSONObject next=null;
  for(int i=0;i<a.length();i++){JSONObject e=a.optJSONObject(i);if(e!=null&&!e.optBoolean("teste")&&!e.optBoolean("confirmado")&&!e.optBoolean("horarioInvalido")&&e.optLong("hora")>System.currentTimeMillis()&&(next==null||e.optLong("hora")<next.optLong("hora")))next=e;}
  LinearLayout eventCard=card();eventCard.addView(caption("PRÓXIMO COMPROMISSO"));
  if(!Alarms.storageValid(this)){eventCard.addView(heading("Não foi possível ler os compromissos",20));eventCard.addView(text("Os dados foram preservados. Use outro despertador.",16));}
  else if(next==null){eventCard.addView(heading("Nenhum compromisso futuro",20));eventCard.addView(text("Cadastre um compromisso e confira as permissões.",16));}
  else {long ms=next.optLong("hora"),minutes=Math.max(0,(ms-System.currentTimeMillis())/60000L);eventCard.addView(heading(next.optString("nome"),21));TextView time=heading(new SimpleDateFormat("HH:mm",new Locale("pt","BR")).format(new Date(ms)),34);time.setTextColor(UiStyles.accent(darkTheme()));eventCard.addView(time);eventCard.addView(text("Despertar em "+fmt.format(new Date(ms))+" · aproximadamente "+(minutes/60)+" h e "+(minutes%60)+" min",16));TextView detail=text("Principal e "+next.optInt("reservas",2)+" reserva(s): horários cadastrados. O registro atual no Android não foi confirmado.",15);detail.setTextColor(UiStyles.muted(darkTheme()));eventCard.addView(detail);}
  LinearLayout status=card();status.addView(caption("RESUMO DE SEGURANÇA"));status.addView(heading(readinessLabel(readiness()),18));
  NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);AudioManager am=(AudioManager)getSystemService(AUDIO_SERVICE);PowerManager pm=(PowerManager)getSystemService(POWER_SERVICE);
  status.addView(text("Alarmes exatos: "+(Alarms.allowed(this)?"permitidos":"atenção, não permitidos")+"\nNotificações: "+(nm.areNotificationsEnabled()?"ativadas":"atenção, desativadas")+"\nVolume do alarme: "+am.getStreamVolume(AudioManager.STREAM_ALARM)+" de "+am.getStreamMaxVolume(AudioManager.STREAM_ALARM)+"\nOtimização de bateria: "+(pm.isIgnoringBatteryOptimizations(getPackageName())?"sem otimização":"pode estar ativa"),16));
  android.content.SharedPreferences soundPrefs=getSharedPreferences("configuracoes",0);status.addView(text("Volume do aplicativo: "+soundPrefs.getInt("volume_app",100)+"%"+(soundPrefs.getBoolean("volume_gradual",false)?" — começa baixo e aumenta aos poucos.":""),16));
  if(Build.VERSION.SDK_INT>=34)status.addView(text("Tela cheia: "+(nm.canUseFullScreenIntent()?"permitida":"atenção, não permitida"),16));
  Intent battery=registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));if(battery!=null){int level=battery.getIntExtra(BatteryManager.EXTRA_LEVEL,-1),scale=battery.getIntExtra(BatteryManager.EXTRA_SCALE,-1);if(level>=0&&scale>0)status.addView(text("Bateria: "+(level*100/scale)+"%",16));}
  TextView note=text("A verificação é parcial e o alarme pode falhar. Mantenha o aparelho ligado e carregado e use outro despertador enquanto esta versão estiver em testes.",15);note.setTextColor(UiStyles.muted(darkTheme()));root.addView(note);
  button("Verificação de segurança",this::checks);
  button("Boa noite! Vou descansar.",()->{TextView moon=heading("☾  ✦  Boa noite!",30);moon.setAlpha(0);root.addView(moon);moon.animate().alpha(1f).setDuration(900).start();});
  button("Voltar",this::home);
 }

 void settings(){
  screen("Configurações");android.content.SharedPreferences prefs=getSharedPreferences("configuracoes",0);
  String theme=prefs.getString("tema","escuro");String themeLabel="claro".equals(theme)?"Claro":"sistema".equals(theme)?"Seguir o aparelho":"Escuro";
  root.addView(text("Tema: "+themeLabel+". Salve as demais alterações antes de trocar o tema.",18));
  button("Escolher tema",()->new AlertDialog.Builder(this).setTitle("Escolher tema").setItems(new String[]{"Escuro", "Claro", "Seguir o aparelho"},(d,which)->{String choice=which==0?"escuro":which==1?"claro":"sistema";if(prefs.edit().putString("tema",choice).commit()){toast("Tema alterado. Outras alterações ainda não salvas foram descartadas.");settings();}else toast("Não foi possível salvar o tema.");}).setNegativeButton("Cancelar",null).show());
  root.addView(text("Confira o volume do alarme no aparelho. O som pode não ser ouvido se estiver baixo.",16));
  LinearLayout settingsPage=root,details=optionalSection("ajustes de perfil e alarme");root=details;
  section("Seu perfil");
  EditText name=field("Como podemos chamar você?",prefs.getString("nome",""),1);
  section("Alarmes e soneca");
  EditText backups=field("Quantidade padrão de reservas",String.valueOf(prefs.getInt("reservas",2)),2);
  EditText interval=field("Intervalo padrão em minutos (1 a 60)",String.valueOf(prefs.getInt("intervalo",5)),2);
  EditText snooze=field("Duração da soneca em minutos (1 a 60)",String.valueOf(prefs.getInt("soneca",5)),2);
  CheckBox vibrate=new CheckBox(this);vibrate.setText("Vibrar durante o alerta");vibrate.setTextColor(ThemePalette.text(darkTheme()));vibrate.setButtonTintList(ColorStateList.valueOf(ThemePalette.text(darkTheme())));vibrate.setChecked(prefs.getBoolean("vibrar",true));root.addView(vibrate);
  section("Som e vibração");
  EditText volume=field("Volume do aplicativo em % (10 a 100)",String.valueOf(prefs.getInt("volume_app",100)),2);
  CheckBox gradual=new CheckBox(this);gradual.setText("Aumentar o volume aos poucos durante 1 minuto");gradual.setTextColor(ThemePalette.text(darkTheme()));gradual.setButtonTintList(ColorStateList.valueOf(ThemePalette.text(darkTheme())));gradual.setChecked(prefs.getBoolean("volume_gradual",false));root.addView(gradual);
  root.addView(text("O volume do aplicativo é limitado pelo volume de alarme do aparelho. O aumento gradual começa baixo; confirme que consegue ouvi-lo. Alguns aparelhos podem não aceitar esse ajuste.",16));
  String uriText=prefs.getString("som","");String soundTitle="Padrão do aparelho";
  if(!uriText.isEmpty())try{Ringtone r=RingtoneManager.getRingtone(this,Uri.parse(uriText));if(r!=null)soundTitle=r.getTitle(this);else soundTitle="Som escolhido indisponível";}catch(Exception ex){soundTitle="Som escolhido indisponível";}
  root.addView(text("Som do alarme: "+soundTitle,18));
  button("Escolher som do aparelho",()->{Intent picker=new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);picker.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_ALARM);picker.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,"Escolha o som do despertador");picker.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT,true);picker.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT,false);picker.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,uriText.isEmpty()?RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM):Uri.parse(uriText));try{startActivityForResult(picker,72);}catch(Exception ex){toast("Não foi possível abrir a seleção de sons.");}});
  button("Abrir volume de alarme do aparelho",()->open(new Intent(Settings.ACTION_SOUND_SETTINGS)));
  root.addView(text("A escolha de som é salva imediatamente; salve as demais alterações antes de abrir o seletor. Se o som escolhido ficar indisponível, o aplicativo tentará usar o padrão. Confira também o volume de alarme nos controles do aparelho.",16));
  root.addView(text("Os padrões de reservas serão aplicados aos novos compromissos. Os já cadastrados não serão alterados.",16));
  root=settingsPage;
  button("Salvar configurações",()->{try{int b=Integer.parseInt(backups.getText().toString()),gap=Integer.parseInt(interval.getText().toString()),nap=Integer.parseInt(snooze.getText().toString()),vol=Integer.parseInt(volume.getText().toString());if(b<0||gap<1||gap>60||nap<1||nap>60||vol<10||vol>100)throw new IllegalArgumentException();if(prefs.edit().putString("nome",name.getText().toString().trim()).putInt("reservas",b).putInt("intervalo",gap).putInt("soneca",nap).putBoolean("vibrar",vibrate.isChecked()).putInt("volume_app",vol).putBoolean("volume_gradual",gradual.isChecked()).commit()){toast("Configurações salvas.");home();}else toast("Não foi possível salvar.");}catch(Exception ex){details.setVisibility(View.VISIBLE);((Button)details.getTag()).setText("Ocultar ajustes de perfil e alarme");toast("Informe uma quantidade de reservas não negativa, durações de 1 a 60 minutos e volume de 10% a 100%.");}});
  button("Ajustes de notificações do Android",()->open(new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE,getPackageName())));
  button("Voltar",this::home);
 }

 @Override protected void onActivityResult(int request,int result,Intent data){
  super.onActivityResult(request,result,data);
  if(request!=72)return;
  if(result==RESULT_OK&&data!=null){
   Uri picked=data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
   if(picked==null){toast("Escolha um som; o modo silencioso não é aceito.");}
   else if(getSharedPreferences("configuracoes",0).edit().putString("som",picked.toString()).commit())toast("Som selecionado. Teste se consegue ouvi-lo.");
   else toast("Não foi possível salvar o som.");
  }
  settings();
 }

 void history(){
  screen("Histórico");root.addView(text("Registros locais mostram o que o aplicativo observou, não comprovam que você ouviu o alarme. São mantidos até 250 registros.",16));
  boolean historyValid=History.storageValid(this);JSONArray entries=historyValid?History.load(this):new JSONArray();if(!historyValid)root.addView(text("Não foi possível ler o histórico. Os registros foram preservados e novos registros não serão acrescentados até que você apague o histórico.",18));else if(entries.length()==0)root.addView(text("Ainda não há registros.",18));
  for(int i=entries.length()-1;i>=0;i--){JSONObject entry=entries.optJSONObject(i);if(entry!=null)root.addView(text(fmt.format(new Date(entry.optLong("quando")))+"\n"+entry.optString("detalhe"),16));}
  if(entries.length()>0||!historyValid)button("Apagar histórico",()->new AlertDialog.Builder(this).setTitle("Apagar histórico?").setMessage("Os registros serão excluídos deste aparelho. Os compromissos e alarmes existentes não serão alterados.").setNegativeButton("Cancelar",null).setPositiveButton("Apagar",(d,w)->{if(History.clear(this)){toast("Histórico apagado.");history();}else toast("Não foi possível apagar o histórico.");}).show());
  button("Voltar",this::home);
 }
 void privacy(){screen("Privacidade");root.addView(text("Compromissos, observações, confirmações e histórico são armazenados no espaço privado do aplicativo neste aparelho. O backup do aplicativo está desativado. Os dados não recebem criptografia adicional específica; use um bloqueio de tela para proteger o aparelho.",18));root.addView(text("Esta versão não solicita acesso à internet nem envia dados a servidores. Ao excluir um compromisso, registros anteriores podem permanecer no histórico até serem apagados nessa seção. Desinstalar o aplicativo remove os dados locais e cancela seus alarmes.",18));root.addView(text("São usadas permissões de alarme exato, notificações, tela cheia, reinicialização, vibração, ativação breve da tela e serviço de primeiro plano para tentar apresentar os alarmes. O nome do próximo compromisso e o horário de acordar podem aparecer na tela bloqueada; ajuste a privacidade nas notificações do aparelho. As regras variam conforme a versão do Android e o fabricante.",18));button("Voltar",this::home);}
 void open(Intent i){try{startActivity(i);}catch(Exception e){toast("Não foi possível abrir os ajustes neste aparelho.");}}void toast(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
}
