package asistenciaalumnos.asistenciaalumnos;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by francis on 23/12/17.
 */

@SuppressWarnings("deprecation")
@TargetApi(11)
public class VistaConfiguracion extends PreferenceActivity
{
    private static int recurso_preferencias = R.xml.configuracion;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if( Build.VERSION.SDK_INT >= 11 )
        {
            LinearLayout root =
                    (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent(
                    );
            Toolbar bar = (Toolbar)
                    LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
            bar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
            root.addView(bar, 0); // insert at top
            bar.setNavigationOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    finish();
                }
            });
            addPreferencesFromResource(recurso_preferencias);
        }
        else
        {
            getFragmentManager().beginTransaction().replace(android.R.id.content,
                    new PF()).commit();
        }
    }
    public static class PF extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(recurso_preferencias);
        }
    }
}