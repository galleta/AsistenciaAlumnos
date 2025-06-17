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
import modelo.alumno.AlumnoMatriculado;
import utilidades.Utilidades;

public class ControladorAlumno
{
    private Context contexto;

    private final int RESULTADO_OK = 1;
    private final int RESULTADO_ERROR = 2;
    private final int RESULTADO_ERROR_DESCONOCIDO = 3;

    private final String TAG = "ControladorProfesor";

    private static String urlObtenerAlumnosMatriculaos = Utilidades.urlservidor + "obtenerAlumnosMatriculadosCicloCurso.php";
    private static String urlAgregarAlumno = Utilidades.urlservidor + "insertarAlumno.php";
    private static String urlObtenerAlumnoNombreApellidos = Utilidades.urlservidor + "obtenerAlumnoPorNombreApellidos.php";
    private static String urlMatricularAlumno = Utilidades.urlservidor + "matricularAlumno.php";
    private static String urlMatricularAlumnoAsignatura = Utilidades.urlservidor + "matricularAlumnoAsignatura.php";
    private static String urlBorrarAlumnoYAsistencia = Utilidades.urlservidor + "eliminarAlumnoAsistencias.php";
    private static String urlModificarAlumno = Utilidades.urlservidor + "modificarAlumno.php";
    private static String urlDesmatricularAlumnoCicloCurso = Utilidades.urlservidor + "desmatricularAlumnoCicloCurso.php";
    private static String urlObtenerAlumnosMatriculadosAsignatura = Utilidades.urlservidor + "obtenerAlumnosMatriculadosAsignatura.php";

    public ControladorAlumno(Context contexto)
    {
        this.contexto = contexto;
    }

    /**
     * Obtiene los alumnos de un curso de un ciclo
     * @param token Token válido de un profesor
     * @param ciclo Ciclo para consultar los alumnos
     * @param curso Curso del ciclo
     * @return Lista con todos los alumnos de ese curso del ciclo
     * @throws ServidorPHPException
     */
    public ArrayList<AlumnoMatriculado> obtenerAlumnosMatriculadosPorCicloCurso(String token, String ciclo, String curso) throws ServidorPHPException
    {
        ArrayList<AlumnoMatriculado> resultado = new ArrayList<>();

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;
            int resultadoobtenido;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("ciclo", ciclo));
            parametros.add(new Pair<>("curso", curso));

            datos = parser.getJSONArrayFromUrl(urlObtenerAlumnosMatriculaos, parametros);
            //System.out.println(datos);

