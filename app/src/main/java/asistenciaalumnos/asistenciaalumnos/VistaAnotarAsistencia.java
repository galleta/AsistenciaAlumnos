package asistenciaalumnos.asistenciaalumnos;

import android.app.DatePickerDialog;
import android.content.Context;
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
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import asistenciaalumnos.asistenciaalumnos.adaptadores.AdaptadorAnotarAsistenciaAlumnos;
import controlador.ControladorAlumno;
import controlador.ControladorAsignatura;
import controlador.ControladorAsistencia;
import modelo.alumno.Alumno;
import modelo.asignatura.Asignatura;
import modelo.asistencia.Asistencia;
import internet.AccesoInternet;
import internet.ServidorPHPException;
import utilidades.SpaceItemDecoration;
import utilidades.Utilidades;

public class VistaAnotarAsistencia extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener
{
    private Toolbar toolbar_anotarasistencia;
    private Spinner spCiclos2, spCurso2, spAsignatura2;
    private Button bAnotarAsistencia, bResetearAsistencia;
    private RecyclerView listaalumnos2;
    private String cicloelegido = "SMR", cursoelegido = "PRIMERO";
    private AdaptadorAnotarAsistenciaAlumnos adaptador;
    private ControladorAlumno controladoralum;
    private ControladorAsignatura controladorasig;
    private ControladorAsistencia controladorasis;
    private ArrayList<Asistencia> asistencias;
    private TextView tFechaAsistencia;
    private ImageButton ibCambiarFecha;
    private Context contexto;
    private AccesoInternet accesointernet;
    private LinearLayout layoutErrorInternet3;
    private TextView tCantidadAlumnos;

    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;
    private String email;
    private ArrayList<Asignatura> asignaturas;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anotar_asistencia);

        // ***** Obtengo los recursos de la aplicación *****
        toolbar_anotarasistencia = findViewById(R.id.toolbar_anotarasistencia);
        bAnotarAsistencia = findViewById(R.id.bAnotarAsistencia);
        bResetearAsistencia = findViewById(R.id.bResetearAsistencia);
        spCiclos2 = findViewById(R.id.spCiclos2);
        spCurso2 = findViewById(R.id.spCurso2);
        spAsignatura2 = findViewById(R.id.spAsignatura2);
        listaalumnos2 = findViewById(R.id.listaalumnos2);
        tFechaAsistencia = findViewById(R.id.tFechaAsistencia);
        ibCambiarFecha = findViewById(R.id.ibCambiarFecha);
        layoutErrorInternet3 = findViewById(R.id.layoutErrorInternet3);
        tCantidadAlumnos = findViewById(R.id.tCantidadAlumnos);
        // *************************************************

        toolbar_anotarasistencia.setTitle(Utilidades.obtenerStringXML(this, R.string.title_activity_anotar_asistencia));
        toolbar_anotarasistencia.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar_anotarasistencia.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        contexto = this;
        accesointernet = new AccesoInternet(this);
        asignaturas = new ArrayList<>();

        Utilidades.rellenarSpinner(spCiclos2, R.array.ciclos, this);
        Utilidades.rellenarSpinner(spCurso2, R.array.cursos, this);
        Utilidades.rellenarSpinner(spAsignatura2, R.array.asignaturas_1SMR, this);

        // Obtengo el usuario y lo pongo en la cabecera del menu
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(
                        contexto);
        email = prefs.getString("usuario", "");

        controladoralum = new ControladorAlumno(this);
        controladorasig = new ControladorAsignatura(this);
        controladorasis = new ControladorAsistencia(this);
        asistencias = new ArrayList<>();

        if( getIntent().getExtras().getString("llamadodesde").equals("menu") )
        {
            String fechaactual = Utilidades.formatofecha.format(new Date());
            tFechaAsistencia.setText(fechaactual);
        }
        else
        {
            tFechaAsistencia.setText(getIntent().getExtras().getString("dia") + "/" + getIntent().getExtras().getString("mes") + "/" + getIntent().getExtras().getString("anio"));
        }

        listaalumnos2.addItemDecoration(new SpaceItemDecoration(this, R.dimen.list_space, true, true));
        // Con esto el tamaño del recyclerwiew no cambiará
        listaalumnos2.setHasFixedSize(true);
        // Creo un layoutmanager para el recyclerview
        LinearLayoutManager llm = new LinearLayoutManager(this);
        listaalumnos2.setLayoutManager(llm);

        spCiclos2.setOnItemSelectedListener(this);
        spCurso2.setOnItemSelectedListener(this);
        spAsignatura2.setOnItemSelectedListener(this);

        bAnotarAsistencia.setOnClickListener(this);
        bResetearAsistencia.setOnClickListener(this);
        ibCambiarFecha.setOnClickListener(this);

        myCalendar = Calendar.getInstance();

        date = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                actualizarFecha();
            }
        };

        mostrarAlumnos();

        if( accesointernet.verificarConexion().equals(AccesoInternet.TipoConexionInternet.NO_CONEXION) )
        {
            layoutErrorInternet3.setVisibility(View.VISIBLE);
        }
        else
        {
            layoutErrorInternet3.setVisibility(View.GONE);
        }
    }

    /**
     * Obtiene las asignaturas impartidas del profesor en el ciclo y curso elegidos
     */
    private void obtenerAsignaturasImpartidas()
    {
        try
        {
            // Obtengo el token del profesor
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String token = prefs.getString("token", "");

            bAnotarAsistencia.setEnabled(Boolean.TRUE);
            bResetearAsistencia.setEnabled(Boolean.TRUE);

            asignaturas = controladorasig.obtenerTodasAsignaturasProfesorCicloCurso(token, email, cicloelegido, cursoelegido);
        }
        catch (ServidorPHPException e)
        {
            System.out.println("Error obteniendo las asignaturas impartidas: " + e.toString());
        }
    }

    /**
     * Muestra en una lista los alumnos que hay matriculados de la asignatura elegida de un curso y ciclo
     */
    private void mostrarAlumnos()
    {
        if( !accesointernet.verificarConexion().equals(AccesoInternet.TipoConexionInternet.NO_CONEXION) )
        {
            try
            {
                // Obtengo el token del profesor
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String token = prefs.getString("token", "");

                String asignatura = spAsignatura2.getSelectedItem().toString();

                ArrayList<Alumno> alumnos = controladoralum.obtenerAlumnosMatriculadosAsignatura(token, asignatura ,cursoelegido, cicloelegido);
                tCantidadAlumnos.setText(Utilidades.obtenerStringXML(this, R.string.texto_cantidad_alumnos) + " " + alumnos.size());

                asistencias.clear();
                adaptador = new AdaptadorAnotarAsistenciaAlumnos(this, alumnos);
                listaalumnos2.setAdapter(adaptador);
                adaptador.refrescar();

                if( alumnos.size() == 0 )
                {
                    bAnotarAsistencia.setEnabled(Boolean.FALSE);
                    bResetearAsistencia.setEnabled(Boolean.FALSE);
                }
                else
                {
                    bAnotarAsistencia.setEnabled(Boolean.TRUE);
                    bResetearAsistencia.setEnabled(Boolean.TRUE);
                }
            }
            catch (ServidorPHPException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.bAnotarAsistencia:
                if( !accesointernet.verificarConexion().equals(AccesoInternet.TipoConexionInternet.NO_CONEXION) )
                {
                    // Obtengo el token del profesor
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    String token = prefs.getString("token", "");

                    Boolean insertadas = Boolean.FALSE;
                    asistencias = adaptador.getAsistencias();
                    //System.out.println("******************************");
                    for(Asistencia a : asistencias)
                    {
                        a.setAsignatura(spAsignatura2.getSelectedItem().toString());
                        a.setFecha(tFechaAsistencia.getText().toString());
                        a.setCiclo(spCiclos2.getSelectedItem().toString());
                        a.setCurso(spCurso2.getSelectedItem().toString());

                        try
                        {
                            insertadas |= controladorasis.insertarAsistencia(token, a.getAlumno().getId(), a.getTipoasistencia(), a.getFecha(), a.getAsignatura(), a.getCiclo(), a.getCurso());
                        }
                        catch(ServidorPHPException e)
                        {
                            insertadas = Boolean.FALSE;
                        }
                    }
                    if( !insertadas )
                    {
                        Utilidades.mostrarToastText(this, Utilidades.obtenerStringXML(this, R.string.texto_asistencia_anotada_nook));
                    }
                    else
                        Utilidades.mostrarToastText(this, Utilidades.obtenerStringXML(this, R.string.texto_asistencia_anotada_ok));
                    mostrarAlumnos();
                }
                break;
            case R.id.bResetearAsistencia:
                mostrarAlumnos();
                break;
            case R.id.ibCambiarFecha:
                new DatePickerDialog(VistaAnotarAsistencia.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
        }
    }

    private void actualizarFecha()
    {
        tFechaAsistencia.setText(Utilidades.formatofecha.format(myCalendar.getTime()));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        ArrayList<String> nombresasig;

        switch(parent.getId())
        {
            case R.id.spCiclos2:
                cicloelegido = spCiclos2.getSelectedItem().toString();
                obtenerAsignaturasImpartidas();
                nombresasig = new ArrayList<>();

                if( asignaturas.size() > 0 )
                {
                    for(Asignatura asig : asignaturas)
                        nombresasig.add(asig.getNombreAbreviado());
                }
                else
                {
                    nombresasig.add("-");
                }

                Utilidades.rellenarSpinner(spAsignatura2, nombresasig, contexto);
                break;
            case R.id.spCurso2:
                cursoelegido = spCurso2.getSelectedItem().toString();
                obtenerAsignaturasImpartidas();
                nombresasig = new ArrayList<>();

                if( asignaturas.size() > 0 )
                {
                    for(Asignatura asig : asignaturas)
                        nombresasig.add(asig.getNombreAbreviado());
                }
                else
                {
                    nombresasig.add("-");
                }

                Utilidades.rellenarSpinner(spAsignatura2, nombresasig, contexto);
                break;
            case R.id.spAsignatura2:
                cursoelegido = spCurso2.getSelectedItem().toString();
                mostrarAlumnos();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}
