package asistenciaalumnos.asistenciaalumnos;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import controlador.ControladorAlumno;
import modelo.alumno.Alumno;
import internet.AccesoInternet;
import internet.ServidorPHPException;
import utilidades.Utilidades;

import static android.widget.RadioGroup.*;

public class VistaAgregarAlumno extends AppCompatActivity implements View.OnClickListener, OnCheckedChangeListener
{
    private Toolbar toolbar_agregaralumno;
    private EditText tNombreAlumno, tApellidosAlumno;
    private RadioButton rbSMR, rbDAM, rbPrimero, rbSegundo;
    private Button bAceptar, bLimpiar, bMatricular, bMatricularAsignatura;
    private ControladorAlumno controladoralum;
    private AccesoInternet accesointernet;
    private LinearLayout layoutPrincipalAgregarAlumno, layoutErrorInternet1, lDatosAlumno, lMatriculacion, lMatricularAsignaturas;
    private Alumno alum;
    private RadioGroup rgCiclo, rgCurso;
    private Spinner spAsignaturaMatricular;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_alumno);

        // ***** Obtengo los recursos de la aplicación *****
        toolbar_agregaralumno = (Toolbar) findViewById(R.id.toolbar_agregaralumno);
        tNombreAlumno = (EditText) findViewById(R.id.tNombreAlumno);
        tApellidosAlumno = (EditText) findViewById(R.id.tApellidosAlumno);
        rbSMR = (RadioButton) findViewById(R.id.rbSMR);
        rbDAM = (RadioButton) findViewById(R.id.rbDAM);
        rbPrimero = (RadioButton) findViewById(R.id.rbPrimero);
        rbSegundo = (RadioButton) findViewById(R.id.rbSegundo);
        bAceptar = (Button) findViewById(R.id.bAceptar);
        bLimpiar = (Button) findViewById(R.id.bLimpiar);
        bMatricular = (Button) findViewById(R.id.bMatricular);
        bMatricularAsignatura = (Button) findViewById(R.id.bMatricularAsignatura);
        layoutErrorInternet1 = (LinearLayout) findViewById(R.id.layoutErrorInternet1);
        lMatriculacion = (LinearLayout) findViewById(R.id.lMatriculacion);
        lDatosAlumno = (LinearLayout) findViewById(R.id.lDatosAlumno);
        layoutPrincipalAgregarAlumno = (LinearLayout) findViewById(R.id.layoutPrincipalAgregarAlumno);
        lMatricularAsignaturas = (LinearLayout) findViewById(R.id.lMatricularAsignaturas);
        rgCiclo = (RadioGroup) findViewById(R.id.rgCiclo);
        rgCurso = (RadioGroup) findViewById(R.id.rgCurso);
        spAsignaturaMatricular = (Spinner) findViewById(R.id.spAsignaturaMatricular);
        // *************************************************

        //setSupportActionBar(toolbar_agregarhoras);
        toolbar_agregaralumno.setTitle(Utilidades.obtenerStringXML(this, R.string.title_activity_agregar_alumno));
        toolbar_agregaralumno.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar_agregaralumno.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        bAceptar.setOnClickListener(this);
        bLimpiar.setOnClickListener(this);
        bMatricular.setOnClickListener(this);
        bMatricularAsignatura.setOnClickListener(this);

        rgCiclo.setOnCheckedChangeListener(this);
        rgCurso.setOnCheckedChangeListener(this);

        controladoralum = new ControladorAlumno(this);
        accesointernet = new AccesoInternet(this);

        Utilidades.rellenarSpinner(spAsignaturaMatricular, R.array.asignaturas_1SMR, this);

        Utilidades.enableDisableView(lMatriculacion, Boolean.FALSE);
        Utilidades.enableDisableView(lMatricularAsignaturas, Boolean.FALSE);
        Utilidades.enableDisableView(lDatosAlumno, Boolean.TRUE);
        Utilidades.enableDisableView(lMatricularAsignaturas, Boolean.FALSE);

        if( accesointernet.verificarConexion().equals(AccesoInternet.TipoConexionInternet.NO_CONEXION) )
        {
            layoutErrorInternet1.setVisibility(View.VISIBLE);
        }
        else
        {
            layoutErrorInternet1.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.bAceptar:
                if( accesointernet.verificarConexion().equals(AccesoInternet.TipoConexionInternet.NO_CONEXION) )
                {
                    Utilidades.mostrarToastText(this, Utilidades.obtenerStringXML(this, R.string.error_internet));
                    Utilidades.enableDisableView(lMatriculacion, Boolean.FALSE);
                    Utilidades.enableDisableView(lDatosAlumno, Boolean.FALSE);
                }
                else
                {
                    alum = new Alumno(1, tNombreAlumno.getText().toString(), tApellidosAlumno.getText().toString());

                    try
                    {
                        // Obtengo el token del profesor
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                        String token = prefs.getString("token", "");
                        if( controladoralum.insertarAlumno(token, alum) )
                        {
                            Utilidades.mostrarSnackBar(layoutPrincipalAgregarAlumno, Utilidades.obtenerStringXML(this, R.string.texto_alumno_insertado_ok));
                            // Obtengo el alumno con su id actualizado del servidor
                            alum = controladoralum.obtenerAlumnoNombreApellidos(token, tNombreAlumno.getText().toString(), tApellidosAlumno.getText().toString());
                            Utilidades.enableDisableView(lMatriculacion, Boolean.TRUE);
                            Utilidades.enableDisableView(lMatricularAsignaturas, Boolean.FALSE);
                            Utilidades.enableDisableView(lDatosAlumno, Boolean.FALSE);

                            // Rellenar asignaturas
                        }
                        else
                        {
                            Utilidades.mostrarSnackBar(layoutPrincipalAgregarAlumno, Utilidades.obtenerStringXML(this, R.string.texto_alumno_insertado_nook));
                            Utilidades.enableDisableView(lMatriculacion, Boolean.FALSE);
                            Utilidades.enableDisableView(lDatosAlumno, Boolean.TRUE);
                        }
                    }
                    catch (ServidorPHPException e)
                    {
                        Utilidades.mostrarSnackBar(layoutPrincipalAgregarAlumno, Utilidades.obtenerStringXML(this, R.string.texto_alumno_insertado_nook));
                        Utilidades.enableDisableView(lMatriculacion, Boolean.FALSE);
                        Utilidades.enableDisableView(lDatosAlumno, Boolean.TRUE);
                    }
                }

                break;
            case R.id.bLimpiar:
                tNombreAlumno.setText("");
                tApellidosAlumno.setText("");
                rbSMR.setChecked(Boolean.TRUE);
                rbPrimero.setChecked(Boolean.TRUE);

                break;
            case R.id.bMatricular:
                // Matriculo a un alumno de un curso de un ciclo
                String ciclo, curso;

                if( rbSMR.isChecked() )
                    ciclo = Utilidades.obtenerStringXML(this, R.string.smr);
                else
                    ciclo = Utilidades.obtenerStringXML(this, R.string.dam);

                if( rbPrimero.isChecked() )
                    curso = Utilidades.obtenerStringXML(this, R.string.primero);
                else
                    curso = Utilidades.obtenerStringXML(this, R.string.segundo);

                try
                {
                    // Obtengo el token del profesor
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    String token = prefs.getString("token", "");
                    if( controladoralum.matricularAlumnoEnCicloCurso(token, alum, ciclo, curso) )
                    {
                        Utilidades.mostrarSnackBar(layoutPrincipalAgregarAlumno, Utilidades.obtenerStringXML(this, R.string.texto_alumno_matriculado_ok));
                        Utilidades.enableDisableView(lMatricularAsignaturas, Boolean.TRUE);
                    }
                    else
                    {
                        Utilidades.mostrarSnackBar(layoutPrincipalAgregarAlumno, Utilidades.obtenerStringXML(this, R.string.texto_alumno_matriculado_nook));
                        Utilidades.enableDisableView(lMatricularAsignaturas, Boolean.FALSE);
                    }
                }
                catch (ServidorPHPException e)
                {
                    Utilidades.mostrarSnackBar(layoutPrincipalAgregarAlumno, Utilidades.obtenerStringXML(this, R.string.texto_alumno_matriculado_nook));
                    Utilidades.enableDisableView(lMatricularAsignaturas, Boolean.FALSE);
                }
                break;
            case R.id.bMatricularAsignatura:
                // Matriculo a un alumno de una asignatura
                // Matriculo a un alumno de un curso de un ciclo
                String ciclo2, curso2;

                if( rbSMR.isChecked() )
                    ciclo2 = Utilidades.obtenerStringXML(this, R.string.smr);
                else
                    ciclo2 = Utilidades.obtenerStringXML(this, R.string.dam);

                if( rbPrimero.isChecked() )
                    curso2 = Utilidades.obtenerStringXML(this, R.string.primero);
                else
                    curso2 = Utilidades.obtenerStringXML(this, R.string.segundo);

                String asig = spAsignaturaMatricular.getSelectedItem().toString();

                try
                {
                    // Obtengo el token del profesor
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    String token = prefs.getString("token", "");
                    if( controladoralum.matricularAlumnoAsignatura(token, alum, asig, ciclo2, curso2) )
                    {
                        Utilidades.mostrarSnackBar(layoutPrincipalAgregarAlumno, "Alumno matriculado correctamente.");
                    }
                    else
                    {
                        Utilidades.mostrarSnackBar(layoutPrincipalAgregarAlumno, "Error matriculando al alumno.");
                    }
                }
                catch (ServidorPHPException e)
                {
                    System.out.println("Error -> " + e.toString());
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId)
    {
        switch(group.getId())
        {
            case R.id.rgCiclo:
                switch( checkedId )
                {
                    case R.id.rbSMR:
                        if( rbPrimero.isChecked() )
                        {
                            Utilidades.rellenarSpinner(spAsignaturaMatricular, R.array.asignaturas_1SMR, this);
                        }
                        if( rbSegundo.isChecked() )
                        {
                            Utilidades.rellenarSpinner(spAsignaturaMatricular, R.array.asignaturas_2SMR, this);
                        }
                        break;
                    case R.id.rbDAM:
                        if( rbPrimero.isChecked() )
                        {
                            Utilidades.rellenarSpinner(spAsignaturaMatricular, R.array.asignaturas_1DAM, this);
                        }
                        if( rbSegundo.isChecked() )
                        {
                            Utilidades.rellenarSpinner(spAsignaturaMatricular, R.array.asignaturas_2DAM, this);
                        }
                        break;
                }
                break;
            case R.id.rgCurso:
                switch(checkedId)
                {
                    case R.id.rbPrimero:
                        if( rbSMR.isChecked() )
                        {
                            Utilidades.rellenarSpinner(spAsignaturaMatricular, R.array.asignaturas_1SMR, this);
                        }
                        if( rbDAM.isChecked() )
                        {
                            Utilidades.rellenarSpinner(spAsignaturaMatricular, R.array.asignaturas_1SMR, this);
                        }
                        break;
                    case R.id.rbSegundo:
                        if( rbSMR.isChecked() )
                        {
                            Utilidades.rellenarSpinner(spAsignaturaMatricular, R.array.asignaturas_2SMR, this);
                        }
                        if( rbDAM.isChecked() )
                        {
                            Utilidades.rellenarSpinner(spAsignaturaMatricular, R.array.asignaturas_2DAM, this);
                        }
                        break;
                }
                break;
        }
    }
}
