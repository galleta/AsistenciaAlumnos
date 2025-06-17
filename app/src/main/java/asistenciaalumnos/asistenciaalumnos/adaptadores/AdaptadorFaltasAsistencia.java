package asistenciaalumnos.asistenciaalumnos.adaptadores;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import asistenciaalumnos.asistenciaalumnos.R;
import asistenciaalumnos.asistenciaalumnos.VistaConsultarFechaFaltas;
import controlador.ControladorAsistencia;
import internet.ServidorPHPException;
import modelo.asistencia.TipoAsistencia;
import modelo.faltaasistencia.FaltaAsistencia;
import utilidades.Utilidades;

/**
 * Created by francis on 27/12/17.
 */

public class AdaptadorFaltasAsistencia extends RecyclerView.Adapter<AdaptadorFaltasAsistencia.HolderFaltaAsistencia>
{
    /**
     * Clase interna equivalente al Holder de los elementos
     */
    public static class HolderFaltaAsistencia extends RecyclerView.ViewHolder
    {
        TextView tTipoFaltaAsistencia, tFechaFaltaAsistencia, tCantidadFaltaAsistencia;
        ImageButton bJustificar;
        LinearLayout layoutJustificar;

        HolderFaltaAsistencia(View itemView)
        {
            super(itemView);
            tTipoFaltaAsistencia = itemView.findViewById(R.id.tTipoFaltaAsistencia);
            tFechaFaltaAsistencia = itemView.findViewById(R.id.tFechaFaltaAsistencia);
            tCantidadFaltaAsistencia = itemView.findViewById(R.id.tCantidadFaltaAsistencia);
            bJustificar = itemView.findViewById(R.id.bJustificar);
            layoutJustificar = itemView.findViewById(R.id.layoutJustificar);
        }
    };

    private ArrayList<FaltaAsistencia> faltas;
    private Context contexto;
    private VistaConsultarFechaFaltas ventanallamadora;

    public AdaptadorFaltasAsistencia(Context contexto, ArrayList<FaltaAsistencia> faltas, VistaConsultarFechaFaltas ventanallamadora)
    {
        this.faltas = faltas;
        this.contexto = contexto;
        this.ventanallamadora = ventanallamadora;
    }

    /**
     *
     * @param datos
     */
    public void add(ArrayList<FaltaAsistencia> datos)
    {
        faltas.clear();
        faltas.addAll(datos);
    }

    /**
     *
     */
    public void refrescar()
    {
        notifyDataSetChanged();
    }

    @Override
    public HolderFaltaAsistencia onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.elemento_lista_consultar_falta_asistencia, parent, false);
        HolderFaltaAsistencia pvh = new HolderFaltaAsistencia(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final HolderFaltaAsistencia faltadeturno, final int position)
    {
        faltadeturno.tCantidadFaltaAsistencia.setText(String.valueOf(faltas.get(position).getCantidad()));
        faltadeturno.tFechaFaltaAsistencia.setText(faltas.get(position).getFecha());
        faltadeturno.layoutJustificar.setVisibility(View.GONE);

        if( faltas.get(position).getTipoAsistencia().equals(TipoAsistencia.FALTA.toString()) )
        {
            faltadeturno.tTipoFaltaAsistencia.setText(Utilidades.obtenerStringXML(contexto, R.string.texto_FALTA));
            faltadeturno.layoutJustificar.setVisibility(View.VISIBLE);
        }
        else
        {
            if( faltas.get(position).getTipoAsistencia().equals(TipoAsistencia.FALTAJUSTIFICADA.toString()) )
            {
                faltadeturno.tTipoFaltaAsistencia.setText(Utilidades.obtenerStringXML(contexto, R.string.texto_FALTA_JUSTIFICADA));
            }
            else
            {
                if( faltas.get(position).getTipoAsistencia().equals(TipoAsistencia.RETRASO.toString()) )
                {
                    faltadeturno.tTipoFaltaAsistencia.setText(Utilidades.obtenerStringXML(contexto, R.string.texto_RETRASO));
                }
            }
        }


        faltadeturno.bJustificar.setOnClickListener(new View.OnClickListener() {
            @Override
            /**
             *
             */
            public void onClick(View v)
            {
                try
                {
                    ControladorAsistencia controladorfaltas = new ControladorAsistencia(contexto);
                    //System.out.println(faltas.get(position).toString());

                    // Obtengo el token del profesor
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(contexto);
                    String token = prefs.getString("token", "");
                    int idalumno = faltas.get(position).getAlumno().getId();
                    int idasignatura = faltas.get(position).getAsignatura().getId();
                    String fechafalta = faltas.get(position).getFecha();

                    if( controladorfaltas.justificarAsistencia(token, idalumno, idasignatura, fechafalta) )
                    {
                        Utilidades.mostrarToastText(contexto, "Faltas de asistencia justificadas correctamente");
                        ventanallamadora.consultarFechaFaltas();
                    }
                    else
                    {
                        Utilidades.mostrarToastText(contexto, "Error justificando las faltas de asistencia");
                    }
                }
                catch (ServidorPHPException | ClassCastException e)
                {
                    Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_error_justificar_faltas_asistencia));
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return faltas.size();
    }
}
