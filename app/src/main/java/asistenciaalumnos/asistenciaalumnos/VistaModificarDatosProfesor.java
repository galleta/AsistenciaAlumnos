package asistenciaalumnos.asistenciaalumnos;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;

import asistenciaalumnos.asistenciaalumnos.adaptadores.AdaptadorQuitarAsignaturaProfesor;
import controlador.ControladorProfesor;
import internet.AccesoInternet;
import internet.ServidorPHPException;
import modelo.asignatura.Asignatura;
import modelo.profesor.Profesor;
import utilidades.SpaceItemDecoration;
import utilidades.Utilidades;

public class VistaModificarDatosProfesor extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener
{
    private Toolbar toolbar_modificarprofesor;
    private Profesor profesor;
    private EditText tNombreProfConProf, tEmailProfConProf;
    private LinearLayout layoutErrorInternetConProf;
    private Spinner spTipoProfModProf, spCiclosModProf, spCursoModProf, spAsignaturaModProf;
    private Button bModificarProf, bMatricularModProf;
    private ControladorProfesor controladorp;
    private AccesoInternet accesointernet;
    private String cicloelegido = "SMR", cursoelegido = "PRIMERO", asignaturaelegida = "", emailprofesor;
    private RecyclerView listaasignaturasquitar;
    private AdaptadorQuitarAsignaturaProfesor adaptador;
    private ArrayList<Asignatura> asignaturas;
    private Profesor profaux;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_profesor);

        // ***** Obtengo los recursos de la actividad *****
        toolbar_modificarprofesor = (Toolbar) findViewById(R.id.toolbar_modificarprofesor);
        tNombreProfConProf = (EditText) findViewById(R.id.tNombreProfConProf);
        tEmailProfConProf = (EditText) findViewById(R.id.tEmailProfConProf);
        layoutErrorInternetConProf = (LinearLayout) findViewById(R.id.layoutErrorInternetConProf);
        spTipoProfModProf = (Spinner) findViewById(R.id.spTipoProfModProf);
        spCiclosModProf = (Spinner) findViewById(R.id.spCiclosModProf);
        spCursoModProf = (Spinner) findViewById(R.id.spCursoModProf);
        spAsignaturaModProf = (Spinner) findViewById(R.id.spAsignaturaModProf);
        bModificarProf = (Button) findViewById(R.id.bModificarProf);
        bMatricularModProf = (Button) findViewById(R.id.bMatricularModProf);
        listaasignaturasquitar = (RecyclerView) findViewById(R.id.listaasignaturasquitar);
        // ************************************************

        toolbar_modificarprofesor.setTitle(Utilidades.obtenerStringXML(this, R.string.title_activity_modificar_profesor));
        toolbar_modificarprofesor.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar_modificarprofesor.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        accesointernet = new AccesoInternet(this);
        asignaturas = new ArrayList<>();

        if( accesointernet.verificarConexion().equals(AccesoInternet.TipoConexionInternet.NO_CONEXION) )
        {
            layoutErrorInternetConProf.setVisibility(View.VISIBLE);
        }
        else
        {
            layoutErrorInternetConProf.setVisibility(View.GONE);
            Utilidades.rellenarSpinner(spTipoProfModProf, R.array.tipos_profesor, this);
            Utilidades.rellenarSpinner(spCiclosModProf, R.array.ciclos, this);
            Utilidades.rellenarSpinner(spCursoModProf, R.array.cursos, this);

            spAsignaturaModProf.setOnItemSelectedListener(this);
            spCiclosModProf.setOnItemSelectedListener(this);
            spCursoModProf.setOnItemSelectedListener(this);
            bModificarProf.setOnClickListener(this);
            bMatricularModProf.setOnClickListener(this);

            controladorp = new ControladorProfesor(this);

            profaux = (Profesor)getIntent().getExtras().getSerializable("profesor");
            emailprofesor = profaux.getEmail();

            try
            {
                // Obtengo el token del profesor
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String token = prefs.getString("token", "");

                profesor = controladorp.obtenerProfesorPorEmail(token, profaux.getEmail());

                tNombreProfConProf.setText(profesor.getNombre());
                tEmailProfConProf.setText(profesor.getEmail());
                if( profesor.getTipo().equals("ADMINISTRADOR") )
                    spTipoProfModProf.setSelection(0);
                else
                    spTipoProfModProf.setSelection(1);

                listaasignaturasquitar.addItemDecoration(new SpaceItemDecoration(this, R.dimen.list_space, true, true));
                // Con esto el tamaño del recyclerwiew no cambiará
                listaasignaturasquitar.setHasFixedSize(true);
                // Creo un layoutmanager para el recyclerview
                LinearLayoutManager llm = new LinearLayoutManager(this);
                listaasignaturasquitar.setLayoutManager(llm);

                mostrarAsignaturasImpartidas();
            }
            catch (ServidorPHPException e)
            {
                e.printStackTrace();
            }

        }
    }

    public void mostrarAsignaturasImpartidas() throws ServidorPHPException
    {
        profaux = (Profesor)getIntent().getExtras().getSerializable("profesor");
        emailprofesor = profaux.getEmail();

        asignaturas.clear();

        // Obtengo el token del profesor
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String token = prefs.getString("token", "");

        profesor = controladorp.obtenerProfesorPorEmail(token, profaux.getEmail());

        // Creo el array con todas las asignaturas que imparte el profesor
        for(String asig : profesor.getAsignaturas1smr())
        {
            if( !asig.equals("Ninguna") )
            {
                Asignatura a = new Asignatura(0, asig, "", "PRIMERO", "SMR");
                asignaturas.add(a);
            }
        }

        for(String asig : profesor.getAsignaturas2smr())
        {
            if( !asig.equals("Ninguna") )
            {
                Asignatura a = new Asignatura(0, asig, "", "SEGUNDO", "SMR");
                asignaturas.add(a);
            }
        }

        for(String asig : profesor.getAsignaturas1dam())
        {
            if( !asig.equals("Ninguna") )
            {
                Asignatura a = new Asignatura(0, asig, "", "PRIMERO", "DAM");
                asignaturas.add(a);
            }
        }

        for(String asig : profesor.getAsignaturas2dam())
        {
            if( !asig.equals("Ninguna") )
            {
                Asignatura a = new Asignatura(0, asig, "", "SEGUNDO", "DAM");
                asignaturas.add(a);
            }
        }

        adaptador = new AdaptadorQuitarAsignaturaProfesor(emailprofesor, asignaturas, this, this);
        listaasignaturasquitar.setAdapter(adaptador);
        adaptador.refrescar();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.bModificarProf:
                try
                {
                    // Obtengo el token del profesor
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    String token = prefs.getString("token", "");

                    String nuevonombre = tNombreProfConProf.getText().toString();
                    String nuevotipo = spTipoProfModProf.getSelectedItem().toString();

                    if( controladorp.modificarProfesor(token, emailprofesor, nuevonombre, nuevotipo) )
                    {
                        Utilidades.mostrarToastText(this, Utilidades.obtenerStringXML(this, R.string.texto_profesor_modificado_ok));
                        mostrarAsignaturasImpartidas();
                    }
                    else
                    {
                        Utilidades.mostrarToastText(this, Utilidades.obtenerStringXML(this, R.string.texto_profesor_modificado_nook));
                    }
                }
                catch (ServidorPHPException e)
                {
                    Utilidades.mostrarToastText(this, Utilidades.obtenerStringXML(this, R.string.texto_profesor_modificado_nook));
                }
                break;
            case R.id.bMatricularModProf:
                try
                {
                    // Obtengo el token del profesor
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    String token = prefs.getString("token", "");

                    if( controladorp.matricularProfesorAsignatura(token, emailprofesor, cicloelegido, cursoelegido, asignaturaelegida) )
                    {
                        Utilidades.mostrarToastText(this, Utilidades.obtenerStringXML(this, R.string.texto_profesor_matriculado_ok));
                        mostrarAsignaturasImpartidas();
                    }
                    else
                    {
                        Utilidades.mostrarToastText(this, Utilidades.obtenerStringXML(this, R.string.texto_profesor_matriculado_nook));
                    }
                }
                catch (ServidorPHPException e)
                {
                    Utilidades.mostrarToastText(this, Utilidades.obtenerStringXML(this, R.string.texto_profesor_matriculado_nook));
                }
                break;
        }
    }

    /**
     * Rellena las asignaturas del ciclo y curso elegido
     */
    private void rellenarAsignaturas()
    {
        if( cicloelegido.equals("SMR") && cursoelegido.equals("PRIMERO") )
        {
            Utilidades.rellenarSpinner(spAsignaturaModProf, R.array.asignaturas_1SMR, this);
        }
        else
        if( cicloelegido.equals("SMR") && cursoelegido.equals("SEGUNDO") )
        {
            Utilidades.rellenarSpinner(spAsignaturaModProf, R.array.asignaturas_2SMR, this);
        }
        else
        if( cicloelegido.equals("DAM") && cursoelegido.equals("PRIMERO") )
        {
            Utilidades.rellenarSpinner(spAsignaturaModProf, R.array.asignaturas_1DAM, this);
        }
        else
        if( cicloelegido.equals("DAM") && cursoelegido.equals("SEGUNDO") )
        {
            Utilidades.rellenarSpinner(spAsignaturaModProf, R.array.asignaturas_2DAM, this);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        switch(parent.getId())
        {
            case R.id.spCiclosModProf:
                cicloelegido = spCiclosModProf.getSelectedItem().toString();
                rellenarAsignaturas();
                break;
            case R.id.spCursoModProf:
                cursoelegido = spCursoModProf.getSelectedItem().toString();
                rellenarAsignaturas();
                break;
            case R.id.spAsignaturaModProf:
                asignaturaelegida = spAsignaturaModProf.getSelectedItem().toString();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}
