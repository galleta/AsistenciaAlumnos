package asistenciaalumnos.asistenciaalumnos.dialogos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import asistenciaalumnos.asistenciaalumnos.R;
import controlador.ControladorAlumno;
import internet.ServidorPHPException;
import utilidades.Utilidades;

@SuppressLint("ValidFragment")
public class DialogoConfirmacionDesmatricularAlumno extends DialogFragment
{
    private Context contexto;
    private String token, ciclo, curso;
    private int idalumno;

    @SuppressLint("ValidFragment")
    public DialogoConfirmacionDesmatricularAlumno(Context contexto, String token, String ciclo, String curso, int idalumno)
    {
        this.contexto = contexto;
        this.token = token;
        this.ciclo = ciclo;
        this.curso = curso;
        this.idalumno = idalumno;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        builder.setMessage(Utilidades.obtenerStringXML(contexto, R.string.texto_aviso_eliminar_matricula))
                .setTitle(Utilidades.obtenerStringXML(contexto, R.string.titulo_desmatricular_alumno))
                .setPositiveButton(Utilidades.obtenerStringXML(contexto, R.string.texto_aceptar), new
                        DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                ControladorAlumno controladoralum = new ControladorAlumno(contexto);
                                try
                                {
                                    if( controladoralum.desmatricularAlumnoCicloCurso(token, idalumno, ciclo, curso) )
                                        Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_alumno_desmatriculado_ok));
                                    else
                                        Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_alumno_borrado_nook));
                                }
                                catch (ServidorPHPException e)
                                {
                                    Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_alumno_desmatriculado_nook));
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
