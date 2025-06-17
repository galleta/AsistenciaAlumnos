package asistenciaalumnos.asistenciaalumnos.adaptadores;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import asistenciaalumnos.asistenciaalumnos.VistaModificarDatosAlumno;
import asistenciaalumnos.asistenciaalumnos.R;
import controlador.ControladorAsignatura;
import internet.ServidorPHPException;
import modelo.alumno.AlumnoMatriculado;
import modelo.asignatura.Asignatura;
import utilidades.Utilidades;

public class AdaptadorQuitarAsignaturaAlumno extends RecyclerView.Adapter<AdaptadorQuitarAsignaturaAlumno.HolderQuitarAsignatura>
{
    /**
     * Clase interna equivalente al Holder de los elementos
     */
    public static class HolderQuitarAsignatura extends RecyclerView.ViewHolder
    {
        TextView tCicloABorrar, tCursoABorrar, tNombreABorrar;
        ImageButton ibQuitarAsignatura;

        HolderQuitarAsignatura(View itemView)
        {
            super(itemView);
            tCicloABorrar = itemView.findViewById(R.id.tCicloABorrar);
            tCursoABorrar = itemView.findViewById(R.id.tCursoABorrar);
            tNombreABorrar = itemView.findViewById(R.id.tNombreABorrar);
            ibQuitarAsignatura = itemView.findViewById(R.id.ibQuitarAsignatura);
        }
    };

    private ArrayList<Asignatura> asignaturas;
    private Context contexto;
    private ControladorAsignatura controladorasig;
    private AlumnoMatriculado alumno;
    private VistaModificarDatosAlumno actividad;

    public AdaptadorQuitarAsignaturaAlumno(ArrayList<Asignatura> asignaturas, Context contexto, AlumnoMatriculado alumno, VistaModificarDatosAlumno actividad)
    {
        this.asignaturas = asignaturas;
        this.contexto = contexto;
        controladorasig = new ControladorAsignatura(contexto);
        this.alumno = alumno;
        this.actividad = actividad;
    }

    /**
     *
     * @param datos
     */
    public void add(ArrayList<Asignatura> datos)
    {
        asignaturas.clear();
        asignaturas.addAll(datos);
    }

    /**
     *
     */
    public void refrescar()
    {
        notifyDataSetChanged();
    }

    @Override
    public HolderQuitarAsignatura onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.elemento_lista_eliminar_asignatura_profesor, viewGroup, false);
        HolderQuitarAsignatura pvh = new HolderQuitarAsignatura(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final HolderQuitarAsignatura asignaturadeturno, final int i)
    {
        asignaturadeturno.tCicloABorrar.setText(asignaturas.get(i).getCiclo());
        asignaturadeturno.tCursoABorrar.setText(asignaturas.get(i).getCurso());
        asignaturadeturno.tNombreABorrar.setText(asignaturas.get(i).getNombreAbreviado());

        asignaturadeturno.ibQuitarAsignatura.setOnClickListener(new View.OnClickListener()
        {
            @Override
            /**
             *
             */
            public void onClick(View v)
            {
                int idasignatura = asignaturas.get(i).getId();
                int idalumno = alumno.getId();

                // Obtengo el token del profesor
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(contexto);
                String token = prefs.getString("token", "");

                try
                {
                    if( controladorasig.quitarAsignaturaMatriculadaAlumno(token, idalumno, idasignatura) )
                    {
                        Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_asignatura_quitada_ok));
                        actividad.rellenarAsignaturasParaModificar();
                    }
                    else
                    {
                        Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_asignatura_quitada_nook));
                    }
                }
                catch (ServidorPHPException e)
                {
                    Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_asignatura_quitada_nook));
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return asignaturas.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
