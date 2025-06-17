package asistenciaalumnos.asistenciaalumnos.adaptadores;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import asistenciaalumnos.asistenciaalumnos.VistaConsultarAlumnos;
import asistenciaalumnos.asistenciaalumnos.dialogos.DialogoConfirmacionEliminarAlumno;
import asistenciaalumnos.asistenciaalumnos.VistaModificarDatosAlumno;
import asistenciaalumnos.asistenciaalumnos.R;
import modelo.alumno.Alumno;
import modelo.alumno.AlumnoMatriculado;
import utilidades.Utilidades;

/**
 * Created by francis on 21/09/16.
 */

public class AdaptadorAlumnosMatriculados extends RecyclerView.Adapter<AdaptadorAlumnosMatriculados.ResumenAlumno>
{
    /**
     * Clase interna equivalente al Holder de los elementos
     */
    public static class ResumenAlumno extends RecyclerView.ViewHolder
    {
        TextView tNombreAlumnoL, tApellidosAlumnoL;
        ImageButton ibEliminarAlumno, ibModificarAlumno;

        ResumenAlumno(View itemView)
        {
            super(itemView);
            tNombreAlumnoL = (TextView) itemView.findViewById(R.id.tNombreAlumnoL);
            tApellidosAlumnoL = (TextView) itemView.findViewById(R.id.tApellidosAlumnoL);
            ibEliminarAlumno = (ImageButton) itemView.findViewById(R.id.ibEliminarAlumno);
            ibModificarAlumno = (ImageButton) itemView.findViewById(R.id.ibModificarAlumno);
        }
    };

    private ArrayList<AlumnoMatriculado> alumnos;
    private Context contexto;
    private VistaConsultarAlumnos actividadconsultar;
    private Boolean mostrarbotones;

    public AdaptadorAlumnosMatriculados(Context contexto, Boolean mostrarbotones, ArrayList<AlumnoMatriculado> alumnos, VistaConsultarAlumnos actividadconsultar)
    {
        this.contexto = contexto;
        this.alumnos = alumnos;
        this.actividadconsultar = actividadconsultar;
        this.mostrarbotones = mostrarbotones;
    }

    @Override
    public int getItemCount()
    {
        return alumnos.size();
    }

    /**
     *
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

    @Override
    public ResumenAlumno onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.elemento_lista_consultar_alumnos, viewGroup, false);
        ResumenAlumno pvh = new ResumenAlumno(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ResumenAlumno alumnodeturno, final int i)
    {
        alumnodeturno.tNombreAlumnoL.setText(alumnos.get(i).getNombre());
        alumnodeturno.tApellidosAlumnoL.setText(alumnos.get(i).getApellidos());

        if( mostrarbotones )
        {
            alumnodeturno.ibEliminarAlumno.setOnClickListener(new View.OnClickListener() {
                @Override
                /**
                 *
                 */
                public void onClick(View v) {
                    try
                    {
                        final Activity activity = (Activity) contexto;
                        // Obtengo el token del profesor
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(contexto);
                        String token = prefs.getString("token", "");

                        FragmentManager fragmentManager = activity.getFragmentManager();
                        DialogoConfirmacionEliminarAlumno dialogo2 = new DialogoConfirmacionEliminarAlumno(contexto, alumnos.get(i).getId(), token, actividadconsultar);
                        dialogo2.show(fragmentManager, "tagAlerta");
                    }
                    catch (ClassCastException e)
                    {
                        Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_alumno_borrado_nook));
                    }
                }
            });

            alumnodeturno.ibModificarAlumno.setOnClickListener(new View.OnClickListener() {
                @Override
                /**
                 *
                 */
                public void onClick(View v) {
                    irAModificarAlumno(alumnos.get(i));
                }
            });
            alumnodeturno.ibEliminarAlumno.setVisibility(View.VISIBLE);
            alumnodeturno.ibModificarAlumno.setVisibility(View.VISIBLE);
        }
        else
        {
            alumnodeturno.ibEliminarAlumno.setVisibility(View.GONE);
            alumnodeturno.ibModificarAlumno.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * Va a la pantalla de anotar asistencias
     * @param alumno Alumno a modificar
     */
    private void irAModificarAlumno(Alumno alumno)
    {
        Intent intent = new Intent(contexto, VistaModificarDatosAlumno.class);
        intent.putExtra("alumno", alumno);
        contexto.startActivity(intent);
    }
}
