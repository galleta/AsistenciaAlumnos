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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.util.ArrayList;

import asistenciaalumnos.asistenciaalumnos.adaptadores.AdaptadorQuitarAsignaturaAlumno;
import asistenciaalumnos.asistenciaalumnos.dialogos.DialogoConfirmacionDesmatricularAlumno;
import controlador.ControladorAlumno;
import controlador.ControladorAsignatura;
import modelo.alumno.Alumno;
import modelo.alumno.AlumnoMatriculado;
import internet.AccesoInternet;
import internet.ServidorPHPException;
import modelo.asignatura.Asignatura;
import utilidades.SpaceItemDecoration;
import utilidades.Utilidades;

public class VistaModificarDatosAlumno extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener
{
    private Toolbar toolbar_modificaralumno;
    private LinearLayout layoutErrorInternetMA, layoutPrincipalAgregarAlumno;
    private ControladorAlumno controladoralum;
    private ControladorAsignatura controladorasig;
    private AccesoInternet accesointernet;
    private EditText tNombreAlumnoMA, tApellidosAlumnoMA;
    private Button bModificarMA, bLimpiarMA, bAgregarAsigAlumMod, bDesmatricularAlumMod, bMatricularAlumMod;//, bModificarAlumCicloCurso;
    private AlumnoMatriculado alumno;
    private RadioButton rbSMRMod, rbDAMMod, rbPrimeroMod, rbSegundoMod;
    private RecyclerView lAsignaturasAlumMod;
    private Spinner spAsignaturaAlumMod;
    private String cicloelegido, cursoelegido;
    private LinearLayout lCicloCursoAlumMod;
    private AdaptadorQuitarAsignaturaAlumno adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_alumno);

        // ***** Obtengo los recursos de la aplicación *****
        toolbar_modificaralumno = (Toolbar) findViewById(R.id.toolbar_modificaralumno);
        layoutPrincipalAgregarAlumno = (LinearLayout) findViewById(R.id.layoutPrincipalAgregarAlumno);
        layoutErrorInternetMA = (LinearLayout) findViewById(R.id.layoutErrorInternetMA);
        tNombreAlumnoMA = (EditText) findViewById(R.id.tNombreAlumnoMA);
        tApellidosAlumnoMA = (EditText) findViewById(R.id.tApellidosAlumnoMA);
        bModificarMA = (Button) findViewById(R.id.bModificarMA);
        bLimpiarMA = (Button) findViewById(R.id.bLimpiarMA);
        rbSMRMod = findViewById(R.id.rbSMRMod);
        rbDAMMod = findViewById(R.id.rbDAMMod);
        rbPrimeroMod = findViewById(R.id.rbPrimeroMod);
        rbSegundoMod = findViewById(R.id.rbSegundoMod);
        bDesmatricularAlumMod = findViewById(R.id.bDesmatricularAlumMod);
        bMatricularAlumMod = findViewById(R.id.bMatricularAlumMod);
        spAsignaturaAlumMod = findViewById(R.id.spAsignaturaAlumMod);
        lCicloCursoAlumMod = findViewById(R.id.lCicloCursoAlumMod);
        lAsignaturasAlumMod = findViewById(R.id.lAsignaturasAlumMod);
        bAgregarAsigAlumMod = findViewById(R.id.bAgregarAsigAlumMod);
        //bModificarAlumCicloCurso = findViewById(R.id.bModificarAlumCicloCurso);
        // *************************************************

        //setSupportActionBar(toolbar_agregarhoras);
        toolbar_modificaralumno.setTitle(Utilidades.obtenerStringXML(this, R.string.title_activity_modificar_alumno));
        toolbar_modificaralumno.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar_modificaralumno.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        accesointernet = new AccesoInternet(this);

        if( accesointernet.verificarConexion().equals(AccesoInternet.TipoConexionInternet.NO_CONEXION) )
        {
            layoutErrorInternetMA.setVisibility(View.VISIBLE);
        }
        else
        {
            layoutErrorInternetMA.setVisibility(View.GONE);

            alumno = (AlumnoMatriculado)getIntent().getExtras().getSerializable("alumno");
            tNombreAlumnoMA.setText(alumno.getNombre());
            tApellidosAlumnoMA.setText(alumno.getApellidos());
            controladoralum = new ControladorAlumno(this);
            controladorasig = new ControladorAsignatura(this);
            accesointernet = new AccesoInternet(this);

            bLimpiarMA.setOnClickListener(this);
            bModificarMA.setOnClickListener(this);
            //bModificarAlumCicloCurso.setOnClickListener(this);
            bAgregarAsigAlumMod.setOnClickListener(this);

            bDesmatricularAlumMod.setOnClickListener(this);
            bMatricularAlumMod.setOnClickListener(this);

            // Pongo la información sobre el curso y el ciclo del alumno
            if( alumno.getCiclo().compareTo("SMR") == 0 )
            {
                rbSMRMod.setChecked(Boolean.TRUE);
                cicloelegido = "SMR";
            }
            if( alumno.getCiclo().compareTo("DAM") == 0 )
            {
                rbDAMMod.setChecked(Boolean.TRUE);
                cicloelegido = "DAM";
            }
            if( alumno.getCurso().compareTo("PRIMERO") == 0 )
            {
                rbPrimeroMod.setChecked(Boolean.TRUE);
                cursoelegido = "PRIMERO";
            }
            if( alumno.getCurso().compareTo("SEGUNDO") == 0 )
            {
                rbSegundoMod.setChecked(Boolean.TRUE);
                cursoelegido = "SEGUNDO";
            }

            lAsignaturasAlumMod.addItemDecoration(new SpaceItemDecoration(this, R.dimen.list_space, true, true));
            // Con esto el tamaño del recyclerwiew no cambiará
            lAsignaturasAlumMod.setHasFixedSize(true);
            // Creo un layoutmanager para el recyclerview
            LinearLayoutManager llm = new LinearLayoutManager(this);
            lAsignaturasAlumMod.setLayoutManager(llm);

            try
            {
                rellenarAsignaturasParaModificar();
            }
            catch (ServidorPHPException e)
            {
                System.out.println("Error -> " + e.toString());
            }
        }

    }

    public void rellenarAsignaturasParaModificar() throws ServidorPHPException
    {
        // Pongo las asignaturas según el ciclo y el curso
        String asigs [];
        ArrayList<String> asignaturasfinales = new ArrayList<>();

        if( rbSMRMod.isChecked() )
        {
            if( rbPrimeroMod.isChecked() )
                asigs = getResources().getStringArray(R.array.asignaturas_1SMR);
            else
                asigs = getResources().getStringArray(R.array.asignaturas_2SMR);
        }
        else
        {
            if( rbPrimeroMod.isChecked() )
                asigs = getResources().getStringArray(R.array.asignaturas_1DAM);
            else
                asigs = getResources().getStringArray(R.array.asignaturas_2DAM);
        }

        for (int i = 0; i < asigs.length; i++)
        {
            asignaturasfinales.add(asigs[i]);
        }

        // Elimino de las asignaturas para matricular las que ya tenga el alumno matriculadas
        // Obtengo el token del profesor
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String token = prefs.getString("token", "");
        ArrayList<Asignatura> asignaturasmatriculadas = controladorasig.obtenerTodasAsignaturasMatriculadasAlumno(token, alumno);

        for(Asignatura asig : asignaturasmatriculadas)
        {
            asignaturasfinales.remove(asig.getNombreAbreviado());
        }

        Utilidades.rellenarSpinner(spAsignaturaAlumMod, asignaturasfinales, this);

        if( asignaturasfinales.size() == 0 )
            bAgregarAsigAlumMod.setEnabled(Boolean.FALSE);
        else
            bAgregarAsigAlumMod.setEnabled(Boolean.TRUE);

        adaptador = new AdaptadorQuitarAsignaturaAlumno(asignaturasmatriculadas, this, alumno, this);
        lAsignaturasAlumMod.setAdapter(adaptador);
        adaptador.refrescar();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.bAgregarAsigAlumMod:
                // Matriculo a un alumno de una asignatura
                // Matriculo a un alumno de un curso de un ciclo
                String ciclo2, curso2;

                if( rbSMRMod.isChecked() )
                    ciclo2 = Utilidades.obtenerStringXML(this, R.string.smr);
                else
                    ciclo2 = Utilidades.obtenerStringXML(this, R.string.dam);

                if( rbPrimeroMod.isChecked() )
                    curso2 = Utilidades.obtenerStringXML(this, R.string.primero);
                else
                    curso2 = Utilidades.obtenerStringXML(this, R.string.segundo);

                String asig = spAsignaturaAlumMod.getSelectedItem().toString();

                try
                {
                    // Obtengo el token del profesor
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    String token = prefs.getString("token", "");

                    if( controladoralum.matricularAlumnoAsignatura(token, alumno, asig, ciclo2, curso2) )
                    {
                        Utilidades.mostrarSnackBar(layoutPrincipalAgregarAlumno, "Alumno matriculado correctamente.");
                        rellenarAsignaturasParaModificar();
                    }
                    else
                    {
                        Utilidades.mostrarSnackBar(layoutPrincipalAgregarAlumno, "Error matriculado al alumno.");
                    }
                }
                catch (ServidorPHPException e)
                {
                    System.out.println("Error -> " + e.toString());
                }
                break;
            case R.id.bDesmatricularAlumMod:
                try
                {
                    // Obtengo el token del profesor
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    String token = prefs.getString("token", "");

                    String nombrealu = tNombreAlumnoMA.getText().toString();
                    String apellidosalu = tApellidosAlumnoMA.getText().toString();

                    Alumno alumno = controladoralum.obtenerAlumnoNombreApellidos(token, nombrealu, apellidosalu);

                    String ciclo, curso;

                    if( rbSMRMod.isChecked() )
                        ciclo = Utilidades.obtenerStringXML(this, R.string.smr);
                    else
                        ciclo = Utilidades.obtenerStringXML(this, R.string.dam);

                    if( rbPrimeroMod.isChecked() )
                        curso = Utilidades.obtenerStringXML(this, R.string.primero);
                    else
                        curso = Utilidades.obtenerStringXML(this, R.string.segundo);

                    FragmentManager fragmentManager = getFragmentManager();
                    DialogoConfirmacionDesmatricularAlumno dialogodesmatricular = new DialogoConfirmacionDesmatricularAlumno(this, token, ciclo, curso, alumno.getId());
                    dialogodesmatricular.show(fragmentManager, "tagAlerta");

                    rellenarAsignaturasParaModificar();
                }
                catch (ServidorPHPException e)
                {
                    System.out.println("Error -> " + e.toString());
                }
                break;
            case R.id.bMatricularAlumMod:
                try
                {
                    // Obtengo el token del profesor
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    String token = prefs.getString("token", "");

                    String nombrealu = tNombreAlumnoMA.getText().toString();
                    String apellidosalu = tApellidosAlumnoMA.getText().toString();

                    Alumno alumno = controladoralum.obtenerAlumnoNombreApellidos(token, nombrealu, apellidosalu);

                    String ciclo, curso;

                    if( rbSMRMod.isChecked() )
                        ciclo = Utilidades.obtenerStringXML(this, R.string.smr);
                    else
                        ciclo = Utilidades.obtenerStringXML(this, R.string.dam);

                    if( rbPrimeroMod.isChecked() )
                        curso = Utilidades.obtenerStringXML(this, R.string.primero);
                    else
                        curso = Utilidades.obtenerStringXML(this, R.string.segundo);

                    if( controladoralum.matricularAlumnoEnCicloCurso(token, alumno, ciclo, curso) )
                    {
                        Utilidades.mostrarSnackBar(layoutPrincipalAgregarAlumno, Utilidades.obtenerStringXML(this, R.string.texto_alumno_matriculado_ok));
                    }
                    else
                    {
                        Utilidades.mostrarSnackBar(layoutPrincipalAgregarAlumno, Utilidades.obtenerStringXML(this, R.string.texto_alumno_matriculado_nook));
                    }

                    rellenarAsignaturasParaModificar();
                }
                catch (ServidorPHPException e)
                {
                    System.out.println("Error -> " + e.toString());
                }
                break;
            case R.id.bModificarMA:
                String nombrenuevo = tNombreAlumnoMA.getText().toString();
                String apellidosnuevos = tApellidosAlumnoMA.getText().toString();

                // Obtengo el token del profesor
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String token = prefs.getString("token", "");

                try
                {
                    if( controladoralum.modificarAlumno(token, alumno.getId(), nombrenuevo, apellidosnuevos) )
                    {
                        Utilidades.mostrarSnackBar(layoutPrincipalAgregarAlumno, "Alumno modificado correctamente.");
                    }
                    else
                    {
                        Utilidades.mostrarSnackBar(layoutPrincipalAgregarAlumno, "Error modificando el alumno.");
                    }
                }
                catch(ServidorPHPException e)
                {
                    Utilidades.mostrarSnackBar(layoutPrincipalAgregarAlumno, "Error modificando el alumno.");
                }

                break;
            case R.id.bLimpiarMA:

                tNombreAlumnoMA.setText("");
                tApellidosAlumnoMA.setText("");

                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
