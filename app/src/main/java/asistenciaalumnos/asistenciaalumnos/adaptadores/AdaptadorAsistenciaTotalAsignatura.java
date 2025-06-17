package asistenciaalumnos.asistenciaalumnos.adaptadores;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import asistenciaalumnos.asistenciaalumnos.R;
import modelo.asistencia.AsistenciaTotalAsignatura;
import utilidades.Utilidades;

/**
 * Created by francis on 27/09/16.
 */

public class AdaptadorAsistenciaTotalAsignatura extends RecyclerView.Adapter<AdaptadorAsistenciaTotalAsignatura.ResumenAsistenciaTotal>
{
    private ArrayList<AsistenciaTotalAsignatura> asistenciastotales;
    private Context contexto;

    private static final double MINIMO_ASISTENCIA = 85;

    /**
     * Clase interna equivalente al Holder de los elementos
     */
    public class ResumenAsistenciaTotal extends RecyclerView.ViewHolder
    {
        final TextView tTotalHoras, tNombreAlumnoATotal, tAsiste, tRetraso, tFaltas, tFaltasJustificadas, tPorcentajeAsistido, tTotalNotaAsistencia;

        public ResumenAsistenciaTotal(View itemView)
        {
            super(itemView);
            tTotalHoras = itemView.findViewById(R.id.tTotalHoras);
            tNombreAlumnoATotal = itemView.findViewById(R.id.tNombreAlumnoATotal);
            tAsiste = itemView.findViewById(R.id.tAsiste);
            tRetraso = itemView.findViewById(R.id.tRetraso);
            tFaltas = itemView.findViewById(R.id.tFaltas);
            tFaltasJustificadas = itemView.findViewById(R.id.tFaltasJustificadas);
            tPorcentajeAsistido = itemView.findViewById(R.id.tPorcentajeAsistido);
            tTotalNotaAsistencia = itemView.findViewById(R.id.tTotalNotaAsistencia);
        }
    }

    public AdaptadorAsistenciaTotalAsignatura(Context contexto, ArrayList<AsistenciaTotalAsignatura> asistenciastotales)
    {
        this.contexto = contexto;
        this.asistenciastotales = asistenciastotales;
    }

    @Override
    public int getItemCount()
    {
        return asistenciastotales.size();
    }

    /**
     * @param datos
     */
    public void add(ArrayList<AsistenciaTotalAsignatura> datos)
    {
        asistenciastotales.clear();
        asistenciastotales.addAll(datos);
    }

    public void refrescar()
    {
        notifyDataSetChanged();
    }

    @Override
    public AdaptadorAsistenciaTotalAsignatura.ResumenAsistenciaTotal onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.elemento_lista_asistencia_total, viewGroup, false);
        AdaptadorAsistenciaTotalAsignatura.ResumenAsistenciaTotal pvh = new AdaptadorAsistenciaTotalAsignatura.ResumenAsistenciaTotal(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final AdaptadorAsistenciaTotalAsignatura.ResumenAsistenciaTotal asistenciadeturno, final int i)
    {
        asistenciadeturno.tNombreAlumnoATotal.setText(" " + asistenciastotales.get(i).getNombreAlumno() + " " + asistenciastotales.get(i).getApellidosAlumno());
        asistenciadeturno.tTotalHoras.setText(String.valueOf(asistenciastotales.get(i).getTotalHoras()));
        asistenciadeturno.tAsiste.setText(String.valueOf(asistenciastotales.get(i).getTotalAsiste()));
        asistenciadeturno.tRetraso.setText(String.valueOf(asistenciastotales.get(i).getTotalRetraso()));
        asistenciadeturno.tFaltas.setText(String.valueOf(asistenciastotales.get(i).getTotalFalta()));
        asistenciadeturno.tFaltasJustificadas.setText(String.valueOf(asistenciastotales.get(i).getTotalFaltaJustificada()));

        double porcentaje = asistenciastotales.get(i).getPorcentajeAsistencia();
        asistenciadeturno.tPorcentajeAsistido.setText(String.format("%.2f", porcentaje) + " %");

        if( porcentaje < MINIMO_ASISTENCIA )
        {
            asistenciadeturno.tPorcentajeAsistido.setTextAppearance(contexto, R.style.EstiloTexto2FAIL);
            asistenciadeturno.tTotalNotaAsistencia.setText("0");
            asistenciadeturno.tTotalNotaAsistencia.setTextAppearance(contexto, R.style.EstiloTexto2FAIL);
        }
        else
        {
            asistenciadeturno.tPorcentajeAsistido.setTextAppearance(contexto, R.style.EstiloTexto2OK);
            asistenciadeturno.tTotalNotaAsistencia.setText(String.format("%.2f", asistenciastotales.get(i).getNotaAsistencia()));
            asistenciadeturno.tTotalNotaAsistencia.setTextAppearance(contexto, R.style.EstiloTexto2OK);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
