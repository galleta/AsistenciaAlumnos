package controlador;

import android.content.Context;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import internet.JSONParser;
import internet.ServidorPHPException;
import modelo.alumno.Alumno;
import modelo.asignatura.Asignatura;
import modelo.faltaasistencia.FaltaAsistencia;
import utilidades.Utilidades;

public class ControladorFaltaAsistencia
{
    private Context contexto;

    private final int RESULTADO_OK = 1;
    private final int RESULTADO_ERROR = 2;
    private final int RESULTADO_ERROR_DESCONOCIDO = 3;

    private final String TAG = "ControladorFaltaAsistencia";

    private static String urlobtenerTotalFaltasAlumnoAsignatura = Utilidades.urlservidor + "obtenerTotalFaltasAlumnoAsignatura.php";

    public ControladorFaltaAsistencia(Context contexto)
    {
        this.contexto = contexto;
    }

    /**
     * Obtiene la cantidad de faltas, faltas justificadas y retrasos de un alumno en una asignatura hasta el momento por fechas
     * @param token token Token válido de un profesor
     * @param idalumno Identificador del alumno
     * @param idasignatura Identificador de la asignatura
     * @return Cantidad de faltas, faltas justificadas y retrasos de un alumno en una asignatura hasta el momento por fechas
     * @throws ServidorPHPException
     */
    public ArrayList<FaltaAsistencia> obtenerTotalFaltasAlumnoAsignatura(String token, int idalumno, int idasignatura) throws ServidorPHPException
    {
        ArrayList<FaltaAsistencia> faltas = new ArrayList<>();

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("idalumno", String.valueOf(idalumno)));
            parametros.add(new Pair<>("idasignatura", String.valueOf(idasignatura)));

            datos = parser.getJSONArrayFromUrl(urlobtenerTotalFaltasAlumnoAsignatura, parametros);

            if( datos != null )
            {
                if( datos != null )
                {
                    int resultado = datos.getJSONObject(0).getInt("estado");
                    //System.out.println("EL RESULTADO ES " + resultado);

                    switch(resultado)
                    {
                        case RESULTADO_OK:
                            JSONArray datosfaltasasistencias = datos.getJSONObject(0).getJSONArray("mensaje");
                            for(int i = 0; i < datosfaltasasistencias.length(); i++)
                            {
                                int idalu = datosfaltasasistencias.getJSONObject(i).getInt("idalumno");
                                String nombrealu = datosfaltasasistencias.getJSONObject(i).getString("nombrealumno");
                                String apellidosalu = datosfaltasasistencias.getJSONObject(i).getString("apellidosalumno");
                                Alumno alu = new Alumno(idalu, nombrealu, apellidosalu);
                                int idasig = datosfaltasasistencias.getJSONObject(i).getInt("idasignatura");
                                String nombreasig = datosfaltasasistencias.getJSONObject(i).getString("nombreasig");
                                String nombreabreasig = datosfaltasasistencias.getJSONObject(i).getString("nombreabreasig");
                                String cicloasig = datosfaltasasistencias.getJSONObject(i).getString("cicloasig");
                                String cursoasig = datosfaltasasistencias.getJSONObject(i).getString("cursoasig");
                                Asignatura asig = new Asignatura(idasig, nombreasig, nombreabreasig, cursoasig, cicloasig);
                                String tipo_asistencia = datosfaltasasistencias.getJSONObject(i).getString("tipo_asistencia");
                                String fecha = datosfaltasasistencias.getJSONObject(i).getString("fecha");
                                int cantidad = datosfaltasasistencias.getJSONObject(i).getInt("cantidad");

                                FaltaAsistencia falta = new FaltaAsistencia(alu, asig, tipo_asistencia, fecha, cantidad);
                                faltas.add(falta);
                            }
                            break;
                        case RESULTADO_ERROR:
                            System.out.println(datos);
                            throw new ServidorPHPException("Error anotando la asistencia.");
                        case RESULTADO_ERROR_DESCONOCIDO:
                            System.out.println(datos);
                            throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                    }
                }
                else
                {
                    Utilidades.mostrarToastText(contexto, "Error obteniendo los datos del servidor.");
                }
            }
            else
            {
                Utilidades.mostrarToastText(contexto, "Error obteniendo los datos del servidor.");
            }
        }
        catch(InterruptedException | ExecutionException | IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }

        return faltas;
    }
}
