package asistenciaalumnos.asistenciaalumnos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import asistenciaalumnos.asistenciaalumnos.adaptadores.AdaptadorAlumnosMatriculados;
import controlador.ControladorAlumno;
import modelo.alumno.Alumno;
import modelo.alumno.AlumnoMatriculado;
import internet.AccesoInternet;
import internet.ServidorPHPException;
import utilidades.SpaceItemDecoration;
import utilidades.Utilidades;

public class VistaConsultarAlumnos extends AppCompatActivity
{
    private Toolbar toolbar_consultaralumnos;
    private Spinner spCiclos, spCurso;
    private RecyclerView listaalumnos;
    private AdaptadorAlumnosMatriculados adaptador;
    private ControladorAlumno controladoralum;
    private String cicloelegido = "SMR", cursoelegido = "PRIMERO";
    private LinearLayout layoutErrorInternet2;
    private AccesoInternet accesointernet;
    private Boolean mostrarbotones;
    private TextView tCantidadAlumnosCon;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_alumnos);

        // ***** Obtengo los recursos de la aplicación *****
        toolbar_consultaralumnos = (Toolbar) findViewById(R.id.toolbar_consultaralumnos);
        spCiclos = (Spinner) findViewById(R.id.spCiclos);
        spCurso = (Spinner) findViewById(R.id.spCurso);
        listaalumnos = (RecyclerView) findViewById(R.id.listaalumnos);
        layoutErrorInternet2 = (LinearLayout) findViewById(R.id.layoutErrorInternet2);
        tCantidadAlumnosCon = findViewById(R.id.tCantidadAlumnosCon);
        // *************************************************

        // Obtengo si muestro los botones de eliminar y modificar alumnos
        Intent intent = getIntent();
        mostrarbotones = intent.getBooleanExtra("mostrarbotones", Boolean.TRUE);

        //setSupportActionBar(toolbar_agregarhoras);
        toolbar_consultaralumnos.setTitle(Utilidades.obtenerStringXML(this, R.string.title_activity_consultar_alumnos));
        toolbar_consultaralumnos.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar_consultaralumnos.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        controladoralum = new ControladorAlumno(this);
        accesointernet = new AccesoInternet(this);

        if( accesointernet.verificarConexion().equals(AccesoInternet.TipoConexionInternet.NO_CONEXION) )
        {
            layoutErrorInternet2.setVisibility(View.VISIBLE);
        }
        else
        {
            layoutErrorInternet2.setVisibility(View.GONE);

            Utilidades.rellenarSpinner(spCiclos, R.array.ciclos, this);
            Utilidades.rellenarSpinner(spCurso, R.array.cursos, this);

            listaalumnos.addItemDecoration(new SpaceItemDecoration(this, R.dimen.list_space, true, true));
            // Con esto el tamaño del recyclerwiew no cambiará
            listaalumnos.setHasFixedSize(true);
            // Creo un layoutmanager para el recyclerview
            LinearLayoutManager llm = new LinearLayoutManager(this);
            listaalumnos.setLayoutManager(llm);

            spCiclos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    cicloelegido = spCiclos.getSelectedItem().toString();
                    mostrarAlumnos();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });

            spCurso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    cursoelegido = spCurso.getSelectedItem().toString();
                    mostrarAlumnos();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });
        }
    }

    /**
     * Muestra en una lista los alumnos
     */
    public void mostrarAlumnos()
    {
        if( !accesointernet.verificarConexion().equals(AccesoInternet.TipoConexionInternet.NO_CONEXION) )
        {
            try
            {
                // Obtengo el token del profesor
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String token = prefs.getString("token", "");

                ArrayList<AlumnoMatriculado> alumnos = controladoralum.obtenerAlumnosMatriculadosPorCicloCurso(token, cicloelegido, cursoelegido);
                tCantidadAlumnosCon.setText(Utilidades.obtenerStringXML(this, R.string.texto_cantidad_alumnos) + " " + alumnos.size());

                ArrayList<AlumnoMatriculado> alums = controladoralum.obtenerAlumnosMatriculadosPorCicloCurso(token, cicloelegido, cursoelegido);

                adaptador = new AdaptadorAlumnosMatriculados(this, mostrarbotones, alums, this);
                listaalumnos.setAdapter(adaptador);
                adaptador.refrescar();
            }
            catch (ServidorPHPException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();  // Always call the superclass method first

        mostrarAlumnos();
    }

}
