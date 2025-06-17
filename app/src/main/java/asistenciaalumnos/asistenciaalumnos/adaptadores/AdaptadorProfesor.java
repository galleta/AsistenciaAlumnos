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

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import asistenciaalumnos.asistenciaalumnos.VistaConsultarProfesores;
import asistenciaalumnos.asistenciaalumnos.dialogos.DialogoConfirmarEliminarProfesor;
import asistenciaalumnos.asistenciaalumnos.VistaModificarDatosProfesor;
import asistenciaalumnos.asistenciaalumnos.R;
import controlador.ControladorProfesor;
import internet.ServidorPHPException;
import modelo.profesor.Profesor;
import utilidades.GeneradorToken;
import utilidades.Utilidades;

/**
 * Created by francis on 29/12/17.
 */

public class AdaptadorProfesor extends RecyclerView.Adapter<AdaptadorProfesor.HolderProfesor>
{
    /**
     * Clase interna equivalente al Holder de los elementos
     */
    public static class HolderProfesor extends RecyclerView.ViewHolder
    {
        TextView tNombreProfesor, tEmailProfesor, tTipoProfesor, tAsignaturasProfesor;
        ImageButton ibEliminarProfesor, ibModificarProfesor, ibResetearPasswordProfesor;

        HolderProfesor(View itemView)
        {
            super(itemView);
            tNombreProfesor = itemView.findViewById(R.id.tNombreProfesor);
            tEmailProfesor = itemView.findViewById(R.id.tEmailProfesor);
            tTipoProfesor = itemView.findViewById(R.id.tTipoProfesor);
            tAsignaturasProfesor = itemView.findViewById(R.id.tAsignaturasProfesor);
            ibEliminarProfesor = itemView.findViewById(R.id.ibEliminarProfesor);
            ibModificarProfesor = itemView.findViewById(R.id.ibModificarProfesor);
            ibResetearPasswordProfesor = itemView.findViewById(R.id.ibResetearPasswordProfesor);
        }
    };

    private ArrayList<Profesor> profesores;
    private Context contexto;
    private Boolean mostrarbotones;
    private VistaConsultarProfesores actividad;

    public AdaptadorProfesor(ArrayList<Profesor> profesores, Context contexto, Boolean mostrarbotones, VistaConsultarProfesores actividad)
    {
        this.profesores = profesores;
        this.contexto = contexto;
        this.mostrarbotones = mostrarbotones;
        this.actividad = actividad;
    }

    /**
     *
     * @param datos
     */
    public void add(ArrayList<Profesor> datos)
    {
        profesores.clear();
        profesores.addAll(datos);
    }

    /**
     *
     */
    public void refrescar()
    {
        notifyDataSetChanged();
    }

    @Override
    public HolderProfesor onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.elemento_lista_consultar_profesores, viewGroup, false);
        HolderProfesor pvh = new HolderProfesor(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(HolderProfesor profesordeturno, final int i)
    {
        profesordeturno.tNombreProfesor.setText(profesores.get(i).getNombre());
        profesordeturno.tEmailProfesor.setText(profesores.get(i).getEmail());
        profesordeturno.tTipoProfesor.setText(profesores.get(i).getTipo());
        profesordeturno.tAsignaturasProfesor.setText(profesores.get(i).getAsignaturasImpartidas());

        if( !mostrarbotones )
        {
            profesordeturno.ibEliminarProfesor.setVisibility(View.GONE);
            profesordeturno.ibModificarProfesor.setVisibility(View.GONE);
            profesordeturno.ibResetearPasswordProfesor.setVisibility(View.GONE);
        }
        else
        {
            profesordeturno.ibEliminarProfesor.setVisibility(View.VISIBLE);
            profesordeturno.ibModificarProfesor.setVisibility(View.VISIBLE);
            profesordeturno.ibResetearPasswordProfesor.setVisibility(View.VISIBLE);
        }

        profesordeturno.ibEliminarProfesor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            /**
             *
             */
            public void onClick(View v)
            {
                try
                {
                    final Activity activity = (Activity) contexto;

                    FragmentManager fragmentManager = activity.getFragmentManager();
                    DialogoConfirmarEliminarProfesor dialogo2 = new DialogoConfirmarEliminarProfesor(contexto, profesores.get(i).getEmail(), actividad);
                    dialogo2.show(fragmentManager, "tagAlerta");
                }
                catch (ClassCastException e)
                {
                    Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_profesor_borrado_error));
                }
            }
        });

        profesordeturno.ibModificarProfesor.setOnClickListener(new View.OnClickListener(){
            @Override
            /**
             *
             */
            public void onClick(View v)
            {
                Profesor p = new Profesor(profesores.get(i).getNombre(), profesores.get(i).getEmail(), profesores.get(i).getTipo(), profesores.get(i).getAsignaturasImpartidas());

                Intent intent = new Intent(contexto, VistaModificarDatosProfesor.class);
                intent.putExtra("profesor", p);
                contexto.startActivity(intent);
            }
        });

        profesordeturno.ibResetearPasswordProfesor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            /**
             *
             */
            public void onClick(View v)
            {
                ControladorProfesor controladorp = new ControladorProfesor(contexto);
                try
                {
                    String passpordefecto = "poniente123";

                    // Obtengo el usuario y lo pongo en la cabecera del menu
                    SharedPreferences prefs =
                            PreferenceManager.getDefaultSharedPreferences(
                                    contexto);

                    // Obtengo el token del profesor
                    //SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(contexto);
                    String token = prefs.getString("token", "");

                    String emailprofesor = profesores.get(i).getEmail();

                    String nuevotoken = GeneradorToken.generarToken(emailprofesor, passpordefecto);

                    if( controladorp.modificarPasswordProfesor(token, emailprofesor, passpordefecto, nuevotoken) )
                    {
                        // Guardo el nuevo token en las preferencias
                        //SharedPreferences prefs3 = PreferenceManager.getDefaultSharedPreferences(contexto);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("token", nuevotoken);
                        editor.commit();
                        Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_password_reseteado_ok));
                    }
                    else
                    {
                        Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_password_reseteado_nook));
                    }
                }
                catch (ServidorPHPException | NoSuchAlgorithmException | UnsupportedEncodingException e)
                {
                    Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_password_reseteado_nook));
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return profesores.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
