package asistenciaalumnos.asistenciaalumnos;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import asistenciaalumnos.asistenciaalumnos.adaptadores.AdaptadorFaltasAsistencia;
import controlador.ControladorAlumno;
import controlador.ControladorAsignatura;
import controlador.ControladorAsistencia;
import controlador.ControladorFaltaAsistencia;
import controlador.ControladorProfesor;
import modelo.alumno.Alumno;
import modelo.asignatura.Asignatura;
import internet.AccesoInternet;
import internet.ServidorPHPException;
import modelo.faltaasistencia.FaltaAsistencia;
import modelo.profesor.Profesor;
import utilidades.SpaceItemDecoration;
import utilidades.Utilidades;

public class VistaConsultarFechaFaltas extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener
{
    private Toolbar toolbar_consultarfechafaltas;
    private AccesoInternet accesointernet;
    private LinearLayout layoutPrincipalConsultarFechaFaltas;
    private Spinner spCiclosConFaltas, spCursoConFaltas, spAlumnosConFaltas, spAsignaturasConFaltas;
    private ControladorAsignatura controladorasig;
    private ControladorAlumno controladoralum;
    private ControladorProfesor controladorp;
    private ControladorFaltaAsistencia controladorfaltas;
    private String cicloelegido = "SMR", cursoelegido = "PRIMERO", asignaturaelegida = "";
    private int alumnoelegido;
    private AdaptadorFaltasAsistencia adaptador;
    private Button ibConsultarFechaFaltas, bGenerarInformeAsistencia;
    private RecyclerView listafechafaltas;
    private TextView tCantidadFaltas;
    private String email;
    private ArrayList<Asignatura> asignaturas;
    private ArrayList<Alumno> alumnosmatriculados;
    private ArrayList<String> asignaturasnombres;

    private Activity actividad = this;

