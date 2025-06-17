package asistenciaalumnos.asistenciaalumnos.dialogos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import asistenciaalumnos.asistenciaalumnos.VistaConsultarProfesores;
import asistenciaalumnos.asistenciaalumnos.R;
import controlador.ControladorProfesor;
import internet.ServidorPHPException;
import utilidades.Utilidades;

/**
 * Created by francis on 3/01/18.
 */

@SuppressLint("ValidFragment")
public class DialogoConfirmarEliminarProfesor extends DialogFragment
{
    private Context contexto;
    private String email;
    private ControladorProfesor controladorp;
    private VistaConsultarProfesores actividad;

    @SuppressLint("ValidFragment")
    public DialogoConfirmarEliminarProfesor(Context contexto, String email, VistaConsultarProfesores actividad)
    {
        this.contexto = contexto;
        this.email = email;
        this.actividad = actividad;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        builder.setMessage(Utilidades.obtenerStringXML(contexto, R.string.texto_mensaje_eliminar_profesor))
                .setTitle(Utilidades.obtenerStringXML(contexto, R.string.titulo_eliminar_profesor))
                .setPositiveButton(Utilidades.obtenerStringXML(contexto, R.string.texto_aceptar), new
                        DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                controladorp = new ControladorProfesor(contexto);
                                System.out.println("Me llega para borrar " + email);
                                try
                                {
                                    // Obtengo el token del profesor
                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(contexto);
                                    String token = prefs.getString("token", "");

                                    if( controladorp.eliminarProfesor(token, email) )
                                    {
                                        Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_profesor_borrado_ok));
                                        actividad.mostrarProfesores();
                                    }
                                    else
                                        Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_profesor_borrado_error));
                                }
                                catch (ServidorPHPException e)
                                {
                                    Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_profesor_borrado_error));
                                }
                                dialog.cancel();
                            }
                        })
                .setNegativeButton(Utilidades.obtenerStringXML(contexto, R.string.texto_cancelar), new
                        DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.cancel();
                            }
                        });
        return builder.create();
    }
}
