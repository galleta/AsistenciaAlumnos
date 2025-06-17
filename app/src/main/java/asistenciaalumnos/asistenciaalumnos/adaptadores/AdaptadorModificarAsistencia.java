package asistenciaalumnos.asistenciaalumnos.adaptadores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import asistenciaalumnos.asistenciaalumnos.R;
import asistenciaalumnos.asistenciaalumnos.VistaModificarDatosAsistencia;
import controlador.ControladorAsistencia;
import internet.ServidorPHPException;
import modelo.asistencia.Asistencia;
import modelo.asistencia.TipoAsistencia;
import utilidades.Utilidades;

/**
 * Created by francis on 16/11/16.
 */

public class AdaptadorModificarAsistencia extends RecyclerView.Adapter<AdaptadorModificarAsistencia.ResumenTodaAsistenciaDia>
{
    private ArrayList<Asistencia> asistencias;
    private Context contexto;
    private Map<Integer,Integer> idsMarcadas;

    /**
     * Clase interna equivalente al Holder de los elementos
     */
    public class ResumenTodaAsistenciaDia extends RecyclerView.ViewHolder
    {
        final TextView tAsignaturaAsistencia;
        final RadioButton rbAsisteModif, rbFaltaModif, rbFaltaJustificadaModif, rbRetrasoModif;
        final RadioGroup rgAsistenciaModif;
        final ImageButton ibModificarAsis, ibEliminarAsis;
        int posicion;

        public ResumenTodaAsistenciaDia(View itemView)
        {
            super(itemView);
            tAsignaturaAsistencia = itemView.findViewById(R.id.tAsignaturaAsistencia);
            rbAsisteModif = itemView.findViewById(R.id.rbAsisteModif);
            rbFaltaModif = itemView.findViewById(R.id.rbFaltaModif);
            rbFaltaJustificadaModif = itemView.findViewById(R.id.rbFaltaJustificadaModif);
            rbRetrasoModif = itemView.findViewById(R.id.rbRetrasoModif);
            rgAsistenciaModif = itemView.findViewById(R.id.rgAsistenciaModif);
            ibModificarAsis = itemView.findViewById(R.id.ibModificarAsis);
            ibEliminarAsis = itemView.findViewById(R.id.ibEliminarAsis);

            rgAsistenciaModif.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
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

            ibEliminarAsis.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            switch (which)
                            {
                                case DialogInterface.BUTTON_POSITIVE:
                                    try
                                    {
                                        // Obtengo el token del profesor
                                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(contexto);
                                        String token = prefs.getString("token", "");

                                        ControladorAsistencia controladorasis = new ControladorAsistencia(contexto);
                                        int idasistencia = asistencias.get(posicion).getId();
                                        System.out.println("Se va a borrar la asistencia " + idasistencia + ", token: " + token);
                                        if( controladorasis.eliminarAsistencia(token, idasistencia) )
                                        {
                                            Utilidades.mostrarToastText(contexto, "Asistencia eliminada correctamente.");
                                            ((VistaModificarDatosAsistencia)contexto).consultarAsistencias();
                                        }
                                        else
                                        {
                                            Utilidades.mostrarToastText(contexto, "Error al eliminar la asistencia.");
                                        }
                                    }
                                    catch (ServidorPHPException e)
                                    {
                                        System.out.println("Error -> " + e.toString());
                                    }
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                    builder.setMessage("¿Eliminar la asistencia?").setPositiveButton("Si", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            });

            ibModificarAsis.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int idasistencia = asistencias.get(posicion).getId();
                    String nuevovalor = "";

                    switch ( rgAsistenciaModif.getCheckedRadioButtonId() )
                    {
                        case R.id.rbAsisteModif:
                            //System.out.println("Valor nuevo: ASISTE");
                            nuevovalor = TipoAsistencia.ASISTE.toString();
                            break;
                        case R.id.rbFaltaModif:
                            //System.out.println("Valor nuevo: FALTA");
                            nuevovalor = TipoAsistencia.FALTA.toString();
                            break;
                        case R.id.rbFaltaJustificadaModif:
                            //System.out.println("Valor nuevo: FALTA JUSTIFICADA");
                            nuevovalor = TipoAsistencia.FALTAJUSTIFICADA.toString();
                            break;
                        case R.id.rbRetrasoModif:
                            //System.out.println("Valor nuevo: RETRASO");
                            nuevovalor = TipoAsistencia.RETRASO.toString();
                            break;
                    }

                    //System.out.println("Se va a modificar la asistencia " + idasistencia + " a " + nuevovalor);

                    try
                    {
                        // Obtengo el token del profesor
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(contexto);
                        String token = prefs.getString("token", "");

                        ControladorAsistencia controladorasis = new ControladorAsistencia(contexto);
                        if( controladorasis.modificarAsistencia(token, idasistencia, nuevovalor) )
                            Utilidades.mostrarToastText(contexto, "Asistencia modificada correctamente.");
                        else
                            Utilidades.mostrarToastText(contexto, "Error al modificar la asistencia.");
                    }
                    catch (ServidorPHPException e)
                    {
                        System.out.println("Error -> " + e.toString());
                    }

                }
            });

        }

        public void setPosicion(int pos)
        {
            this.posicion = pos;
        }
    }

    public AdaptadorModificarAsistencia(Context contexto, ArrayList<Asistencia> asistencias)
    {
        this.contexto = contexto;
        this.asistencias = asistencias;
        idsMarcadas = new HashMap<>();
        for(int i = 0; i < asistencias.size(); i++)
        {
            switch ( asistencias.get(i).getTipoasistencia() )
            {
                case "ASISTE":
                    idsMarcadas.put(i, R.id.rbAsisteModif);
                    break;
                case "FALTA":
                    idsMarcadas.put(i, R.id.rbFaltaModif);
                    break;
                case "FALTAJUSTIFICADA":
                    idsMarcadas.put(i, R.id.rbFaltaJustificadaModif);
                    break;
                case "RETRASO":
                    idsMarcadas.put(i, R.id.rbRetrasoModif);
                    break;
                default:
                    idsMarcadas.put(i, 0);
                    break;
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return asistencias.size();
    }

    /**
     * @param datos
     */
    public void add(ArrayList<Asistencia> datos)
    {
        asistencias.clear();
        asistencias.addAll(datos);
    }

    /**
     *
     */
    public void refrescar()
    {
        notifyDataSetChanged();
    }

    @Override
    public AdaptadorModificarAsistencia.ResumenTodaAsistenciaDia onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.elemento_lista_modificar_asistencia, viewGroup, false);
        AdaptadorModificarAsistencia.ResumenTodaAsistenciaDia pvh = new AdaptadorModificarAsistencia.ResumenTodaAsistenciaDia(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final AdaptadorModificarAsistencia.ResumenTodaAsistenciaDia asistenciadeturno, final int i)
    {
        asistenciadeturno.tAsignaturaAsistencia.setText(asistencias.get(i).getAsignatura());
        asistenciadeturno.setPosicion(i);
        if(idsMarcadas.get(i) != 0)
            asistenciadeturno.rgAsistenciaModif.check(idsMarcadas.get(i));
        /*else
            asistenciadeturno.rgAsistenciaModif.clearCheck();*/
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
