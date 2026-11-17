package br.com.podedormir;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;

/** Histórico local limitado. Os registros mostram o que o aplicativo observou,
 * não comprovam que um alerta foi ouvido ou que o Android manterá o agendamento. */
final class History {
    private static final String STORE="historico", KEY="registros";
    private static final int LIMIT=250;
    private History() {}
    static synchronized boolean storageValid(Context c){
        try{JSONArray entries=new JSONArray(c.getSharedPreferences(STORE,0).getString(KEY,"[]"));for(int i=0;i<entries.length();i++){JSONObject e=entries.getJSONObject(i);if(!e.has("quando")||e.getLong("quando")<=0||!e.has("detalhe"))return false;}return true;}
        catch(Exception ex){return false;}
    }
    static synchronized JSONArray load(Context c) {
        try{return new JSONArray(c.getSharedPreferences(STORE,0).getString(KEY,"[]"));}
        catch(Exception e){return new JSONArray();}
    }
    static synchronized boolean add(Context c,String detail) {
        try {
            if(!storageValid(c))return false;
            JSONArray old=load(c), next=new JSONArray();
            int start=Math.max(0,old.length()-(LIMIT-1));
            for(int i=start;i<old.length();i++)next.put(old.get(i));
            JSONObject entry=new JSONObject();entry.put("quando",System.currentTimeMillis());entry.put("detalhe",detail);next.put(entry);
            return c.getSharedPreferences(STORE,0).edit().putString(KEY,next.toString()).commit();
        }catch(Exception ex){return false;}
    }
    static synchronized boolean clear(Context c) {return c.getSharedPreferences(STORE,0).edit().clear().commit();}
}
