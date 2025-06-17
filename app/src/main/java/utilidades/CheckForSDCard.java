package utilidades;

import android.os.Environment;

public class CheckForSDCard
{
    // Comprueba si la tarjeta SD está montada o no

    public static boolean isSDCardPresent()
    {
        Boolean montado = Boolean.FALSE;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            montado = Boolean.TRUE;
        }
        return montado;
    }
}
