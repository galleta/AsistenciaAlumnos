package asistenciaalumnos.asistenciaalumnos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import controlador.ControladorProfesor;
import internet.AccesoInternet;
import internet.ServidorPHPException;
import utilidades.GeneradorToken;
import utilidades.Utilidades;

public class VistaLogin extends AppCompatActivity implements View.OnClickListener
{
    private ImageButton user_profile_photo;
    private Toolbar toolbar_login;
    private Button bAcceder;
    private CheckBox cbRecordarme;
    private EditText tUsuario, tPass;
    private AccesoInternet accesointernet;
    private final String CORREO = "@itponiente.com";
    private String usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ***** Obtengo los recursos de la aplicación *****
        //toolbar_login = (Toolbar) findViewById(R.id.toolbar_login);
        user_profile_photo = (ImageButton) findViewById(R.id.user_profile_photo);
        bAcceder = (Button) findViewById(R.id.bAcceder);
        cbRecordarme = (CheckBox) findViewById(R.id.cbRecordarme);
        tUsuario = (EditText) findViewById(R.id.tUsuario);
        tPass = (EditText) findViewById(R.id.tPass);
        // *************************************************

        //setSupportActionBar(toolbar_login);
        //toolbar_login.setTitle(Utilidades.obtenerStringXML(this, R.string.app_name));

        accesointernet = new AccesoInternet(this);

        // Hago que la imagen del perfil que aparece en la pantalla sea circular
        // Imagen: http://www.iconarchive.com/show/small-n-flat-icons-by-paomedia/user-female-alt-icon.html

        Bitmap avatar = BitmapFactory.decodeResource(getResources(), R.drawable.profile_icon);
        RoundedBitmapDrawable roundDrawable = RoundedBitmapDrawableFactory.create(getResources(), avatar);
        roundDrawable.setCircular(true);
        user_profile_photo.setImageDrawable(roundDrawable);

        bAcceder.setOnClickListener(this);

        //Obteniendo la instancia del Intent
        Intent intent = getIntent();
        String procedencia = intent.getStringExtra("vengo");

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(
                        this);

        // Si procedencia vale main es que vengo de la pantalla principal y tengo que resetear los datos
        if( procedencia != null && procedencia.equals("main") )
        {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("recordarme", Boolean.FALSE);
            editor.putString("usuario", "");
            editor.putString("password", "");
            editor.putString("token", "");
            editor.commit();
        }

        // Compruebo si la opcion de recordar estaba activada
        Boolean recordarme = prefs.getBoolean("recordarme", Boolean.FALSE);
        //String usu = sharedPref.getString("usuario", "");
        //System.out.println("Usuario: " + usu + ", recordar: " + recordarme);

        if( recordarme )
        {
            irAPrincipal();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch( v.getId() )
        {
            case R.id.bAcceder:
                if( accesointernet.verificarConexion().equals(AccesoInternet.TipoConexionInternet.NO_CONEXION) )
                {
                    Utilidades.mostrarToastText(this, Utilidades.obtenerStringXML(this, R.string.error_internet));
                }
                else
                {
                    usuario = tUsuario.getText().toString();
                    String pass = tPass.getText().toString();
                    Boolean recordar = cbRecordarme.isChecked();

                    ControladorProfesor controladorp = new ControladorProfesor(this);

                    try
                    {
                        //System.out.println("*******************************************************************");
                        //System.out.println("Voy a hacer login con " + usuario + "@itponiente.com");
                        // Actualizo el usuario
                        usuario += CORREO;

                        //System.out.println("********** VOY A MIRAR EL CORREO -> " + usuario + " CON EL PASS -> " + pass);

                        if( controladorp.loginProfesor(usuario, pass) )
                        {
                            //System.out.println("********** HACE LOGIN **********");
                            String token = GeneradorToken.generarToken(usuario, pass);
                            //System.out.println("Su nuevo token va a ser: " + token);

                            // Si el login ha ido OK modifico el token al nuevo
                            Boolean nuevotoken = controladorp.modificarTokenProfesor(usuario, token);
                            //System.out.println("lo del token " + nuevotoken);

                            SharedPreferences prefs =
                                    PreferenceManager.getDefaultSharedPreferences(
                                            this);
                            SharedPreferences.Editor editor = prefs.edit();

                            // Guardo las preferencias
                            editor.putBoolean("recordarme", recordar);
                            editor.putString("usuario", usuario);
                            editor.putString("password", pass);
                            editor.putString("token", token);

                            // Guardo los parametros si se pulsa recordar
                            if( recordar )
                            {
                                editor.putString("password", pass);
                            }
                            editor.commit();
                            tPass.setError(null);

                            irAPrincipal();
                        }
                        else
                        {
                            System.out.println("********** ERROR 1 **********");
                            tPass.setError(Utilidades.obtenerStringXML(this, R.string.texto_error_login));
                            Utilidades.mostrarToastText(this, Utilidades.obtenerStringXML(this, R.string.texto_error_login));
                        }
                    }
                    catch (ServidorPHPException | NoSuchAlgorithmException | UnsupportedEncodingException e)
                    {
                        System.out.println("********** ERROR 2 **********");
                        tPass.setError(e.getMessage());
                        //Utilidades.mostrarToastText(this, Utilidades.obtenerStringXML(this, R.string.texto_error_servidor));
                        Utilidades.mostrarToastText(this, e.getMessage());
                    }
                }

                break;
        }
    }

    /**
     * Va a la pantalla principal
     */
    private void irAPrincipal()
    {
        Intent intent = new Intent(VistaLogin.this, VistaPantallaPrincipal.class);
        // Hago que se limpie la pila de llamadas
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("usuario", usuario);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
