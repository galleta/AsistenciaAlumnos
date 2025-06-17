package internet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by francis on 21/09/16.
 */

public class AccesoInternet
{
    private Context contexto;

    /**
     * Enumerado para los tipos de conxiones posibles a internet
     */
    public enum TipoConexionInternet
    {
        NO_CONEXION,
        WIFI,
        MOVIL
    }

    /**
     *
     * @param contexto
     */
    public AccesoInternet(Context contexto)
    {
        this.contexto = contexto;
    }

    /**
     * Verifica si hay conexión a internet y si la hay indica si es por wifi o por datos
     * @return Tipo de conexión a internet
     */
    public TipoConexionInternet verificarConexion()
    {
        TipoConexionInternet conectado = TipoConexionInternet.NO_CONEXION;

        ConnectivityManager cm = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
            {
                // Conectado al wifi
                conectado = TipoConexionInternet.WIFI;
            }
            else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                // Conectado a los datos
                conectado = TipoConexionInternet.MOVIL;
            }
        }
        else
        {
            // Sin conexión a internet
            conectado = TipoConexionInternet.NO_CONEXION;
        }

        return conectado;
    }
}


