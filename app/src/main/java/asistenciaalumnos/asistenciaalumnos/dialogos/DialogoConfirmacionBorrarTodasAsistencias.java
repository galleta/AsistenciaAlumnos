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

import asistenciaalumnos.asistenciaalumnos.R;
import controlador.ControladorAsistencia;
import internet.ServidorPHPException;
import utilidades.Utilidades;

/**
 * Created by francis on 23/12/17.
 */

@SuppressLint("ValidFragment")
public class DialogoConfirmacionBorrarTodasAsistencias extends DialogFragment
{
    private Context contexto;
    private ControladorAsistencia controladorasis;

    @SuppressLint("ValidFragment")
    public DialogoConfirmacionBorrarTodasAsistencias(Context contexto)
    {
        this.contexto = contexto;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        builder.setMessage(Utilidades.obtenerStringXML(contexto, R.string.texto_mensaje_eliminar_asistencias))
                .setTitle(Utilidades.obtenerStringXML(contexto, R.string.titulo_eliminar_asistencias))
                .setPositiveButton(Utilidades.obtenerStringXML(contexto, R.string.texto_aceptar), new
                        DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                controladorasis = new ControladorAsistencia(contexto);
                                try
                                {
                                    // Obtengo el token del profesor
                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(contexto);
                                    String token = prefs.getString("token", "");

                                    if( controladorasis.eliminarTodasAsistencias(token) )
                                    {
                                        Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_todas_asistencias_borrado_ok));
                                    }
                                    else
                                    {
                                        Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_asistencias_borrado_nook));
                                    }
                                }
                                catch (ServidorPHPException e)
                                {
                                    Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_asistencias_borrado_nook));
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