    private String destinatarioemail = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_fecha_faltas);

        // ***** Obtengo los recursos de la actividad *****
        toolbar_consultarfechafaltas = findViewById(R.id.toolbar_consultarfechafaltas);
        layoutPrincipalConsultarFechaFaltas = findViewById(R.id.layoutPrincipalConsultarFechaFaltas);
        spCiclosConFaltas = findViewById(R.id.spCiclosConFaltas);
        spCursoConFaltas = findViewById(R.id.spCursoConFaltas);
        spAlumnosConFaltas = findViewById(R.id.spAlumnosConFaltas);
        spAsignaturasConFaltas = findViewById(R.id.spAsignaturasConFaltas);
        ibConsultarFechaFaltas = findViewById(R.id.ibConsultarFechaFaltas);
        bGenerarInformeAsistencia = findViewById(R.id.bGenerarInformeAsistencia);
        listafechafaltas = findViewById(R.id.listafechafaltas);
        tCantidadFaltas = findViewById(R.id.tCantidadFaltas);
        // ************************************************

        toolbar_consultarfechafaltas.setTitle(Utilidades.obtenerStringXML(this, R.string.title_activity_consultar_fecha_faltas));
        toolbar_consultarfechafaltas.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar_consultarfechafaltas.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        accesointernet = new AccesoInternet(this);
        asignaturas = new ArrayList<>();
        asignaturasnombres = new ArrayList<>();

        // Obtengo los datos que están guardados del profesor
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(
                        this);
        email = prefs.getString("usuario", "");

        if( accesointernet.verificarConexion().equals(AccesoInternet.TipoConexionInternet.NO_CONEXION) )
        {
            layoutPrincipalConsultarFechaFaltas.setVisibility(View.VISIBLE);
        }
        else
        {
            controladorasig = new ControladorAsignatura(this);
            controladoralum = new ControladorAlumno(this);
            controladorfaltas = new ControladorFaltaAsistencia(this);
            controladorp = new ControladorProfesor(this);
            alumnosmatriculados = new ArrayList<>();

            layoutPrincipalConsultarFechaFaltas.setVisibility(View.GONE);
            Utilidades.rellenarSpinner(spCiclosConFaltas, R.array.ciclos, this);
            Utilidades.rellenarSpinner(spCursoConFaltas, R.array.cursos, this);

            spAlumnosConFaltas.setOnItemSelectedListener(this);
            spCiclosConFaltas.setOnItemSelectedListener(this);
            spCursoConFaltas.setOnItemSelectedListener(this);
            spAsignaturasConFaltas.setOnItemSelectedListener(this);
            ibConsultarFechaFaltas.setOnClickListener(this);
            bGenerarInformeAsistencia.setOnClickListener(this);

            listafechafaltas.addItemDecoration(new SpaceItemDecoration(this, R.dimen.list_space, true, true));
            // Con esto el tamaño del recyclerwiew no cambiará
            listafechafaltas.setHasFixedSize(true);
            // Creo un layoutmanager para el recyclerview
            LinearLayoutManager llm = new LinearLayoutManager(this);
            listafechafaltas.setLayoutManager(llm);

            String token = prefs.getString("token", "");

            try
            {
                // Si el profesor es el director que no pueda modificar las asistencias
                String tipoprofesor = controladorp.obtenerProfesorPorEmail(token, email).getTipo();

                switch (tipoprofesor)
                {
                    case "ADMINISTRADOR":
                    case "PROFESOR":
                        bGenerarInformeAsistencia.setVisibility(View.VISIBLE);
                        break;
                    case "DIRECTOR":
                        bGenerarInformeAsistencia.setVisibility(View.INVISIBLE);
                        break;
                }
            }
            catch (ServidorPHPException e)
            {
                System.out.println("Error -> " + e.toString());
            }

            rellenarAsignaturas();
            asignaturaelegida = spAsignaturasConFaltas.getSelectedItem().toString();
            rellenarAlumnos();
        }
    }

    /**
     * Obtiene las asignaturas impartidas del profesor en el ciclo y curso elegidos
     */
    private void obtenerAsignaturasImpartidas()
    {
        try
        {
            // Obtengo los datos que están guardados del profesor
            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(this);
            String email = prefs.getString("usuario", "");
            String token = prefs.getString("token", "");

            Profesor profe = controladorp.obtenerProfesorPorEmail(token, email);

            asignaturasnombres.clear();

            // Compruebo si es profesor, director, tutor (próximamente)
            switch (profe.getTipo())
            {
                case "DIRECTOR":
                    //System.out.println("Es director");
                    // Si es el director tiene todas las asignaturas de todos los cursos para ver
                    if( cicloelegido.equals("SMR") && cursoelegido.equals("PRIMERO") )
                    {
                        String[] foo_array = getResources().getStringArray(R.array.asignaturas_1SMR);
                        Collections.addAll(asignaturasnombres, foo_array);
                    }
                    if( cicloelegido.equals("SMR") && cursoelegido.equals("SEGUNDO") )
                    {
                        String[] foo_array = getResources().getStringArray(R.array.asignaturas_2SMR);
                        Collections.addAll(asignaturasnombres, foo_array);
                    }
                    if( cicloelegido.equals("DAM") && cursoelegido.equals("PRIMERO") )
                    {
                        String[] foo_array = getResources().getStringArray(R.array.asignaturas_1DAM);
                        Collections.addAll(asignaturasnombres, foo_array);
                    }
                    if( cicloelegido.equals("DAM") && cursoelegido.equals("SEGUNDO") )
                    {
                        String[] foo_array = getResources().getStringArray(R.array.asignaturas_2DAM);
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
                    break;
            }
        }
        catch (ServidorPHPException e)
        {
            System.out.println("Error obteniendo las asignaturas impartidas: " + e.toString());
        }
    }

    /**
     * Rellena las asignaturas del ciclo y curso elegido
     */
    private void rellenarAsignaturas()
    {
        obtenerAsignaturasImpartidas();
        Utilidades.rellenarSpinner(spAsignaturasConFaltas, asignaturasnombres, this);
    }

    /**
     * Rellena los alumnos del curso y del ciclo especificados en el spinner de alumnos
     */
    private void rellenarAlumnos()
    {
        try
        {
            // Obtengo el token del profesor
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String token = prefs.getString("token", "");

            alumnosmatriculados.clear();
            alumnosmatriculados = controladoralum.obtenerAlumnosMatriculadosAsignatura(token, asignaturaelegida, cursoelegido, cicloelegido);
            ArrayList<String> nombres = new ArrayList<>();

            if( alumnosmatriculados.size() > 0 )
            {
                alumnoelegido = alumnosmatriculados.get(0).getId();

                for(Alumno a : alumnosmatriculados)
                {
                    nombres.add(a.getNombre() + " " + a.getApellidos());
                }
            }
            else
            {
                nombres.add("-");
            }
            Utilidades.rellenarSpinner(spAlumnosConFaltas, nombres, this);
        }
        catch (ServidorPHPException e)
        {
            System.out.println("Error -> " + e.toString());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    /**
     * Resetea los datos de las faltas de asistencia
     */
    private void resetearDatos()
    {
        adaptador = new AdaptadorFaltasAsistencia(this, new ArrayList<FaltaAsistencia>(), this);
        listafechafaltas.setAdapter(adaptador);
        adaptador.refrescar();
        tCantidadFaltas.setText("");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        switch(parent.getId())
        {
            case R.id.spAlumnosConFaltas:
                if( alumnosmatriculados != null && alumnosmatriculados.size() > 0 )
                    alumnoelegido = alumnosmatriculados.get(position).getId();
                break;
            case R.id.spCiclosConFaltas:
                cicloelegido = spCiclosConFaltas.getSelectedItem().toString();
                rellenarAlumnos();
                rellenarAsignaturas();
                break;
            case R.id.spCursoConFaltas:
                cursoelegido = spCursoConFaltas.getSelectedItem().toString();
                rellenarAlumnos();
                rellenarAsignaturas();
                break;
            case R.id.spAsignaturasConFaltas:
                asignaturaelegida = spAsignaturasConFaltas.getSelectedItem().toString();
                rellenarAlumnos();
                break;
        }
        resetearDatos();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.ibConsultarFechaFaltas:
                consultarFechaFaltas();
                break;
            case R.id.bGenerarInformeAsistencia:
                AlertDialog.Builder builder = new AlertDialog.Builder(actividad);
                builder.setTitle(Utilidades.obtenerStringXML(this, R.string.texto_pregunta_enviar_asistencia));

                // add a radio button list
                final String[] emails;
                final String[] nombres;
                try
                {
                    // Obtengo el token del profesor
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    String token = prefs.getString("token", "");

                    ArrayList<Profesor> listaprofesores = controladorp.obtenerTodosProfesores(token);
                    emails = new String[listaprofesores.size()];
                    nombres = new String[listaprofesores.size()];
                    for (int i = 0; i < listaprofesores.size(); i++)
                    {
                        emails[i] = listaprofesores.get(i).getEmail();
                        nombres[i] = listaprofesores.get(i).getNombre();
                    }

                    int checkedItem = 0;
                    destinatarioemail = emails[0];
                    builder.setSingleChoiceItems(nombres, checkedItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // user checked an item
                            destinatarioemail = emails[which];
                        }
                    });

                    final Context contexto = this;

                    // add OK and Cancel buttons
                    builder.setPositiveButton(Utilidades.obtenerStringXML(this, R.string.texto_enviar_asistencia), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // user clicked OK
                            //System.out.println("Voy a generar el informe del alumno con id " + alumnoelegido + " de la asignatura " + asignaturaelegida + " del curso " + cursoelegido + " del ciclo " + cicloelegido);
                            // Calculo el curso escolar
                            SimpleDateFormat sdf = new SimpleDateFormat("MM");
                            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");
                            int mesactual = Integer.valueOf(sdf.format(new Date()));
                            int anioactual = Integer.valueOf(sdf2.format(new Date()));
                            String cursoescolar = "";
                            if( mesactual >= 9 )
                                cursoescolar = anioactual + "/" + (anioactual+1);
                            else
                                cursoescolar = (anioactual-1) + "/" + anioactual;

                            // Genero el nombre del fichero según el alumno
                            String fichero = spAlumnosConFaltas.getSelectedItem().toString();
                            fichero = fichero.replace("Á", "A");
                            fichero = fichero.replace("É", "E");
                            fichero = fichero.replace("Í", "I");
                            fichero = fichero.replace("Ó", "O");
                            fichero = fichero.replace("Ú", "U");
                            fichero = fichero.replace("á", "a");
                            fichero = fichero.replace("é", "e");
                            fichero = fichero.replace("í", "i");
                            fichero = fichero.replace("ó", "o");
                            fichero = fichero.replace("ú", "u");
                            fichero = fichero.replace(" ", "_");
                            fichero = fichero.replace("ñ", "n");
                            fichero = fichero.replace("Ñ", "N");
                            fichero = "asistencias_" + fichero + "_" + cicloelegido + "_" + asignaturaelegida + ".pdf";

                            try
                            {
                                // Obtengo el token del profesor
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(contexto);
                                String token = prefs.getString("token", "");

                                Asignatura asigna = controladorasig.obtenerAsignatura(token, asignaturaelegida, cicloelegido, cursoelegido);

                                /*System.out.println("El id del alumno es: " + alumnoelegido);
                                System.out.println("La asignatura es: " + asigna.getId());
                                System.out.println("El curso escolar es: " + cursoescolar);
                                System.out.println("El nombre del fichero es: " + fichero);
                                System.out.println("El destinatario del email es: " + destinatarioemail);*/

                                // Envío el email
                                ControladorAsistencia controladorasis = new ControladorAsistencia(contexto);
                                Boolean resultado = controladorasis.enviarEmailPDFAsistencia(token, alumnoelegido, asigna.getId(), cursoescolar, "Android", fichero, destinatarioemail);
                                if( resultado )
                                    Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_email_enviado_ok));
                                else
                                    Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_email_enviado_nook));
                            }
                            catch (ServidorPHPException e)
                            {
                                System.out.println("Error 1 -> " + e.toString());
                            }
                        }
                    });
                    builder.setNegativeButton(Utilidades.obtenerStringXML(contexto, R.string.texto_cancelar), null);

                    // create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
                catch (ServidorPHPException e)
                {
                    System.out.println("Error 1 -> " + e.toString());
                }

                break;
        }
    }

    public void consultarFechaFaltas()
    {
        if( !spAlumnosConFaltas.getSelectedItem().toString().equals("-") && !spAsignaturasConFaltas.getSelectedItem().toString().equals("-") )
        {
            // Obtengo el token del profesor
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String token = prefs.getString("token", "");

            try
            {
                Asignatura asignatura = controladorasig.obtenerAsignatura(token, asignaturaelegida, cicloelegido, cursoelegido);
                ArrayList<FaltaAsistencia> faltas = controladorfaltas.obtenerTotalFaltasAlumnoAsignatura(token, alumnoelegido, asignatura.getId());

                adaptador = new AdaptadorFaltasAsistencia(this, faltas, this);
                listafechafaltas.setAdapter(adaptador);
                adaptador.refrescar();

                int cantidadfaltas = 0;
                for(FaltaAsistencia f : faltas)
                    cantidadfaltas += f.getCantidad();

                tCantidadFaltas.setText(Utilidades.obtenerStringXML(this, R.string.texto_cantidad_faltas) + " " + cantidadfaltas);
            }
            catch (ServidorPHPException e)
            {
                System.out.println("Error -> " + e.toString());
            }
        }
    }
}
