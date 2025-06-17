package controlador;

import android.content.Context;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import internet.JSONParser;
import internet.ServidorPHPException;
import modelo.alumno.Alumno;
import modelo.asignatura.Asignatura;
import utilidades.Utilidades;

public class ControladorAsignatura
{
    private Context contexto;

    private final int RESULTADO_OK = 1;
    private final int RESULTADO_ERROR = 2;
    private final int RESULTADO_ERROR_DESCONOCIDO = 3;

    private final String TAG = "ControladorProfesor";

    private static String urlObtenerAsignaturasMatriculadasAlumno = Utilidades.urlservidor + "obtenerTodasAsignaturasMatriculadasAlumno.php";
    private static String urlEliminarAsignaturaMatriculadaAlumno = Utilidades.urlservidor + "quitarAsignaturaAlumno.php";
    private static String urlObtenerTodasAsignaturasProfesorCicloCurso = Utilidades.urlservidor + "obtenerTodasAsignaturasProfesorCicloCurso.php";
    private static String urlObtenerAsignatura = Utilidades.urlservidor + "obtenerAsignatura.php";

    public ControladorAsignatura(Context contexto)
    {
        this.contexto = contexto;
    }

    /**
     * Obtiene las asignaturas en las que está matriculado un alumno
     * @param token Token válido de un profesor
     * @param alu Alumno
     * @return Array con todas las asignaturas en las que el alumno está matriculado
     * @throws ServidorPHPException
     */
    public ArrayList<Asignatura> obtenerTodasAsignaturasMatriculadasAlumno(String token, Alumno alu) throws ServidorPHPException
    {
        // OK PARA ITP
        ArrayList<Asignatura> asignaturasmatriculadas = new ArrayList<>();

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("idalumno", String.valueOf(alu.getId())));

            datos = parser.getJSONArrayFromUrl(urlObtenerAsignaturasMatriculadasAlumno, parametros);

