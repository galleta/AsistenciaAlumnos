package utilidades;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import asistenciaalumnos.asistenciaalumnos.R;

/**
 * Created by francis on 21/09/16.
 */

public final class Utilidades
{
    public static int temaactual = -1;
    public static String nombre, sexo;
    public static SimpleDateFormat formatofecha = new SimpleDateFormat("dd/MM/yyyy");
    public static String urlservidor = "https://itponiente.com/appsistencia/";

    /**
     * Valida un email
     * @param email Email para validad
     * @return Verdadero si el email es correcto, falso en caso contrario
     */
    public static boolean validarEmail(String email)
    {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    /**
     * Devuelve la versión de la app
     * @param context Contexto de la app
     * @return Versión de la app
     */
    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        int version = 0;
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            version = pi.versionCode;
        } catch (PackageManager.NameNotFoundException ex) {}
        return version;
    }

    /**
     * Muestra un texto en la pantalla
     *
     * @param contexto Contexto
     * @param texto    Texto a mostrar
     */
    public static void mostrarToastText(Context contexto, String texto) {
        Toast toast = Toast.makeText(contexto, texto, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Devuelve el String indicado del fichero strings.xml
     *
     * @param contexto Contexto
     * @param recurso  String que se quiere
     * @return String almacenado en strings.xml
     */
    public static String obtenerStringXML(Context contexto, int recurso) {
        return contexto.getString(recurso).toString();
    }

    /**
     * Muestra un mensaje con un snack bar
     *
     * @param view    Pantalla desde donde se muestra el mensaje
     * @param mensaje Mensaje a mostrar
     */
    public static void mostrarSnackBar(View view, String mensaje) {
        Snackbar.make(view, mensaje, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    /**
     * Rellena un spinner con unos valores concretos
     *
     * @param spin     Spinner a rellenar
     * @param datos    Valores para rellenar el spinner
     * @param contexto Contexto de la app
     */
    public static void rellenarSpinner(Spinner spin, ArrayList<String> datos, Context contexto) {
		/*
		 * Primero vacío el spinner
		 */

        ArrayList<String> vacio = new ArrayList<>();
        ArrayAdapter<String> adaptadorvacio = new ArrayAdapter<>(contexto, android.R.layout.simple_spinner_item, vacio);
        adaptadorvacio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adaptadorvacio);

		/*
		 * Relleno el spinner
		 */

        ArrayAdapter<String> adaptador = new ArrayAdapter<>(contexto, android.R.layout.simple_spinner_item, datos);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adaptador);
    }

    /**
     * Rellena un spinner con unos valores concretos
     *
     * @param spin     Spinner a rellenar
     * @param recurso  Recurso donde este el array
     * @param contexto Contexto de la app
     */
    public static void rellenarSpinner(Spinner spin, int recurso, Context contexto) {
		/*
		 * Primero vacío el spinner
		 */

        ArrayList<String> vacio = new ArrayList<>();
        ArrayAdapter<String> adaptadorvacio = new ArrayAdapter<>(contexto, android.R.layout.simple_spinner_item, vacio);
        adaptadorvacio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adaptadorvacio);

		/*
		 * Relleno el spinner
		 */

        ArrayAdapter<String> adaptador = new ArrayAdapter<>(contexto, android.R.layout.simple_spinner_item, contexto.getResources().getStringArray(recurso));
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adaptador);
    }

    public static String mesString(int mes, Context contexto) {
        String valor = "";

        switch (mes) {
            case 1:
                valor = Utilidades.obtenerStringXML(contexto, R.string.enero);
                break;
            case 2:
                valor = Utilidades.obtenerStringXML(contexto, R.string.febrero);
                break;
            case 3:
                valor = Utilidades.obtenerStringXML(contexto, R.string.marzo);
                break;
            case 4:
                valor = Utilidades.obtenerStringXML(contexto, R.string.abril);
                break;
            case 5:
                valor = Utilidades.obtenerStringXML(contexto, R.string.mayo);
                break;
            case 6:
                valor = Utilidades.obtenerStringXML(contexto, R.string.junio);
                break;
            case 7:
                valor = Utilidades.obtenerStringXML(contexto, R.string.julio);
                break;
            case 8:
                valor = Utilidades.obtenerStringXML(contexto, R.string.agosto);
                break;
            case 9:
                valor = Utilidades.obtenerStringXML(contexto, R.string.septiembre);
                break;
            case 10:
                valor = Utilidades.obtenerStringXML(contexto, R.string.octubre);
                break;
            case 11:
                valor = Utilidades.obtenerStringXML(contexto, R.string.noviembre);
                break;
            case 12:
                valor = Utilidades.obtenerStringXML(contexto, R.string.diciembre);
                break;
        }

        return valor;
    }

    public static int obtenerDiasMes(int mes, int anio) {
        Calendar cal = new GregorianCalendar(anio, Calendar.JANUARY, 1);

        switch (mes) {
            case 2:
                cal = new GregorianCalendar(anio, Calendar.FEBRUARY, 1);
                break;
            case 3:
                cal = new GregorianCalendar(anio, Calendar.MARCH, 1);
                break;
            case 4:
                cal = new GregorianCalendar(anio, Calendar.APRIL, 1);
                break;
            case 5:
                cal = new GregorianCalendar(anio, Calendar.MAY, 1);
                break;
            case 6:
                cal = new GregorianCalendar(anio, Calendar.JUNE, 1);
                break;
            case 7:
                cal = new GregorianCalendar(anio, Calendar.JULY, 1);
                break;
            case 8:
                cal = new GregorianCalendar(anio, Calendar.AUGUST, 1);
                break;
            case 9:
                cal = new GregorianCalendar(anio, Calendar.SEPTEMBER, 1);
                break;
            case 10:
                cal = new GregorianCalendar(anio, Calendar.OCTOBER, 1);
                break;
            case 11:
                cal = new GregorianCalendar(anio, Calendar.NOVEMBER, 1);
                break;
            case 12:
                cal = new GregorianCalendar(anio, Calendar.DECEMBER, 1);
                break;
        }

        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * Habilita o deshabilita un elemento
     * @param view Elemento a habilitar o deshabilitar
     * @param enabled Verdadero para habilitar, falso para deshabilitar
     */
    public static void enableDisableView(View view, boolean enabled)
    {
        view.setEnabled(enabled);

        if ( view instanceof ViewGroup)
        {
            ViewGroup group = (ViewGroup)view;

            for ( int idx = 0 ; idx < group.getChildCount() ; idx++ )
            {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }

}
