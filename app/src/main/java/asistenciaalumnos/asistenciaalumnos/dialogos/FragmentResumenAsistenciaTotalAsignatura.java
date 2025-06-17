package asistenciaalumnos.asistenciaalumnos.dialogos;

import android.annotation.TargetApi;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import asistenciaalumnos.asistenciaalumnos.R;
import controlador.ControladorAsignatura;
import controlador.ControladorAsistencia;
import controlador.ControladorProfesor;
import modelo.asignatura.Asignatura;
import modelo.profesor.Profesor;
import utilidades.SpaceItemDecoration;
import asistenciaalumnos.asistenciaalumnos.adaptadores.AdaptadorAsistenciaTotalAsignatura;
import modelo.asistencia.AsistenciaTotalAsignatura;
import internet.AccesoInternet;
import internet.ServidorPHPException;
import utilidades.Utilidades;

/**
 * Created by francis on 25/09/16.
 */

public class FragmentResumenAsistenciaTotalAsignatura extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener
{
    private String TAG = "FragmentResumenAsistenciaTotalAsignatura";
    private AccesoInternet accesointernet;
    private LinearLayout layoutErrorInternet4;
    private Spinner spCiclos3, spCurso3, spAsignatura3;
    private String cicloelegido = "SMR", cursoelegido = "PRIMERO";
    private Context contexto;
    private RecyclerView listaalumnos3;
    private ControladorProfesor controladorp;
    private ControladorAsignatura controladorasig;
    private ControladorAsistencia controladorasis;
    private AdaptadorAsistenciaTotalAsignatura adaptador;
    private Button bConsultarAsistenciaTotal;
    private TextView tFormulaAsistencia;
    private String email;
    private ArrayList<Asignatura> asignaturas;
    private ArrayList<String> asignaturasnombres;
    private int NOTAASISTENCIA;
    private double PESOASISTIDO, PESOFALTA, PESOFALTAJUSTIFICADA, PESORETRASO;
    private Boolean mostrado;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflo el layout de la pestaña
        View v = inflater.inflate(R.layout.contenido_resumen_asistencia_total, container, false);

        // ***** Obtengo los recursos de la aplicación *****
        layoutErrorInternet4 = v.findViewById(R.id.layoutErrorInternet4);
        spCiclos3 = v.findViewById(R.id.spCiclos3);
        spCurso3 = v.findViewById(R.id.spCurso3);
        spAsignatura3 = v.findViewById(R.id.spAsignatura3);
        listaalumnos3 = v.findViewById(R.id.listaalumnos3);
        bConsultarAsistenciaTotal = v.findViewById(R.id.bConsultarAsistenciaTotal);
        tFormulaAsistencia = v.findViewById(R.id.tFormulaAsistencia);
        // *************************************************

        accesointernet = new AccesoInternet(getContext());
        contexto = getContext();
        controladorp = new ControladorProfesor(contexto);
        controladorasig = new ControladorAsignatura(contexto);
        controladorasis = new ControladorAsistencia(contexto);
        asignaturas = new ArrayList<>();
        asignaturasnombres = new ArrayList<>();
        mostrado = Boolean.FALSE;

        Utilidades.rellenarSpinner(spCiclos3, R.array.ciclos, contexto);
        Utilidades.rellenarSpinner(spCurso3, R.array.cursos, contexto);
        Utilidades.rellenarSpinner(spAsignatura3, R.array.asignaturas_1SMR, contexto);

        if( accesointernet.verificarConexion().equals(AccesoInternet.TipoConexionInternet.NO_CONEXION) )
        {
            layoutErrorInternet4.setVisibility(View.VISIBLE);
        }
        else
        {
            layoutErrorInternet4.setVisibility(View.GONE);

            /*spCiclos3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    cicloelegido = spCiclos3.getSelectedItem().toString();

                    obtenerAsignaturasImpartidas();
                    Utilidades.rellenarSpinner(spAsignatura3, asignaturasnombres, contexto);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });

            spCurso3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    cursoelegido = spCurso3.getSelectedItem().toString();

                    obtenerAsignaturasImpartidas();
                    Utilidades.rellenarSpinner(spAsignatura3, asignaturasnombres, contexto);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });

            spAsignatura3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    mostrarAsistenciasAlumnos();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });*/

            spAsignatura3.setOnItemSelectedListener(this);
            spCiclos3.setOnItemSelectedListener(this);
            spCurso3.setOnItemSelectedListener(this);

            listaalumnos3.addItemDecoration(new SpaceItemDecoration(contexto, R.dimen.list_space, true, true));
            // Con esto el tamaño del recyclerwiew no cambiará
            listaalumnos3.setHasFixedSize(true);
            // Creo un layoutmanager para el recyclerview
            LinearLayoutManager llm = new LinearLayoutManager(contexto);
            listaalumnos3.setLayoutManager(llm);

            bConsultarAsistenciaTotal.setOnClickListener(this);

            mostrarAsistenciasAlumnos();

            // Muestro la fórmula para calcular la asistencia
            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(
                            contexto);

            email = prefs.getString("usuario", "");
            NOTAASISTENCIA = prefs.getInt("nota_asistencia", 10);
            PESOASISTIDO = (prefs.getInt("peso_asistencia", 1)/4f);
            PESOFALTA = (prefs.getInt("peso_falta", 1)/4f);
            PESOFALTAJUSTIFICADA = (prefs.getInt("peso_falta_justificada", 1)/4f);
            PESORETRASO = (prefs.getInt("peso_retraso", 1)/4f);

            String textoformula = Utilidades.obtenerStringXML(contexto, R.string.texto_formula);
            textoformula += "(" + Utilidades.obtenerStringXML(contexto, R.string.texto_ASISTENCIA) + " * " + PESOASISTIDO + ") + ";
            textoformula += "(" + Utilidades.obtenerStringXML(contexto, R.string.texto_FALTA) + " * " + PESOFALTA + ") + ";
            textoformula += "(" + Utilidades.obtenerStringXML(contexto, R.string.texto_FALTA_JUSTIFICADA) + " * " + PESOFALTAJUSTIFICADA + ") + ";
            textoformula += "(" + Utilidades.obtenerStringXML(contexto, R.string.texto_RETRASO) + " * " + PESORETRASO + ")\n";
            textoformula += Utilidades.obtenerStringXML(contexto, R.string.texto_maximo) + " " + NOTAASISTENCIA + " ";

            if( NOTAASISTENCIA == 1.0 )
                textoformula += Utilidades.obtenerStringXML(contexto, R.string.texto_punto);
            else
                textoformula += Utilidades.obtenerStringXML(contexto, R.string.texto_puntos);

            textoformula += "\n" + Utilidades.obtenerStringXML(contexto, R.string.texto_no_asistencia_minima);

            tFormulaAsistencia.setText(textoformula);
        }

