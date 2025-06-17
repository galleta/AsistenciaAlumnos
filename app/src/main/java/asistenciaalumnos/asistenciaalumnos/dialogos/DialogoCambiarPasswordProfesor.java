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
import android.view.LayoutInflater;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import asistenciaalumnos.asistenciaalumnos.R;
import controlador.ControladorProfesor;
import internet.ServidorPHPException;
import utilidades.GeneradorToken;
import utilidades.Utilidades;

/**
 * Created by francis on 23/12/17.
 */

@SuppressLint("ValidFragment")
public class DialogoCambiarPasswordProfesor extends DialogFragment
{
    private Context contexto;
    private ControladorProfesor controladorp;

    @SuppressLint("ValidFragment")
    public DialogoCambiarPasswordProfesor(Context contexto)
    {
        this.contexto = contexto;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialogo_cambiar_password, null))
                .setTitle(Utilidades.obtenerStringXML(contexto, R.string.texto_cambiar_contrasena))
                .setNegativeButton(Utilidades.obtenerStringXML(contexto, R.string.texto_cancelar), new
                        DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                dialog.cancel();
                            }
                        })
                .setPositiveButton(Utilidades.obtenerStringXML(contexto, R.string.texto_aceptar),
                        new
                                DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        controladorp = new ControladorProfesor(contexto);
                                        EditText tPasswordAntiguo, tPassword1, tPassword2;

                                        tPasswordAntiguo = getDialog().findViewById(R.id.tPasswordAntiguo);
                                        tPassword1 = getDialog().findViewById(R.id.tPassword1);
                                        tPassword2 = getDialog().findViewById(R.id.tPassword2);

                                        if(!tPasswordAntiguo.getText().toString().isEmpty() &&
                                                !tPassword1.getText().toString().isEmpty() &&
                                                !tPassword2.getText().toString().isEmpty())
                                        {
                                            // Obtengo el usuario y lo pongo en la cabecera del menu
                                            SharedPreferences prefs =
                                                    PreferenceManager.getDefaultSharedPreferences(
                                                            contexto);
                                            String usu = prefs.getString("usuario", "");

                                            try
                                            {
                                                if( controladorp.loginProfesor(usu, tPasswordAntiguo.getText().toString()) )
                                                {
                                                    if(tPassword1.getText().toString().equals(tPassword2.getText().toString()))
                                                    {
                                                        String nuevopass = tPassword1.getText().toString();

                                                        // Obtengo el token del profesor
                                                        //SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(contexto);
                                                        String token = prefs.getString("token", "");

                                                        String nuevotoken = GeneradorToken.generarToken(usu, nuevopass);

                                                        controladorp.modificarPasswordProfesor(token, usu, nuevopass, nuevotoken);

                                                        // Guardo el nuevo token en las preferencias
                                                        //SharedPreferences prefs3 = PreferenceManager.getDefaultSharedPreferences(contexto);
                                                        SharedPreferences.Editor editor = prefs.edit();
                                                        editor.putString("token", nuevotoken);
                                                        editor.commit();

                                                        Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_password_cambiado_ok));
                                                    }
                                                    else
                                                    {
                                                        Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_error_password_no_coinciden));
                                                    }
                                                }
                                                else
                                                {
                                                    Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_error_password));
                                                }
                                            }
                                            catch (ServidorPHPException | NoSuchAlgorithmException | UnsupportedEncodingException e)
                                            {
                                                System.out.println("Error -> " + e.toString());
                                            }
                                        }
                                        else
                                        {
                                            Utilidades.mostrarToastText(contexto, Utilidades.obtenerStringXML(contexto, R.string.texto_error_faltan_datos));
                                        }

                                        dialog.cancel();
                                    }
                                });
        return builder.create();
    }
}
