package asistenciaalumnos.asistenciaalumnos;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import controlador.ControladorProfesor;
import internet.AccesoInternet;
import internet.ServidorPHPException;
import utilidades.GeneradorToken;
import utilidades.Utilidades;

public class VistaAgregarProfesor extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener
{
    private Toolbar toolbar_agregarprofesor;
    private Spinner spCiclosAgProfesor, spCursoAgProf, spAsignaturaAgProf, spTipoAgProf;
    private String cicloelegido = "SMR", cursoelegido = "PRIMERO", asignaturaelegida = "", tipoprofesorelegido;
    private AccesoInternet accesointernet;
    private LinearLayout layoutErrorInternetAgProfesor, lDatosProfesor, lMatriculacionProfesor;
    private TextView tNombreProfesor, tEmailProfesor;
    private Button bAceptarAgProf, bLimpiarAgProf, bMatricularProfesor;
    private final String CONTRASENA_DEFECTO = "poniente123";
    private ControladorProfesor controladorp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_profesor);

        // ***** Obtengo los recursos de la actividad *****
        toolbar_agregarprofesor = (Toolbar) findViewById(R.id.toolbar_agregarprofesor);
        spCiclosAgProfesor = (Spinner) findViewById(R.id.spCiclosAgProfesor);
        spCursoAgProf = (Spinner) findViewById(R.id.spCursoAgProf);
        spAsignaturaAgProf = (Spinner) findViewById(R.id.spAsignaturaAgProf);
        spTipoAgProf = (Spinner) findViewById(R.id.spTipoAgProf);
        layoutErrorInternetAgProfesor = (LinearLayout) findViewById(R.id.layoutErrorInternetAgProfesor);
        lDatosProfesor = (LinearLayout) findViewById(R.id.lDatosProfesor);
        lMatriculacionProfesor = (LinearLayout) findViewById(R.id.lMatriculacionProfesor);
        tNombreProfesor = (TextView) findViewById(R.id.tNombreProfesor);
        tEmailProfesor = (TextView) findViewById(R.id.tEmailProfesor);
        bAceptarAgProf = (Button) findViewById(R.id.bAceptarAgProf);
        bLimpiarAgProf = (Button) findViewById(R.id.bLimpiarAgProf);
        bMatricularProfesor = (Button) findViewById(R.id.bMatricularProfesor);
        // ************************************************

        //setSupportActionBar(toolbar_agregarprofesor);
        toolbar_agregarprofesor.setTitle(Utilidades.obtenerStringXML(this, R.string.title_activity_agregar_profesor));
        toolbar_agregarprofesor.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar_agregarprofesor.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        accesointernet = new AccesoInternet(this);
        Utilidades.rellenarSpinner(spCiclosAgProfesor, R.array.ciclos, this);
        Utilidades.rellenarSpinner(spCursoAgProf, R.array.cursos, this);
        Utilidades.rellenarSpinner(spTipoAgProf, R.array.tipos_profesor, this);

        Utilidades.enableDisableView(lMatriculacionProfesor, Boolean.FALSE);
        Utilidades.enableDisableView(lDatosProfesor, Boolean.TRUE);

        if( accesointernet.verificarConexion().equals(AccesoInternet.TipoConexionInternet.NO_CONEXION) )
        {
            layoutErrorInternetAgProfesor.setVisibility(View.VISIBLE);
        }
        else
        {
            layoutErrorInternetAgProfesor.setVisibility(View.GONE);

            spAsignaturaAgProf.setOnItemSelectedListener(this);
            spCiclosAgProfesor.setOnItemSelectedListener(this);
            spCursoAgProf.setOnItemSelectedListener(this);
            spTipoAgProf.setOnItemSelectedListener(this);

            bAceptarAgProf.setOnClickListener(this);
            bLimpiarAgProf.setOnClickListener(this);
            bMatricularProfesor.setOnClickListener(this);

            controladorp = new ControladorProfesor(this);
        }
    }

    /**
     * Rellena las asignaturas del ciclo y curso elegido
     */
    private void rellenarAsignaturas()
    {
        if( cicloelegido.equals("SMR") && cursoelegido.equals("PRIMERO") )
        {
            Utilidades.rellenarSpinner(spAsignaturaAgProf, R.array.asignaturas_1SMR, this);
        }
        else
            if( cicloelegido.equals("SMR") && cursoelegido.equals("SEGUNDO") )
            {
                Utilidades.rellenarSpinner(spAsignaturaAgProf, R.array.asignaturas_2SMR, this);
            }
            else
                if( cicloelegido.equals("DAM") && cursoelegido.equals("PRIMERO") )
                {
                    Utilidades.rellenarSpinner(spAsignaturaAgProf, R.array.asignaturas_1DAM, this);
                }
                else
                    if( cicloelegido.equals("DAM") && cursoelegido.equals("SEGUNDO") )
                    {
                        Utilidades.rellenarSpinner(spAsignaturaAgProf, R.array.asignaturas_2DAM, this);
                    }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        switch(parent.getId())
        {
            case R.id.spCiclosAgProfesor:
                cicloelegido = spCiclosAgProfesor.getSelectedItem().toString();
                rellenarAsignaturas();
                break;
            case R.id.spCursoAgProf:
                cursoelegido = spCursoAgProf.getSelectedItem().toString();
                rellenarAsignaturas();
                break;
            case R.id.spAsignaturaAgProf:
                asignaturaelegida = spAsignaturaAgProf.getSelectedItem().toString();
                break;
            case R.id.spTipoAgProf:
                tipoprofesorelegido = spTipoAgProf.getSelectedItem().toString();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.bAceptarAgProf:

                try
                {
                    // Obtengo el token del profesor
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    String token = prefs.getString("token", "");

                    String nombre = tNombreProfesor.getText().toString();
                    String email = tEmailProfesor.getText().toString();

                    if( Utilidades.validarEmail(email) )
                    {
                        String tokengenerado = GeneradorToken.generarToken(email, CONTRASENA_DEFECTO);

                        //System.out.println("Voy a matricular al profesor " + nombre + " con email " + email + "con el tipo " + tipoprofesorelegido + " con la contraseña " + CONTRASENA_DEFECTO + ", con el token " + tokengenerado);

                        if( controladorp.insertarProfesor(token, nombre, email, CONTRASENA_DEFECTO, tipoprofesorelegido, tokengenerado) )
                        {
                            Utilidades.mostrarToastText(this, Utilidades.obtenerStringXML(this, R.string.texto_profesor_insertado_ok));
                        }
                        else
                        {
                            Utilidades.mostrarToastText(this, Utilidades.obtenerStringXML(this, R.string.texto_profesor_insertado_nook));
                        }
                        Utilidades.enableDisableView(lMatriculacionProfesor, Boolean.TRUE);
                        Utilidades.enableDisableView(lDatosProfesor, Boolean.FALSE);
                    }
                    else
                    {
                        Utilidades.mostrarToastText(this, Utilidades.obtenerStringXML(this, R.string.texto_email_no_valido));
                    }

                }
                catch (ServidorPHPException | UnsupportedEncodingException | NoSuchAlgorithmException e)
                {
                    Utilidades.mostrarToastText(this, Utilidades.obtenerStringXML(this, R.string.texto_profesor_insertado_nook));
                    System.out.println(e.toString());
                }
                break;
            case R.id.bLimpiarAgProf:
                tNombreProfesor.setText("");
                tEmailProfesor.setText("");
                break;
            case R.id.bMatricularProfesor:
                System.out.println(tEmailProfesor.getText().toString());
                System.out.println(cicloelegido);
                System.out.println(cursoelegido);
                System.out.println(asignaturaelegida);
                try
                {
                    // Obtengo el token del profesor
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    String token = prefs.getString("token", "");
                    if( controladorp.matricularProfesorAsignatura(token, tEmailProfesor.getText().toString(), cicloelegido, cursoelegido, asignaturaelegida) )
                    {
                        Utilidades.mostrarToastText(this, Utilidades.obtenerStringXML(this, R.string.texto_profesor_matriculado_ok));
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
}
