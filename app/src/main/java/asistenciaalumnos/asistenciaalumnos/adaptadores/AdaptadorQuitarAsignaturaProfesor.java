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

import asistenciaalumnos.asistenciaalumnos.VistaModificarDatosProfesor;
import asistenciaalumnos.asistenciaalumnos.R;
import controlador.ControladorProfesor;
import internet.ServidorPHPException;
import modelo.asignatura.Asignatura;
import utilidades.Utilidades;

/**
 * Created by francis on 4/01/18.
 */

public class AdaptadorQuitarAsignaturaProfesor extends RecyclerView.Adapter<AdaptadorQuitarAsignaturaProfesor.HolderQuitarAsignatura>
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
    private VistaModificarDatosProfesor actividad;
    private ControladorProfesor controladorp;
    private String emailprofesor;

    public AdaptadorQuitarAsignaturaProfesor(String emailprofesor, ArrayList<Asignatura> asignaturas, Context contexto, VistaModificarDatosProfesor actividad)
    {
        this.emailprofesor = emailprofesor;
        this.asignaturas = asignaturas;
        this.contexto = contexto;
        this.actividad = actividad;
        controladorp = new ControladorProfesor(contexto);
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

    public void clear()
    {
        int size = asignaturas.size();
        asignaturas.clear();
        notifyItemRangeRemoved(0, size);
    }

    @Override
    public void onBindViewHolder(HolderQuitarAsignatura profesordeturno, final int i)
    {
        profesordeturno.tCicloABorrar.setText(asignaturas.get(i).getCiclo());
        profesordeturno.tCursoABorrar.setText(asignaturas.get(i).getCurso());
        profesordeturno.tNombreABorrar.setText(asignaturas.get(i).getNombre());

        profesordeturno.ibQuitarAsignatura.setOnClickListener(new View.OnClickListener()
        {
            @Override
            /**
             *
             */
            public void onClick(View v)
            {
                try
                {
                    // Obtengo el token del profesor
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(contexto);
                    String token = prefs.getString("token", "");

                    if( controladorp.quitarAsignaturaProfesor(token, emailprofesor, asignaturas.get(i).getCiclo(), asignaturas.get(i).getCurso(), asignaturas.get(i).getNombre()) )
                    {
                        Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_asignatura_quitada_ok));
                        actividad.mostrarAsignaturasImpartidas();
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
