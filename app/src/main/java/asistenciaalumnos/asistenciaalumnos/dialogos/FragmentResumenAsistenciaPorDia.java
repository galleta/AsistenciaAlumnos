package asistenciaalumnos.asistenciaalumnos.dialogos;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import asistenciaalumnos.asistenciaalumnos.R;
import controlador.ControladorAlumno;
import controlador.ControladorAsignatura;
import controlador.ControladorAsistencia;
import controlador.ControladorProfesor;
import modelo.alumno.Alumno;
import modelo.asignatura.Asignatura;
import modelo.profesor.Profesor;
import utilidades.SpaceItemDecoration;
import asistenciaalumnos.asistenciaalumnos.adaptadores.AdaptadorAlumnosAsistenciaPorDia;
import modelo.asistencia.AsistenciaAsignaturaDia;
import internet.AccesoInternet;
import internet.ServidorPHPException;
import utilidades.Utilidades;

/**
 * Created by francis on 25/09/16.
 */

public class FragmentResumenAsistenciaPorDia extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener
{
    private final String TAG = "FragmentResumenAsistenciaPorDia";
    private AccesoInternet accesointernet;
    private LinearLayout layoutErrorInternetDia, layoutNoDatos;
    private Spinner spCiclosDia, spCursoDia, spAsignaturaDia;
    private String cicloelegido = "SMR", cursoelegido = "PRIMERO";
    private Context contexto;
    private TextView tFechaAsistenciaDia;
    private ImageButton ibCambiarFechaDia, ibRecargar;
    private RecyclerView listaalumnosdia;
    private AdaptadorAlumnosAsistenciaPorDia adaptador;
    private ControladorAsignatura controladorasig;
    private ControladorProfesor controladorp;
    private ControladorAlumno controladoralum;
    private ControladorAsistencia controladorasis;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;
    private String email;
    private ArrayList<Asignatura> asignaturas;
    private ArrayList<String> asignaturasnombres;
    private Boolean mostrado, primeravez;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflo el layout de la pestaña
        View v = inflater.inflate(R.layout.contenido_resumen_asistencia_dia, container, false);

        // ***** Obtengo los recursos de la aplicación *****
        layoutErrorInternetDia = v.findViewById(R.id.layoutErrorInternetDia);
        layoutNoDatos = v.findViewById(R.id.layoutNoDatos);
        spCiclosDia = v.findViewById(R.id.spCiclosDia);
        spCursoDia = v.findViewById(R.id.spCursoDia);
        spAsignaturaDia = v.findViewById(R.id.spAsignaturaDia);
        tFechaAsistenciaDia = v.findViewById(R.id.tFechaAsistenciaDia);
        ibCambiarFechaDia = v.findViewById(R.id.ibCambiarFechaDia);
        ibRecargar = v.findViewById(R.id.ibRecargar);
        listaalumnosdia = v.findViewById(R.id.listaalumnosdia);
        // *************************************************

        accesointernet = new AccesoInternet(getContext());
        contexto = getContext();
        controladorasig = new ControladorAsignatura(contexto);
        controladorp = new ControladorProfesor(contexto);
        controladoralum = new ControladorAlumno(contexto);
        controladorasis = new ControladorAsistencia(contexto);
        asignaturas = new ArrayList<>();
        asignaturasnombres = new ArrayList<>();
        mostrado = Boolean.FALSE;
        primeravez = Boolean.TRUE;

        if( accesointernet.verificarConexion().equals(AccesoInternet.TipoConexionInternet.NO_CONEXION) )
        {
            layoutErrorInternetDia.setVisibility(View.VISIBLE);
        }
        else
        {
            spCiclosDia.setOnItemSelectedListener(this);
            spAsignaturaDia.setOnItemSelectedListener(this);
            spCursoDia.setOnItemSelectedListener(this);
            layoutErrorInternetDia.setVisibility(View.GONE);

            Utilidades.rellenarSpinner(spCiclosDia, R.array.ciclos, contexto);
            Utilidades.rellenarSpinner(spCursoDia, R.array.cursos, contexto);
            Utilidades.rellenarSpinner(spAsignaturaDia, R.array.asignaturas_1SMR, contexto);

            // Obtengo el usuario y lo pongo en la cabecera del menu
            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(
                            contexto);
            email = prefs.getString("usuario", "");

            String fechaactual = Utilidades.formatofecha.format(new Date());
            tFechaAsistenciaDia.setText(fechaactual);

            obtenerAsignaturasImpartidas();

            spCursoDia.setOnItemSelectedListener(this);
            spAsignaturaDia.setOnItemSelectedListener(this);
            spCiclosDia.setOnItemSelectedListener(this);

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

            ibCambiarFechaDia.setOnClickListener(this);
            ibRecargar.setOnClickListener(this);

            listaalumnosdia.addItemDecoration(new SpaceItemDecoration(contexto, R.dimen.list_space, true, true));
            // Con esto el tamaño del recyclerwiew no cambiará
            listaalumnosdia.setHasFixedSize(true);
            // Creo un layoutmanager para el recyclerview
            LinearLayoutManager llm = new LinearLayoutManager(contexto);
            listaalumnosdia.setLayoutManager(llm);
        }