        return v;
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
                    PreferenceManager.getDefaultSharedPreferences(getContext());
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
                    break;
            }
        }
        catch (ServidorPHPException e)
        {
            System.out.println("Error obteniendo las asignaturas impartidas: " + e.toString());
        }
    }

    /**
     * Muestra el total hasta el momento de las asistencias, faltas, faltas justificadas y retrasos, junto a su total y la nota correspondiente de los alumnos matriculados en la asignatura elegida
     */
    private void mostrarAsistenciasAlumnos()
    {
        if( accesointernet.verificarConexion().equals(AccesoInternet.TipoConexionInternet.NO_CONEXION) )
        {
            layoutErrorInternet4.setVisibility(View.VISIBLE);
        }
        else
        {
            layoutErrorInternet4.setVisibility(View.GONE);
            try
            {
                if( !mostrado )
                {
                    mostrado = Boolean.TRUE;
                    // Obtengo el token del profesor
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(contexto);
                    String token = prefs.getString("token", "");

                    String asignaturaelegida = spAsignatura3.getSelectedItem().toString();

                    if( !asignaturaelegida.equals("-") )
                    {
                        listaalumnos3.setVisibility(View.VISIBLE);
                        // Muestro la fórmula para calcular la asistencia
                        SharedPreferences prefs2 =
                                PreferenceManager.getDefaultSharedPreferences(
                                        contexto);

                        email = prefs2.getString("usuario", "");
                        NOTAASISTENCIA = prefs2.getInt("nota_asistencia", 10);
                        PESOASISTIDO = (prefs2.getInt("peso_asistencia", 1)/4f);
                        PESOFALTA = (prefs2.getInt("peso_falta", 1)/4f);
                        PESOFALTAJUSTIFICADA = (prefs2.getInt("peso_falta_justificada", 1)/4f);
                        PESORETRASO = (prefs2.getInt("peso_retraso", 1)/4f);

                        Asignatura asignatura = controladorasig.obtenerAsignatura(token, asignaturaelegida, cicloelegido, cursoelegido);
                        ArrayList<AsistenciaTotalAsignatura> asistenciastotales = controladorasis.obtenerTotalAsistenciaAlumnoAsignatura(token, asignatura.getId(), NOTAASISTENCIA, PESOASISTIDO, PESOFALTA, PESOFALTAJUSTIFICADA, PESORETRASO);
                        if( asistenciastotales.size() == 0 )
                        {
                            Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_no_asistencias));
                        }
                        adaptador = new AdaptadorAsistenciaTotalAsignatura(contexto, asistenciastotales);
                        listaalumnos3.setAdapter(adaptador);
                        adaptador.refrescar();
                    }
                    else
                    {
                        listaalumnos3.setVisibility(View.GONE);
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
    public void onClick(View v)
    {
        switch( v.getId() )
        {
            case R.id.bConsultarAsistenciaTotal:
                mostrado = Boolean.FALSE;
                mostrarAsistenciasAlumnos();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        switch(parent.getId())
        {
            case R.id.spCiclos3:
                cicloelegido = spCiclos3.getSelectedItem().toString();

                obtenerAsignaturasImpartidas();
                Utilidades.rellenarSpinner(spAsignatura3, asignaturasnombres, contexto);
                break;
            case R.id.spCurso3:
                cursoelegido = spCurso3.getSelectedItem().toString();

                obtenerAsignaturasImpartidas();
                Utilidades.rellenarSpinner(spAsignatura3, asignaturasnombres, contexto);
                break;
            case R.id.spAsignatura3:
                mostrarAsistenciasAlumnos();
                break;
        }
        bConsultarAsistenciaTotal.performClick();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
