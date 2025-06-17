package utilidades;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public final class GeneradorToken
{
    public static String generarToken(String usuario, String contrasena) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        String resultado = "";
        String caracteres[] = {"!", "·", "$", "%", "&", "/", "(", ")", "=", "'", "¡", "?", "¿", "|", "@", "#", "~", "½", "¬", "{", "]", "}", "-", ".", ",", ";", ":", "_"};
        Random generador = new Random();

        resultado = usuario.substring(0, usuario.indexOf('@')) + contrasena.substring(0, contrasena.length()/2);
        for (int i = 0; i < 5; i++)
        {
            resultado = caracteres[generador.nextInt(caracteres.length)] + resultado;
        }
        for (int i = 0; i < 5; i++)
        {
            resultado += caracteres[generador.nextInt(caracteres.length)];
        }
        //System.out.println("Ha salido: " + resultado);
        return sha1Hash(resultado);
    }

    private static String sha1Hash( String toHash ) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        String hash = "";
        MessageDigest digest = MessageDigest.getInstance( "SHA-1" );
        byte[] bytes = toHash.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        bytes = digest.digest();

        // This is ~55x faster than looping and String.formating()
        hash = bytesToHex( bytes );
        return hash;
    }

    private static String bytesToHex( byte[] bytes )
    {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[ bytes.length * 2 ];
        for( int j = 0; j < bytes.length; j++ )
        {
            int v = bytes[ j ] & 0xFF;
            hexChars[ j * 2 ] = hexArray[ v >>> 4 ];
            hexChars[ j * 2 + 1 ] = hexArray[ v & 0x0F ];
        }
        return new String( hexChars );
    }
}