            if( datos != null )
            {
                resultadoobtenido = datos.getJSONObject(0).getInt("estado");
                //System.out.println("EL RESULTADO ES " + resultadoobtenido);

                switch(resultadoobtenido)
                {
                    case RESULTADO_OK:
                        JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");
                        for(int i = 0; i < mensaje.length(); i++)
                        {
                            int id = mensaje.getJSONObject(i).getInt("id");
                            String n = mensaje.getJSONObject(i).getString("nombre");
                            String ap = mensaje.getJSONObject(i).getString("apellidos");
                            AlumnoMatriculado alu = new AlumnoMatriculado(id, n, ap, ciclo, curso);
                            resultado.add(alu);
                        }
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
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

        return resultado;
    }

    /**
     * Inserta un alumno nuevo
     * @param token Token válido de un profesor
     * @param alumno Alumno a insertar
     * @return Verdadero si se ha podido insertar con éxito, falso en caso contrario
     * @throws ServidorPHPException
     */
    public Boolean insertarAlumno(String token, Alumno alumno) throws ServidorPHPException
    {
        Boolean insertado = Boolean.FALSE;

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            int resultado;
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("nombre", alumno.getNombre()));
            parametros.add(new Pair<>("apellidos", alumno.getApellidos()));

            datos = parser.getJSONArrayFromUrl(urlAgregarAlumno, parametros);

            if( datos != null )
            {
                resultado = datos.getJSONObject(0).getInt("estado");
                //System.out.println("EL RESULTADO ES " + resultado);

                switch(resultado)
                {
                    case RESULTADO_OK:
                        insertado = Boolean.TRUE;
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
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
     * Obtiene los datos de un alumno por su nombre y apellidos
     * @param token Token válido de un profesor
     * @param nombre Nombre del alumno
     * @param apellidos Apellidos del alumno
     * @return Datos del alumno
     * @throws ServidorPHPException
     */
    public Alumno obtenerAlumnoNombreApellidos(String token, String nombre, String apellidos) throws ServidorPHPException
    {
        Alumno a = new Alumno(-1, "", "");

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;
            int resultado;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("nombre", nombre));
            parametros.add(new Pair<>("apellidos", apellidos));

            datos = parser.getJSONArrayFromUrl(urlObtenerAlumnoNombreApellidos, parametros);

            if( datos != null )
            {
                resultado = datos.getJSONObject(0).getInt("estado");
                //System.out.println("EL RESULTADO ES " + resultado);
                JSONObject mensaje = (JSONObject) datos.getJSONObject(0).getJSONArray("mensaje").get(0);

                switch(resultado)
                {
                    case RESULTADO_OK:
                        a.setId(mensaje.getInt("id"));
                        a.setNombre(mensaje.getString("nombre"));
                        a.setApellidos(mensaje.getString("apellidos"));
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
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

        return a;
    }

    /**
     * Matricula un alumno en un curso de un ciclo
     * @param token Token válido de un profesor
     * @param alumno Alumno a matricular
     * @param ciclo Ciclo en el que se matricula
     * @param curso Curso en el que se matricula
     * @return
     * @throws ServidorPHPException
     */
    public Boolean matricularAlumnoEnCicloCurso(String token, Alumno alumno, String ciclo, String curso) throws ServidorPHPException
    {
        Boolean insertado = Boolean.FALSE;

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            int resultado;
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("nombre", alumno.getNombre()));
            parametros.add(new Pair<>("apellidos", alumno.getApellidos()));
            parametros.add(new Pair<>("ciclo", ciclo));
            parametros.add(new Pair<>("curso", curso));

            datos = parser.getJSONArrayFromUrl(urlMatricularAlumno, parametros);

            if( datos != null )
            {
                resultado = datos.getJSONObject(0).getInt("estado");
                //System.out.println("EL RESULTADO ES " + resultado);

                switch(resultado)
                {
                    case RESULTADO_OK:
                        insertado = Boolean.TRUE;
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
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
     * Matricula a un alumno en una asignatura
     * @param token Token válido de un profesor
     * @param alumno Alumno
     * @param asignatura Asignatura a matricular
     * @param ciclo Ciclo de la asignatura
     * @param curso Curso de la asignatura
     * @return Verdadero si se ha matriculado correctamente, falso en caso contrario
     * @throws ServidorPHPException
     */
    public Boolean matricularAlumnoAsignatura(String token, Alumno alumno, String asignatura, String ciclo, String curso) throws ServidorPHPException
    {
        // OK PARA ITP
        Boolean insertado = Boolean.TRUE;

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            int resultado;
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("nombre", alumno.getNombre()));
            parametros.add(new Pair<>("apellidos", alumno.getApellidos()));
            parametros.add(new Pair<>("asignatura", asignatura));
            parametros.add(new Pair<>("curso", curso));
            parametros.add(new Pair<>("ciclo", ciclo));

            datos = parser.getJSONArrayFromUrl(urlMatricularAlumnoAsignatura, parametros);

            if( datos != null )
            {
                resultado = datos.getJSONObject(0).getInt("estado");
                //System.out.println("EL RESULTADO ES " + resultado);

                switch(resultado)
                {
                    case RESULTADO_OK:
                        insertado = Boolean.TRUE;
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
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
     * Borra un alumno y todas sus asistencias
     * @param token Token válido de un profesor
     * @param id Identificador del alumno
     * @return Verdadero si se ha borrado con exito, falso en caso contrario
     * @throws ServidorPHPException
     */
    public Boolean borrarAlumnoConAsistencias(String token, int id) throws ServidorPHPException
    {
        // OK PARA ITP
        Boolean borrado = Boolean.FALSE;
        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;
            int resultado;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("id", String.valueOf(id)));

            datos = parser.getJSONArrayFromUrl(urlBorrarAlumnoYAsistencia, parametros);

            if( datos != null )
            {
                resultado = datos.getJSONObject(0).getInt("estado");
                //System.out.println("EL RESULTADO ES " + resultado);

                switch(resultado)
                {
                    case RESULTADO_OK:
                        borrado = Boolean.TRUE;
                        break;
                    case RESULTADO_ERROR:
                        System.out.println("DATOS " + datos);
                        throw new ServidorPHPException("Error borrando el alumno.");
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

        return borrado;
    }

    /**
     * Modifica un alumno
     * @param token Token válido de un profesor
     * @param id_alumno Alumno a modificar
     * @param nombrenuevo Nombre nuevo del alumno
     * @param apellidosnuevos Apellidos nuevos del alumno
     * @return Verdadero si se ha modificado con exito, falso en caso contrario
     * @throws ServidorPHPException
     */
    public Boolean modificarAlumno(String token, int id_alumno, String nombrenuevo, String apellidosnuevos) throws ServidorPHPException
    {
        // OK PARA ITP
        Boolean insertado = Boolean.FALSE;

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("id", String.valueOf(id_alumno)));
            parametros.add(new Pair<>("nnombre", nombrenuevo));
            parametros.add(new Pair<>("napellidos", apellidosnuevos));

            datos = parser.getJSONArrayFromUrl(urlModificarAlumno, parametros);

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
                        throw new ServidorPHPException("Error modificando los datos del alumno.");
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
     * Desmatricula a un alumno de un ciclo y un curso. Desmatricula al alumno de las asignaturas de ese curso del ciclo y borra sus asistencias de esas asignaturas.
     * @param token Token válido de un profesor
     * @param idalumno Identificador del alumno
     * @param ciclo Ciclo para desmatricular
     * @param curso Curso para desmatricular
     * @return Verdadero si se ha desmatriculado correctamente, falso en caso contrario
     * @throws ServidorPHPException
     */
    public Boolean desmatricularAlumnoCicloCurso(String token, int idalumno, String ciclo, String curso) throws ServidorPHPException
    {
        // OK PARA ITP
        Boolean insertado = Boolean.FALSE;

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("idalumno", String.valueOf(idalumno)));
            parametros.add(new Pair<>("ciclo", ciclo));
            parametros.add(new Pair<>("curso", curso));

            datos = parser.getJSONArrayFromUrl(urlDesmatricularAlumnoCicloCurso, parametros);

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
                        throw new ServidorPHPException("Error desmatriculando al alumno.");
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
     * Obtiene una lista de los alumnos matriculados en una asignatura
     * @param token Token válido de un profesor
     * @param asignatura Asignatura a comprobar
     * @param curso Curso de la asignatura
     * @param ciclo Ciclo de la asignatura
     * @return Array con todos los alumnos matriculados en la asignatura
     * @throws ServidorPHPException
     */
    public ArrayList<Alumno> obtenerAlumnosMatriculadosAsignatura(String token, String asignatura, String curso, String ciclo) throws ServidorPHPException
    {
        // OK PARA ITP
        ArrayList<Alumno> alumnos = new ArrayList<>();

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("asignatura", asignatura));
            parametros.add(new Pair<>("ciclo", ciclo));
            parametros.add(new Pair<>("curso", curso));

            datos = parser.getJSONArrayFromUrl(urlObtenerAlumnosMatriculadosAsignatura, parametros);

            if( datos != null )
            {
                int resultado = datos.getJSONObject(0).getInt("estado");
                //System.out.println("EL RESULTADO ES " + resultado);
                JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");

                switch(resultado)
                {
                    case RESULTADO_OK:
                        for(int i = 0; i < mensaje.length(); i++)
                        {
                            int idalumno = mensaje.getJSONObject(i).getInt("id");
                            String nombrealumno = mensaje.getJSONObject(i).getString("nombre");
                            String apellidosalumno = mensaje.getJSONObject(i).getString("apellidos");

                            Alumno alum = new Alumno(idalumno, nombrealumno, apellidosalumno);
                            alumnos.add(alum);
                        }
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
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

        return alumnos;
    }

}
