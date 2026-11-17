package br.com.podedormir;
import android.app.Notification;
import android.content.Context;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class) @Config(sdk=35)
public class AlarmNotificationApi35Test {
 @Test public void fullScreenNotificationContainsPublicEventAndTime()throws Exception{
  Context c=RuntimeEnvironment.getApplication();c.getSharedPreferences("eventos",0).edit().clear().commit();long t=System.currentTimeMillis()+60000;Alarms.put(c,new JSONObject().put("id","a").put("nome","Consulta").put("hora",t));Notification n=AlarmService.notice(c,"a","Consulta");assertNotNull(n.fullScreenIntent);assertEquals(Notification.VISIBILITY_PUBLIC,n.visibility);assertTrue(n.extras.getCharSequence(Notification.EXTRA_TEXT).toString().contains(UpcomingNotification.wakeText(t)));
 }
}
