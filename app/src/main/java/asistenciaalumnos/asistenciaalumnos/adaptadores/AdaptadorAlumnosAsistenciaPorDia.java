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
import modelo.asistencia.AsistenciaAsignaturaDia;

/**
 * Created by francis on 30/09/16.
 */

public class AdaptadorAlumnosAsistenciaPorDia extends RecyclerView.Adapter<AdaptadorAlumnosAsistenciaPorDia.ResumenAsistenciaDia>
{
    private ArrayList<AsistenciaAsignaturaDia> asistenciastotales;
    private Context contexto;
    private DecimalFormat df = new DecimalFormat("###.##");

    /**
     * Clase interna equivalente al Holder de los elementos
     */
    public class ResumenAsistenciaDia extends RecyclerView.ViewHolder
    {
        final TextView tNombreAlumnoATotalDia, tAsisteDia, tRetrasoDia, tFaltasDia, tFaltasJustificadasDia;

        public ResumenAsistenciaDia(View itemView)
        {
            super(itemView);
            tNombreAlumnoATotalDia = (TextView) itemView.findViewById(R.id.tNombreAlumnoATotalDia);
            tAsisteDia = (TextView) itemView.findViewById(R.id.tAsisteDia);
            tRetrasoDia = (TextView) itemView.findViewById(R.id.tRetrasoDia);
            tFaltasDia = (TextView) itemView.findViewById(R.id.tFaltasDia);
            tFaltasJustificadasDia = (TextView) itemView.findViewById(R.id.tFaltasJustificadasDia);
        }
    }

    public AdaptadorAlumnosAsistenciaPorDia(Context contexto, ArrayList<AsistenciaAsignaturaDia> asistenciastotales)
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
    public void add(ArrayList<AsistenciaAsignaturaDia> datos)
    {
        asistenciastotales.clear();
        asistenciastotales.addAll(datos);
    }

    public void refrescar()
    {
        notifyDataSetChanged();
    }

    @Override
    public AdaptadorAlumnosAsistenciaPorDia.ResumenAsistenciaDia onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.elemento_lista_consultar_asistencia_por_dia, viewGroup, false);
        AdaptadorAlumnosAsistenciaPorDia.ResumenAsistenciaDia pvh = new AdaptadorAlumnosAsistenciaPorDia.ResumenAsistenciaDia(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final AdaptadorAlumnosAsistenciaPorDia.ResumenAsistenciaDia asistenciadeturno, final int i)
    {
        asistenciadeturno.tNombreAlumnoATotalDia.setText(asistenciastotales.get(i).getNombrealumno() + " " + asistenciastotales.get(i).getApellidosalumno());
        asistenciadeturno.tAsisteDia.setText(String.valueOf(asistenciastotales.get(i).getTotalasiste()));
        asistenciadeturno.tRetrasoDia.setText(String.valueOf(asistenciastotales.get(i).getTotalretraso()));
        asistenciadeturno.tFaltasDia.setText(String.valueOf(asistenciastotales.get(i).getTotalfalta()));
        asistenciadeturno.tFaltasJustificadasDia.setText(String.valueOf(asistenciastotales.get(i).getTotalfaltajustificada()));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
