package br.com.podedormir;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
final class ViewLookup {
 static <T extends View> List<T> all(View view,Class<T> type){List<T> found=new ArrayList<>();walk(view,type,found);return found;}
 private static <T extends View> void walk(View v,Class<T> t,List<T> out){if(t.isInstance(v))out.add(t.cast(v));if(v instanceof ViewGroup){ViewGroup group=(ViewGroup)v;for(int i=0;i<group.getChildCount();i++)walk(group.getChildAt(i),t,out);}}
 static Button button(View view,String title){for(Button b:all(view,Button.class))if(title.contentEquals(b.getText()))return b;throw new AssertionError("Botão não encontrado: "+title);}
 static TextView textMatching(View view,String regex){for(TextView t:all(view,TextView.class))if(t.getText().toString().matches(regex))return t;throw new AssertionError("Texto não encontrado: "+regex);}
 static EditText field(View view,int position){return all(view,EditText.class).get(position);}
}
