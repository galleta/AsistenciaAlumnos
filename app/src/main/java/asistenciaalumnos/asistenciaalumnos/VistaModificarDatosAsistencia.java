package asistenciaalumnos.asistenciaalumnos;

import android.app.DatePickerDialog;
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

import asistenciaalumnos.asistenciaalumnos.adaptadores.AdaptadorModificarAsistencia;
import controlador.ControladorAlumno;
import controlador.ControladorAsignatura;
import controlador.ControladorAsistencia;
import controlador.ControladorProfesor;
import modelo.alumno.Alumno;
import modelo.asignatura.Asignatura;
import modelo.asistencia.Asistencia;
import internet.AccesoInternet;
import internet.ServidorPHPException;
import utilidades.SpaceItemDecoration;
import utilidades.Utilidades;

public class VistaModificarDatosAsistencia extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener
{
    private Toolbar toolbar_modificarasistencia;
    private LinearLayout layoutPrincipalModificarAsistencia;
    private ControladorAsignatura controladorasig;
    private ControladorAlumno controladoralum;
    private ControladorAsistencia controladorasis;
    private ControladorProfesor controladorp;
    private AccesoInternet accesointernet;
    private Spinner spCiclosMAsis, spCursoMAsis, spAlumnosMAsis, spAsignaturaMAsis;
    private String cicloelegido = "SMR", cursoelegido = "PRIMERO", email;
    private ArrayList<Alumno> alumnos;
    private int alumnoelegido, asignaturaelegida;
    private ImageButton ibCambiarFechaMAsis;
    private TextView tFechaMAsis;
    private Button ibConsultarAsistenciaMA;
    private RecyclerView listaasistencias;
    private AdaptadorModificarAsistencia adaptador;
    private LinearLayout lModAsistenciaPrincipal;
    private ArrayList<Asignatura> asignaturas;

    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_asistencia);

        // ***** Obtengo los recursos de la aplicación *****
        toolbar_modificarasistencia = findViewById(R.id.toolbar_modificarasistencia);
        layoutPrincipalModificarAsistencia = findViewById(R.id.layoutPrincipalModificarAsistencia);
        spCiclosMAsis = findViewById(R.id.spCiclosMAsis);
        spCursoMAsis = findViewById(R.id.spCursoMAsis);
        spAlumnosMAsis = findViewById(R.id.spAlumnosMAsis);
        spAsignaturaMAsis = findViewById(R.id.spAsignaturaMAsis);
        ibCambiarFechaMAsis = findViewById(R.id.ibCambiarFechaMAsis);
        tFechaMAsis = findViewById(R.id.tFechaMAsis);
        ibConsultarAsistenciaMA = findViewById(R.id.ibConsultarAsistenciaMA);
        listaasistencias = findViewById(R.id.listaasistencias);
        lModAsistenciaPrincipal =  findViewById(R.id.lModAsistenciaPrincipal);
        // *************************************************

        toolbar_modificarasistencia.setTitle(Utilidades.obtenerStringXML(this, R.string.title_activity_modificar_asistencia));
        toolbar_modificarasistencia.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar_modificarasistencia.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        controladorasig = new ControladorAsignatura(this);
        controladoralum = new ControladorAlumno(this);
        accesointernet = new AccesoInternet(this);
        controladorasis = new ControladorAsistencia(this);
        controladorp = new ControladorProfesor(this);
        asignaturas = new ArrayList<>();
        alumnos = new ArrayList<>();

        if( accesointernet.verificarConexion().equals(AccesoInternet.TipoConexionInternet.NO_CONEXION) )
        {
            layoutPrincipalModificarAsistencia.setVisibility(View.VISIBLE);
        }
        else
        {
            layoutPrincipalModificarAsistencia.setVisibility(View.GONE);

            Utilidades.rellenarSpinner(spCiclosMAsis, R.array.ciclos, this);
            Utilidades.rellenarSpinner(spCursoMAsis, R.array.cursos, this);

            String fechaactual = Utilidades.formatofecha.format(new Date());
            tFechaMAsis.setText(fechaactual);

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

            spAlumnosMAsis.setOnItemSelectedListener(this);
            spCiclosMAsis.setOnItemSelectedListener(this);
            spCursoMAsis.setOnItemSelectedListener(this);
            spAsignaturaMAsis.setOnItemSelectedListener(this);
            ibCambiarFechaMAsis.setOnClickListener(this);
            ibConsultarAsistenciaMA.setOnClickListener(this);

            listaasistencias.addItemDecoration(new SpaceItemDecoration(this, R.dimen.list_space, true, true));
            // Con esto el tamaño del recyclerwiew no cambiará
            listaasistencias.setHasFixedSize(true);
            // Creo un layoutmanager para el recyclerview
            LinearLayoutManager llm = new LinearLayoutManager(this);
            listaasistencias.setLayoutManager(llm);

            // Obtengo los datos que están guardados del profesor
            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(this);
            email = prefs.getString("usuario", "");
            String token = prefs.getString("token", "");

            try
            {
                // Si el profesor es el director que no pueda modificar las asistencias
                String tipoprofesor = controladorp.obtenerProfesorPorEmail(token, email).getTipo();

                if( tipoprofesor.equals("DIRECTOR") )
                    ibConsultarAsistenciaMA.setEnabled(Boolean.FALSE);
                else
                    ibConsultarAsistenciaMA.setEnabled(Boolean.TRUE);
            }
            catch (ServidorPHPException e)
            {
                System.out.println("Error -> " + e.toString());
            }

            obtenerAsignaturasImpartidas();
            mostrarAlumnos();
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

            asignaturas = controladorasig.obtenerTodasAsignaturasProfesorCicloCurso(token, email, cicloelegido, cursoelegido);
            ArrayList<String> nombres = new ArrayList<>();

            if( asignaturas.size() > 0 )
            {
                for(Asignatura asig : asignaturas)
                    nombres.add(asig.getNombreAbreviado());
                asignaturaelegida = asignaturas.get(0).getId();

                ibConsultarAsistenciaMA.setEnabled(Boolean.TRUE);
            }
            else
            {
                nombres.add("-");
                ibConsultarAsistenciaMA.setEnabled(Boolean.FALSE);
            }

            Utilidades.rellenarSpinner(spAsignaturaMAsis, nombres, this);
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

                String asignatura = spAsignaturaMAsis.getSelectedItem().toString();

                alumnos = controladoralum.obtenerAlumnosMatriculadosAsignatura(token, asignatura ,cursoelegido, cicloelegido);

                ArrayList<String> nombresalum = new ArrayList<>();
                if( alumnos.size() > 0 )
                {
                    for(Alumno alu : alumnos)
                        nombresalum.add(alu.getNombre() + " " + alu.getApellidos());
                    alumnoelegido = alumnos.get(0).getId();
                    ibConsultarAsistenciaMA.setEnabled(Boolean.TRUE);
                }
                else
                {
                    nombresalum.add("-");
                    ibConsultarAsistenciaMA.setEnabled(Boolean.FALSE);
                }
                Utilidades.rellenarSpinner(spAlumnosMAsis, nombresalum, this);
            }
            catch (ServidorPHPException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        switch(parent.getId())
        {
            case R.id.spAlumnosMAsis:
                if( alumnos.size() > 0 )
                    alumnoelegido = alumnos.get(position).getId();
                break;
            case R.id.spCiclosMAsis:
                cicloelegido = spCiclosMAsis.getSelectedItem().toString();
                obtenerAsignaturasImpartidas();
                mostrarAlumnos();
                break;
            case R.id.spCursoMAsis:
                cursoelegido = spCursoMAsis.getSelectedItem().toString();
                obtenerAsignaturasImpartidas();
                mostrarAlumnos();
                break;
            case R.id.spAsignaturaMAsis:
                if( asignaturas.size() > 0 )
                {
                    asignaturaelegida = asignaturas.get(position).getId();
                    mostrarAlumnos();
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent){}

    @Override
    public void onClick(View v)
    {
        switch( v.getId() )
        {
            case R.id.ibCambiarFechaMAsis:
                new DatePickerDialog(VistaModificarDatosAsistencia.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.ibConsultarAsistenciaMA:
                consultarAsistencias();
                break;
        }
    }

    public void consultarAsistencias()
    {
        System.out.println("Consultar las asistencias del alumno: " + alumnoelegido + " la fecha " + tFechaMAsis.getText() + " de la asignatura " + asignaturaelegida);
        try
        {
            // Obtengo el token del profesor
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String token = prefs.getString("token", "");

            ArrayList<Asistencia> asistencias = controladorasis.obtenerAsistenciasAsignaturaAlumnoFecha(token, alumnoelegido, asignaturaelegida, tFechaMAsis.getText().toString());
            if( asistencias.size() == 0 )
            {
                Utilidades.mostrarSnackBar(lModAsistenciaPrincipal, "No hay asistencias anotadas a fecha " + tFechaMAsis.getText().toString());
            }
            adaptador = new AdaptadorModificarAsistencia(this, asistencias);
            listaasistencias.setAdapter(adaptador);
            adaptador.refrescar();
        }
        catch (ServidorPHPException e)
        {
            System.out.println("Error -> " + e.toString());
        }
    }

    private void actualizarFecha()
    {
        tFechaMAsis.setText(Utilidades.formatofecha.format(myCalendar.getTime()));
    }
}
