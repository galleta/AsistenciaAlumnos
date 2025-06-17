package asistenciaalumnos.asistenciaalumnos.adaptadores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import asistenciaalumnos.asistenciaalumnos.R;
import modelo.alumno.Alumno;
import modelo.alumno.AlumnoMatriculado;
import modelo.asistencia.Asistencia;
import modelo.asistencia.TipoAsistencia;

/**
 * Created by francis on 24/09/16.
 */

public class AdaptadorAnotarAsistenciaAlumnos extends RecyclerView.Adapter<AdaptadorAnotarAsistenciaAlumnos.ResumenAlumnoAsistencia>
{
    private ArrayList<Alumno> alumnos;
    private Context contexto;
    private ArrayList<Asistencia> asistencias;
    private Map<Integer,Integer> idsMarcadas; // Mapa para guardar las id marcadas de las asistencias

    /**
     * Clase interna equivalente al Holder de los elementos
     */
    public class ResumenAlumnoAsistencia extends RecyclerView.ViewHolder
    {
        final TextView tNombreAlumnoL2, tApellidosAlumnoL2;
        final RadioButton rbAsiste, rbFalta, rbFaltaJustificada, rbRetraso;
        final RadioGroup rgAsistencia;
        int posicion;

        public ResumenAlumnoAsistencia(View itemView)
        {
            super(itemView);
            tNombreAlumnoL2 = itemView.findViewById(R.id.tNombreAlumnoL2);
            tApellidosAlumnoL2 = itemView.findViewById(R.id.tApellidosAlumnoL2);
            rbAsiste = itemView.findViewById(R.id.rbAsiste);
            rbFalta = itemView.findViewById(R.id.rbFalta);
            rbFaltaJustificada = itemView.findViewById(R.id.rbFaltaJustificada);
            rbRetraso = itemView.findViewById(R.id.rbRetraso);
            rgAsistencia = itemView.findViewById(R.id.rgAsistencia);

            rgAsistencia.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
            {
                @SuppressLint("ResourceType")
                public void onCheckedChanged(RadioGroup group, int checkedId)
                {
                    if(checkedId > 0)
                    {
                        // Guardo en la posicion de cada alumno el id del radiobutton que se ha maracado de su asistencia
                        // al ser un mapa si se cambia el valor del radiobutton se actualiza
                        final RadioButton viewById = group.findViewById(checkedId);
                        if (viewById.isChecked())
                        {
                            idsMarcadas.put(posicion, checkedId);
                        }
                    }
                }
            });

        }

        public void setPosicion(int pos)
        {
            this.posicion = pos;
        }
    }

    public AdaptadorAnotarAsistenciaAlumnos(Context contexto, ArrayList<Alumno> alumnos)
    {
        this.contexto = contexto;
        this.alumnos = alumnos;
        asistencias = new ArrayList<>();
        idsMarcadas = new HashMap<>();
        for(int i = 0; i < alumnos.size(); i++)
        {
            idsMarcadas.put(i, 0);
        }
    }

    @Override
    public int getItemCount()
    {
        return alumnos.size();
    }

    /**
     * @param datos
     */
    public void add(ArrayList<AlumnoMatriculado> datos)
    {
        alumnos.clear();
        alumnos.addAll(datos);
    }

    /**
     *
     */
    public void refrescar()
    {
        notifyDataSetChanged();
    }

    public ArrayList<Asistencia> getAsistencias()
    {
        asistencias.clear();
        for (int i = 0; i < idsMarcadas.size(); i++)
        {
            int tipoasistenciamarcada = Integer.valueOf(idsMarcadas.get(i).toString());
            //System.out.println("Asistencia marcada: " + tipoasistenciamarcada);

            if( tipoasistenciamarcada == 0 )
                tipoasistenciamarcada = R.id.rbAsiste;

            Asistencia asis = new Asistencia(-1, alumnos.get(i), "", "", "", "", "");
            switch (tipoasistenciamarcada)
            {
                case R.id.rbAsiste:
                    asis.setTipoAsistencia(TipoAsistencia.ASISTE.toString());
                    break;
                case R.id.rbFalta:
                    asis.setTipoAsistencia(TipoAsistencia.FALTA.toString());
                    break;
                case R.id.rbFaltaJustificada:
                    asis.setTipoAsistencia(TipoAsistencia.FALTAJUSTIFICADA.toString());
                    break;
                case R.id.rbRetraso:
                    asis.setTipoAsistencia(TipoAsistencia.RETRASO.toString());
                    break;
            }
            asistencias.add(asis);

        }

        return asistencias;
    }

    @Override
    public AdaptadorAnotarAsistenciaAlumnos.ResumenAlumnoAsistencia onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.elemento_lista_anotar_asistencia_alumno, viewGroup, false);
        AdaptadorAnotarAsistenciaAlumnos.ResumenAlumnoAsistencia pvh = new AdaptadorAnotarAsistenciaAlumnos.ResumenAlumnoAsistencia(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final AdaptadorAnotarAsistenciaAlumnos.ResumenAlumnoAsistencia alumnodeturno, final int i)
    {
        alumnodeturno.tNombreAlumnoL2.setText(alumnos.get(i).getNombre());
        alumnodeturno.tApellidosAlumnoL2.setText(alumnos.get(i).getApellidos());
        alumnodeturno.setPosicion(i);
        if(idsMarcadas.get(i)!=0)
        {
            alumnodeturno.rgAsistencia.check(idsMarcadas.get(i));
        }
        /*else
        {
            alumnodeturno.rgAsistencia.clearCheck();
        }*/
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
