package asistenciaalumnos.asistenciaalumnos;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

import asistenciaalumnos.asistenciaalumnos.adaptadores.AdaptadorProfesor;
import asistenciaalumnos.asistenciaalumnos.dialogos.DialogoCambiarPasswordProfesor;
import controlador.ControladorProfesor;
import internet.AccesoInternet;
import internet.ServidorPHPException;
import modelo.profesor.Profesor;
import utilidades.SpaceItemDecoration;
import utilidades.Utilidades;

public class VistaMisDatosProfesor extends AppCompatActivity implements View.OnClickListener
{
    private Toolbar toolbar_profesormisdatos;
    private LinearLayout layoutErrorInternetConsulProfDatos;
    private RecyclerView listaprofesordatos;
    private AccesoInternet accesointernet;
    private ControladorProfesor controladorp;
    private AdaptadorProfesor adaptador;
    private String emailprofesor;
    private Button bCambiarContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profesor_mis_datos);

        // ***** Obtengo los recursos de la actividad *****
        toolbar_profesormisdatos = (Toolbar) findViewById(R.id.toolbar_profesormisdatos);
        layoutErrorInternetConsulProfDatos = (LinearLayout) findViewById(R.id.layoutErrorInternetConsulProfDatos);
        listaprofesordatos = (RecyclerView) findViewById(R.id.listaprofesordatos);
        bCambiarContrasena = (Button) findViewById(R.id.bCambiarContrasena);
        // ************************************************

        toolbar_profesormisdatos.setTitle(Utilidades.obtenerStringXML(this, R.string.title_activity_profesor_mis_datos));
        toolbar_profesormisdatos.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar_profesormisdatos.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        controladorp = new ControladorProfesor(this);
        accesointernet = new AccesoInternet(this);
        emailprofesor = "";

        if( accesointernet.verificarConexion().equals(AccesoInternet.TipoConexionInternet.NO_CONEXION) )
        {
            layoutErrorInternetConsulProfDatos.setVisibility(View.VISIBLE);
        }
        else
        {
            // Obtengo el usuario y lo pongo en la cabecera del menu
            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(
                            this);
            emailprofesor = prefs.getString("usuario", "");

            layoutErrorInternetConsulProfDatos.setVisibility(View.GONE);

            listaprofesordatos.addItemDecoration(new SpaceItemDecoration(this, R.dimen.list_space, true, true));
            // Con esto el tamaño del recyclerwiew no cambiará
            listaprofesordatos.setHasFixedSize(true);
            // Creo un layoutmanager para el recyclerview
            LinearLayoutManager llm = new LinearLayoutManager(this);
            listaprofesordatos.setLayoutManager(llm);

            bCambiarContrasena.setOnClickListener(this);

            mostrarProfesorEmail(emailprofesor);
        }
    }

    /**
     * Muestra los datos de un profesor
     * @param email Email del profesor
     */
    public void mostrarProfesorEmail(String email)
    {
        if( !accesointernet.verificarConexion().equals(AccesoInternet.TipoConexionInternet.NO_CONEXION) )
        {
            try
            {
                // Obtengo el token del profesor
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String token = prefs.getString("token", "");

                Profesor profe = controladorp.obtenerProfesorPorEmail(token, email);
                ArrayList<Profesor> profes = new ArrayList<>();
                profes.add(profe);
                adaptador = new AdaptadorProfesor(profes, this, Boolean.FALSE, null);
                listaprofesordatos.setAdapter(adaptador);
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

        mostrarProfesorEmail(emailprofesor);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.bCambiarContrasena:
                FragmentManager fragmentManager = getFragmentManager();
                DialogoCambiarPasswordProfesor dialogo2 = new DialogoCambiarPasswordProfesor(this);
                dialogo2.show(fragmentManager, "tagAlerta");
                break;
        }
    }
}