        return v;
    }

    /**
     * Elimina todos los elementos de un RecyclerView
     */
    public void limpiarLista()
    {
        listaalumnosdia.removeAllViewsInLayout();
    }

    /**
     * Actualiza la fecha mostrada en la interfaz de la pantalla
     */
    private void actualizarFecha()
    {
        tFechaAsistenciaDia.setText(Utilidades.formatofecha.format(myCalendar.getTime()));
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.ibCambiarFechaDia:
                new DatePickerDialog(contexto, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.ibRecargar:
                mostrado = Boolean.FALSE;
                mostrarAsistenciasAlumnosPorDia();
                break;
        }
    }

    /**
     * Obtiene las asignaturas impartidas del profesor en el ciclo y curso elegidos
     * y las muestra en el spinner
     */
    private void obtenerAsignaturasImpartidas()
    {
        try
        {
            // Obtengo los datos que están guardados del profesor
            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(getContext());
            String email = prefs.getString("usuario", "");
            String token = prefs.getString("token", "");

            Profesor profe = controladorp.obtenerProfesorPorEmail(token, email);
            System.out.println("EL TIPO ES " + profe.getTipo());

            asignaturasnombres.clear();

            // Compruebo si es profesor, director, tutor (próximamente)
            switch (profe.getTipo())
            {
                case "DIRECTOR":
                    //System.out.println("Es director");
                    // Si es el director tiene todas las asignaturas de todos los cursos para ver
                    if( cicloelegido.equals("SMR") && cursoelegido.equals("PRIMERO") )
                    {
                        String[] foo_array = getContext().getResources().getStringArray(R.array.asignaturas_1SMR);
                        Collections.addAll(asignaturasnombres, foo_array);
                    }
                    if( cicloelegido.equals("SMR") && cursoelegido.equals("SEGUNDO") )
                    {
                        String[] foo_array = getContext().getResources().getStringArray(R.array.asignaturas_2SMR);
                        Collections.addAll(asignaturasnombres, foo_array);
                    }
                    if( cicloelegido.equals("DAM") && cursoelegido.equals("PRIMERO") )
                    {
                        String[] foo_array = getContext().getResources().getStringArray(R.array.asignaturas_1DAM);
                        Collections.addAll(asignaturasnombres, foo_array);
                    }
                    if( cicloelegido.equals("DAM") && cursoelegido.equals("SEGUNDO") )
                    {
                        String[] foo_array = getContext().getResources().getStringArray(R.array.asignaturas_2DAM);
                        Collections.addAll(asignaturasnombres, foo_array);
                    }
                    break;
                case "PROFESOR":
                case "ADMINISTRADOR":
                    asignaturas = controladorasig.obtenerTodasAsignaturasProfesorCicloCurso(token, email, cicloelegido, cursoelegido);
                    if( asignaturas.size() == 0 )
                    {
                        asignaturasnombres.add("-");
                    }
                    else
                    {
                        for(Asignatura a : asignaturas)
                        {
                            asignaturasnombres.add(a.getNombreAbreviado());
                        }
                    }
                    Utilidades.rellenarSpinner(spAsignaturaDia, asignaturasnombres, contexto);
                    break;
            }
        }
        catch (ServidorPHPException e)
        {
            System.out.println("Error obteniendo las asignaturas impartidas: " + e.toString());
        }
    }

    /**
     * Muestra las asistencias de los alumnos que hay matriculados en la asignatura elegida en la fecha elegida
     */
    private void mostrarAsistenciasAlumnosPorDia()
    {
        if( accesointernet.verificarConexion().equals(AccesoInternet.TipoConexionInternet.NO_CONEXION) )
        {
            layoutErrorInternetDia.setVisibility(View.VISIBLE);
        }
        else
        {
            layoutErrorInternetDia.setVisibility(View.GONE);
            try
            {
                if( !mostrado )
                {
                    mostrado = Boolean.TRUE;
                    // Obtengo el token del profesor
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(contexto);
                    String token = prefs.getString("token", "");

                    String asignaturaelegida = spAsignaturaDia.getSelectedItem().toString();

                    if( !asignaturaelegida.equals("-") )
                    {
                        Asignatura asignatura = controladorasig.obtenerAsignatura(token, asignaturaelegida, cicloelegido, cursoelegido);
                        ArrayList<Alumno> alumnosmatriculados = controladoralum.obtenerAlumnosMatriculadosAsignatura(token, spAsignaturaDia.getSelectedItem().toString(), cursoelegido, cicloelegido);

                        ArrayList<AsistenciaAsignaturaDia> todasasistencias = new ArrayList<>();
                        String fechaelegida = tFechaAsistenciaDia.getText().toString();
                        for(Alumno alumno : alumnosmatriculados)
                        {
                            todasasistencias.addAll(controladorasis.calcularAsistenciaAlumnoAsignaturaDia(token, alumno.getId(), asignatura.getId(), fechaelegida));
                        }

                        if( todasasistencias.size() > 0 )
                        {
                            layoutNoDatos.setVisibility(View.GONE);
                            listaalumnosdia.setVisibility(View.VISIBLE);
                            adaptador = new AdaptadorAlumnosAsistenciaPorDia(contexto, todasasistencias);
                            listaalumnosdia.setAdapter(adaptador);
                            adaptador.refrescar();
                        }
                        else
                        {
                            layoutNoDatos.setVisibility(View.VISIBLE);
                            listaalumnosdia.setVisibility(View.GONE);
                        }
                    }
                }
            }
            catch (ServidorPHPException e)
            {
                System.out.println(TAG + " -> Error obteniendo las asistencias: " + e.toString());
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        switch(parent.getId())
        {
            case R.id.spCiclosDia:
                cicloelegido = spCiclosDia.getSelectedItem().toString();
                obtenerAsignaturasImpartidas();
                Utilidades.rellenarSpinner(spAsignaturaDia, asignaturasnombres, contexto);
                break;
            case R.id.spCursoDia:
                cursoelegido = spCursoDia.getSelectedItem().toString();
                obtenerAsignaturasImpartidas();
                Utilidades.rellenarSpinner(spAsignaturaDia, asignaturasnombres, contexto);
                break;
            case R.id.spAsignaturaDia:
                break;
        }
        limpiarLista();
        ibRecargar.performClick();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }
}