            if( datos != null )
            {
                int resultado = datos.getJSONObject(0).getInt("estado");
                //System.out.println("EL RESULTADO ES " + resultado);

                switch(resultado)
                {
                    case RESULTADO_OK:
                        JSONArray asignaturas = datos.getJSONObject(0).getJSONArray("mensaje");
                        for(int i = 0; i < asignaturas.length(); i++)
                        {
                            int id = asignaturas.getJSONObject(i).getInt("id");
                            String nombrecompleto = asignaturas.getJSONObject(i).getString("nombre");
                            String nombreabreviado = asignaturas.getJSONObject(i).getString("nombreabreviado");
                            String curso = asignaturas.getJSONObject(i).getString("curso");
                            String ciclo = asignaturas.getJSONObject(i).getString("ciclo");


                            Asignatura a = new Asignatura(id, nombrecompleto, nombreabreviado, curso, ciclo);
                            asignaturasmatriculadas.add(a);
                        }
                        break;
                    case RESULTADO_ERROR:
                        System.out.println(datos);
                        throw new ServidorPHPException("Error obteniendo las asignaturas.");
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
        catch(InterruptedException | ExecutionException | IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }

        return asignaturasmatriculadas;
    }

    /**
     * Obtiene los datos de una asignatura
     * @param token Token válido de un profesor
     * @param nombreabreviado Nombre abreviado de la asignatura
     * @param ciclo Ciclo de la asignatura
     * @param curso Curso de la asignatura
     * @return Asignatura completa
     * @throws ServidorPHPException
     */
    public Asignatura obtenerAsignatura(String token, String nombreabreviado, String ciclo, String curso) throws ServidorPHPException
    {
        // OK PARA ITP
        Asignatura asignatura = null;

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("nombreabreviado", nombreabreviado));
            parametros.add(new Pair<>("ciclo", ciclo));
            parametros.add(new Pair<>("curso", curso));

            datos = parser.getJSONArrayFromUrl(urlObtenerAsignatura, parametros);

            if( datos != null )
            {
                int resultado = datos.getJSONObject(0).getInt("estado");
                //System.out.println("EL RESULTADO ES " + resultado);

                switch(resultado)
                {
                    case RESULTADO_OK:
                        JSONObject datosasignatura = datos.getJSONObject(0).getJSONArray("mensaje").getJSONObject(0);
                        int id = datosasignatura.getInt("id");
                        String nombre = datosasignatura.getString("nombre");
                        String nombreabre = datosasignatura.getString("nombreabreviado");
                        asignatura = new Asignatura(id, nombre, nombreabre, ciclo, curso);
                        break;
                    case RESULTADO_ERROR:
                        System.out.println(datos);
                        throw new ServidorPHPException("Error obteniendo las asignaturas.");
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
        catch(InterruptedException | ExecutionException | IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }

        return asignatura;
    }

    /**
     * Desmatricula a un alumna de una asignatura
     * @param token Token válido de un profesor
     * @param idalumno Identificador del alumno
     * @param idasignatura Identificador de la asignatura
     * @return Verdadero si el alumno se ha desmatriculado correctamente, falso en caso contrario
     * @throws ServidorPHPException
     */
    public Boolean quitarAsignaturaMatriculadaAlumno(String token, int idalumno, int idasignatura) throws ServidorPHPException
    {
        Boolean insertado = Boolean.FALSE;

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("idalumno", String.valueOf(idalumno)));
            parametros.add(new Pair<>("idasignatura", String.valueOf(idasignatura)));

            datos = parser.getJSONArrayFromUrl(urlEliminarAsignaturaMatriculadaAlumno, parametros);

            if( datos != null )
            {
                int resultado = datos.getJSONObject(0).getInt("estado");
                //System.out.println("EL RESULTADO ES " + resultado);

                switch(resultado)
                {
                    case RESULTADO_OK:
                        insertado = Boolean.TRUE;
                        break;
                    case RESULTADO_ERROR:
                        System.out.println(datos);
                        throw new ServidorPHPException("Error quitando la asignatura del alumno.");
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
        catch(InterruptedException | ExecutionException | IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }

        return insertado;
    }

    /**
     * Obtiene las asignaturas que imparte un profesor en un curso de un ciclo
     * @param token Token válido de un profesor
     * @param email Email del profesor
     * @param ciclo Ciclo
     * @param curso Curso
     * @return Array con todas las asignaturas que imparte el profesor en ese curso del ciclo
     * @throws ServidorPHPException
     */
    public ArrayList<Asignatura> obtenerTodasAsignaturasProfesorCicloCurso(String token, String email, String ciclo, String curso) throws ServidorPHPException
    {
        // OK PARA ITP
        ArrayList<Asignatura> asignaturas = new ArrayList<>();

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("email", email));
            parametros.add(new Pair<>("ciclo", ciclo));
            parametros.add(new Pair<>("curso", curso));

            datos = parser.getJSONArrayFromUrl(urlObtenerTodasAsignaturasProfesorCicloCurso, parametros);

            if( datos != null )
            {
                int resultado = datos.getJSONObject(0).getInt("estado");
                //System.out.println("EL RESULTADO ES " + resultado);

                switch(resultado)
                {
                    case RESULTADO_OK:
                        JSONArray datosasignaturas = datos.getJSONObject(0).getJSONArray("mensaje");
                        for(int i = 0; i < datosasignaturas.length(); i++)
                        {
                            int id = datosasignaturas.getJSONObject(i).getInt("id");
                            String nombrecompleto = datosasignaturas.getJSONObject(i).getString("nombre");
                            String nombreabreviado = datosasignaturas.getJSONObject(i).getString("nombreabreviado");
                            String curso2 = datosasignaturas.getJSONObject(i).getString("curso");
                            String ciclo2 = datosasignaturas.getJSONObject(i).getString("ciclo");

                            Asignatura a = new Asignatura(id, nombrecompleto, nombreabreviado, curso2, ciclo2);
                            asignaturas.add(a);
                        }
                        break;
                    case RESULTADO_ERROR:
                        System.out.println(datos);
                        throw new ServidorPHPException("Error obteniendo las asignaturas.");
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
        catch(InterruptedException | ExecutionException | IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }

        return asignaturas;
    }
}
