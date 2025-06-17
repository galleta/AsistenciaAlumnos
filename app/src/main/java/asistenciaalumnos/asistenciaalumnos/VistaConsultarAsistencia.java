package asistenciaalumnos.asistenciaalumnos;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import asistenciaalumnos.asistenciaalumnos.adaptadores.AdaptadorPestanasResumenAsistencia;
import utilidades.Utilidades;

public class VistaConsultarAsistencia extends AppCompatActivity
{
    private Toolbar toolbar_consultarasistencia;
    private TabLayout tab_layout_horas;
    private AdaptadorPestanasResumenAsistencia adapter;
    private ViewPager pager_resumenes;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_asistencia);

        // ***** Obtengo los recursos de la aplicación *****
        toolbar_consultarasistencia = (Toolbar) findViewById(R.id.toolbar_consultarasistencia);
        tab_layout_horas = (TabLayout) findViewById(R.id.tab_layout_horas);
        pager_resumenes = (ViewPager) findViewById(R.id.pager_resumenes);
        // *************************************************

        //setSupportActionBar(toolbar_agregarhoras);
        toolbar_consultarasistencia.setTitle(Utilidades.obtenerStringXML(this, R.string.title_activity_consultar_asistencia));
        toolbar_consultarasistencia.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar_consultarasistencia.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Inserto las pestañas
        tab_layout_horas.addTab(tab_layout_horas.newTab().setText("Por día"));
        tab_layout_horas.addTab(tab_layout_horas.newTab().setText("Totales"));
        tab_layout_horas.setTabGravity(TabLayout.GRAVITY_FILL);

        adapter = new AdaptadorPestanasResumenAsistencia(getSupportFragmentManager(), tab_layout_horas.getTabCount());

        pager_resumenes.setAdapter(adapter);
        pager_resumenes.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab_layout_horas));
        tab_layout_horas.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                pager_resumenes.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {

            }
        });
    }

}
