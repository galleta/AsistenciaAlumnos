package asistenciaalumnos.asistenciaalumnos;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import asistenciaalumnos.asistenciaalumnos.adaptadores.AdaptadorProfesor;
import controlador.ControladorProfesor;
import internet.AccesoInternet;
import internet.ServidorPHPException;
import modelo.profesor.Profesor;
import utilidades.SpaceItemDecoration;
import utilidades.Utilidades;

public class VistaConsultarProfesores extends AppCompatActivity
{
    private Toolbar toolbar_consultarprofesores;
    private LinearLayout layoutErrorInternetConsulProf;
    private RecyclerView listaprofesores;
    private AccesoInternet accesointernet;
    private ControladorProfesor controladorp;
    private AdaptadorProfesor adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_profesores);

        // ***** Obtengo los recursos de la actividad *****
        toolbar_consultarprofesores = (Toolbar) findViewById(R.id.toolbar_consultarprofesores);
        layoutErrorInternetConsulProf = (LinearLayout) findViewById(R.id.layoutErrorInternetConsulProf);
        listaprofesores = (RecyclerView) findViewById(R.id.listaprofesores);
        // ************************************************

        toolbar_consultarprofesores.setTitle(Utilidades.obtenerStringXML(this, R.string.title_activity_consultar_profesores));
        toolbar_consultarprofesores.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar_consultarprofesores.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        controladorp = new ControladorProfesor(this);
        accesointernet = new AccesoInternet(this);

        if( accesointernet.verificarConexion().equals(AccesoInternet.TipoConexionInternet.NO_CONEXION) )
        {
            layoutErrorInternetConsulProf.setVisibility(View.VISIBLE);
        }
        else
        {
            layoutErrorInternetConsulProf.setVisibility(View.GONE);

            listaprofesores.addItemDecoration(new SpaceItemDecoration(this, R.dimen.list_space, true, true));
            // Con esto el tamaño del recyclerwiew no cambiará
            listaprofesores.setHasFixedSize(true);
            // Creo un layoutmanager para el recyclerview
            LinearLayoutManager llm = new LinearLayoutManager(this);
            listaprofesores.setLayoutManager(llm);

            mostrarProfesores();
        }
    }

    /**
     * Muestra en una lista los profesores que hay en el sistema dados de alta
     */
    public void mostrarProfesores()
    {
        if( !accesointernet.verificarConexion().equals(AccesoInternet.TipoConexionInternet.NO_CONEXION) )
        {
            // Obtengo los datos que están guardados del profesor
            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(this);
            String email = prefs.getString("usuario", "");
            String token = prefs.getString("token", "");
            String tipoprofesor = "PROFESOR";

            try
            {
                // Si el profesor es el director que no pueda modificar las asistencias
                tipoprofesor = controladorp.obtenerProfesorPorEmail(token, email).getTipo();
            }
            catch (ServidorPHPException e)
            {
                System.out.println("Error -> " + e.toString());
            }

            try
            {
                ArrayList<Profesor> profes = controladorp.obtenerTodosProfesores(token);

                switch (tipoprofesor)
                {
                    case "PROFESOR":
                    case "ADMINISTRADOR":
                        adaptador = new AdaptadorProfesor(profes, this, Boolean.TRUE, this);
                        break;
                    case "DIRECTOR":
                        adaptador = new AdaptadorProfesor(profes, this, Boolean.FALSE, this);
                        break;
                }

                //System.out.println(profes.size());

                listaprofesores.setAdapter(adaptador);
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

        mostrarProfesores();
    }

}
