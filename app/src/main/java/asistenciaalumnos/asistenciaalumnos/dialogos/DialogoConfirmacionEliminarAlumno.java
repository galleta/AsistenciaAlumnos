package asistenciaalumnos.asistenciaalumnos.dialogos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import asistenciaalumnos.asistenciaalumnos.VistaConsultarAlumnos;
import asistenciaalumnos.asistenciaalumnos.R;
import controlador.ControladorAlumno;
import internet.ServidorPHPException;
import utilidades.Utilidades;

/**
 * Created by francis on 23/12/17.
 */

@SuppressLint("ValidFragment")
public class DialogoConfirmacionEliminarAlumno extends DialogFragment
{
    private Context contexto;
    private String token;
    private int id;
    private ControladorAlumno controladoralum;
    private VistaConsultarAlumnos actividadconsultar;

    @SuppressLint("ValidFragment")
    public DialogoConfirmacionEliminarAlumno(Context contexto, int id, String token, VistaConsultarAlumnos actividadconsultar)
    {
        this.contexto = contexto;
        this.token = token;
        this.actividadconsultar = actividadconsultar;
        this.id = id;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        final int identificador = id;
        builder.setMessage(Utilidades.obtenerStringXML(contexto, R.string.texto_mensaje_eliminar_alumno))
                .setTitle(Utilidades.obtenerStringXML(contexto, R.string.titulo_eliminar_alumno))
                .setPositiveButton(Utilidades.obtenerStringXML(contexto, R.string.texto_aceptar), new
                        DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                controladoralum = new ControladorAlumno(contexto);
                                try
                                {
                                    if( controladoralum.borrarAlumnoConAsistencias(token, identificador) )
                                    {
                                        Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_alumno_borrado_ok));
                                        actividadconsultar.mostrarAlumnos();
                                    }
                                    else
                                        Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_alumno_borrado_nook));
                                }
                                catch (ServidorPHPException e)
                                {
                                    Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_alumno_borrado_nook));
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