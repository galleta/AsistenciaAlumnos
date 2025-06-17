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
import modelo.profesor.Profesor;
import utilidades.Utilidades;

public class ControladorProfesor
{
    private Context contexto;

    private final int RESULTADO_OK = 1;
    private final int RESULTADO_ERROR = 2;
    private final int RESULTADO_ERROR_DESCONOCIDO = 3;

    private final String TAG = "ControladorProfesor";

    private static String urlLoginProfesor = Utilidades.urlservidor + "loginProfesor.php";
    private static String urlModificarTokenProfesor = Utilidades.urlservidor + "modificarTokenProfesor.php";
    private static String urlObtenerProfesorPorEmail = Utilidades.urlservidor + "obtenerProfesorPorEmail.php";
    private static String urlObtenerTodosProfesores = Utilidades.urlservidor + "obtenerTodosProfesores.php";
    private static String urlAgregarProfesor = Utilidades.urlservidor + "insertarProfesor.php";
    private static String urlMatricularProfesorAsignatura = Utilidades.urlservidor + "matricularProfesorAsignatura.php";
    private static String urlEliminarProfesor = Utilidades.urlservidor + "eliminarProfesor.php";
    private static String urlModificarProfesor = Utilidades.urlservidor + "modificarProfesor.php";
    private static String urlQuitarAsignaturaProfesor = Utilidades.urlservidor + "quitarAsignaturaProfesor.php";
    private static String urlModificarPasswordProfesor = Utilidades.urlservidor + "modificarPasswordProfesor.php";

    public ControladorProfesor(Context contexto)
    {
        this.contexto = contexto;
    }

    /**
     * VistaLogin para los profesores
     * @param emailprofesor Email del profesor
     * @param password Contraseña del profesor
     * @return Verdadero si el profesor existe, falso en caso contrario
     * @throws ServidorPHPException
     */
    public Boolean loginProfesor(String emailprofesor, String password) throws ServidorPHPException
    {
        Boolean loguinok = Boolean.FALSE;

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            int resultado;
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("email", emailprofesor));
            parametros.add(new Pair<>("contrasena", password));

            datos = parser.getJSONArrayFromUrl(urlLoginProfesor, parametros);

            if( datos != null )
            {
                resultado = datos.getJSONObject(0).getInt("estado");
                //System.out.println("EL RESULTADO ES " + resultado);

                switch(resultado)
                {
                    case RESULTADO_OK:
                        loguinok = Boolean.TRUE;
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }

            }
            else
            {
                throw new ServidorPHPException("Error obteniendo los datos del servidor.");
            }
        }
        catch(InterruptedException | ExecutionException | IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }

        return loguinok;
    }

    /**
     * Modifica el token de un profesor
     * @param emailprofesor Email del profesor
     * @param nuevotoken Nuevo token del profesor
     * @return Verdadero si se ha modificado correctamente, falso en caso contrario
     * @throws ServidorPHPException
     */
    public Boolean modificarTokenProfesor(String emailprofesor, String nuevotoken) throws ServidorPHPException
    {
        Boolean devolver = Boolean.FALSE;

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            int resultado;
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("emailprofesor", emailprofesor));
            parametros.add(new Pair<>("nuevotoken", nuevotoken));

            datos = parser.getJSONArrayFromUrl(urlModificarTokenProfesor, parametros);

            if( datos != null )
            {
                resultado = datos.getJSONObject(0).getInt("estado");
                //System.out.println("EL RESULTADO ES " + resultado);

                switch(resultado)
                {
                    case RESULTADO_OK:
                        devolver = Boolean.TRUE;
                        break;
                    case RESULTADO_ERROR:
                        throw new ServidorPHPException("Error, datos incorrectos.");
                    case RESULTADO_ERROR_DESCONOCIDO:
                        throw new ServidorPHPException("Error obteniendo los datos del servidor.");
                }

            }
            else
            {
                throw new ServidorPHPException("Error obteniendo los datos del servidor.");
            }
        }
        catch(InterruptedException | ExecutionException | IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }

        return devolver;
    }

    /**
     * Obtiene los datos de un profesor sabiendo su email
     * @param token Token válido de un profesor
     * @param email Email del profesor
     * @return Datos del profesor
     * @throws ServidorPHPException
     */
    public Profesor obtenerProfesorPorEmail(String token, String email) throws ServidorPHPException
    {
        // OK PARA ITP
        Profesor a = new Profesor("", "", "", "");

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;
            int resultado;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("email", email));

            datos = parser.getJSONArrayFromUrl(urlObtenerProfesorPorEmail, parametros);

            if( datos != null )
            {
                resultado = datos.getJSONObject(0).getInt("estado");
                //System.out.println("EL RESULTADO ES " + resultado);

                switch(resultado)
                {
                    case RESULTADO_OK:
                        JSONObject mensaje = datos.getJSONObject(0).getJSONObject("mensaje");
                        //System.out.println("El mensaje es: " + mensaje);
                        a.setEmail(email);
                        a.setNombre(mensaje.getString("nombre"));
                        a.setTipo(mensaje.getString("tipo"));
                        String asignaturas = mensaje.getString("asignaturasSMR1");
                        asignaturas += "\n" + mensaje.getString("asignaturasSMR2");
                        asignaturas += "\n" + mensaje.getString("asignaturasDAM1");
                        asignaturas += "\n" + mensaje.getString("asignaturasDAM2");
                        a.setAsignaturas1SMR(mensaje.getString("asignaturasSMR1"));
                        a.setAsignaturas2SMR(mensaje.getString("asignaturasSMR2"));
                        a.setAsignaturas1DAM(mensaje.getString("asignaturasDAM1"));
                        a.setAsignaturas2DAM(mensaje.getString("asignaturasDAM2"));
                        a.setAsignaturasImpartidas(asignaturas);
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
     * Obtiene los datos de todos los profesores
     * @param token Token válido de un profesor
     * @return Array con todos los profesores
     * @throws ServidorPHPException
     */
    public ArrayList<Profesor> obtenerTodosProfesores(String token) throws ServidorPHPException
    {
        // OK PARA ITP
        ArrayList<Profesor> profesoresobtenidos = new ArrayList<>();

        try
        {
            //System.out.println("ME LLEGA EL TOKEN " + token);
            System.out.println(urlObtenerTodosProfesores);
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));

            datos = parser.getJSONArrayFromUrl(urlObtenerTodosProfesores, parametros);

            if( datos != null )
            {
                int resultado = datos.getJSONObject(0).getInt("estado");
                //System.out.println("EL RESULTADO ES " + resultado);

                switch(resultado)
                {
                    case RESULTADO_OK:
                        JSONArray mensaje = datos.getJSONObject(0).getJSONArray("mensaje");
                        for (int i = 0; i < mensaje.length(); i++)
                        {
                            JSONObject prof = mensaje.getJSONObject(i);
                            String nombre = prof.getString("nombre");
                            String email = prof.getString("email");
                            String tipo = prof.getString("tipo");
                            String asig1smr = prof.getString("asignaturasSMR1");
                            String asig2smr = prof.getString("asignaturasSMR2");
                            String asig1dam = prof.getString("asignaturasDAM1");
                            String asig2dam = prof.getString("asignaturasDAM2");
                            String asignaturas = asig1smr;
                            asignaturas += "\n" + asig2smr;
                            asignaturas += "\n" + asig1dam;
                            asignaturas += "\n" + asig2dam;

                            Profesor p = new Profesor(nombre, email, tipo, asignaturas);
                            profesoresobtenidos.add(p);
                        }
                        break;
                    case RESULTADO_ERROR:
                        System.out.println(datos);
                        throw new ServidorPHPException("Error obteniendo los profesores.");
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

        return profesoresobtenidos;
    }

    /**
     * Inserta un profesor en el sistema
     * @param token Token válido de un profesor
     * @param nombre Nombre del profesor
     * @param email Email del profesor
     * @param password Contraseña del profesor
     * @param tipo Tipo de profesor
     * @return Verdadero si se ha agregado correctamente, falso en caso contrario
     * @throws ServidorPHPException
     */
    public Boolean insertarProfesor(String token, String nombre, String email, String password, String tipo, String tokenprofesor) throws ServidorPHPException
    {
        // OK PARA ITP
        Boolean insertado = Boolean.FALSE;

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("nombre", nombre));
            parametros.add(new Pair<>("email", email));
            parametros.add(new Pair<>("contrasena", password));
            parametros.add(new Pair<>("tipo", tipo));
            parametros.add(new Pair<>("token", tokenprofesor));

            datos = parser.getJSONArrayFromUrl(urlAgregarProfesor, parametros);

            //System.out.println(datos);

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
                        throw new ServidorPHPException("Error dando de alta al profesor.");
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
     * Matricula a un profesor como profesor de una asignatura
     * @param token Token válido de un profesor
     * @param email Email del profesor
     * @param ciclo Ciclo de la asignatura
     * @param curso Curso de la asignatura
     * @param asignatura Asignatura
     * @return Verdadero si se ha agregado correctamente, falso en caso contrario
     * @throws ServidorPHPException
     */
    public Boolean matricularProfesorAsignatura(String token, String email, String ciclo, String curso, String asignatura) throws ServidorPHPException
    {
        Boolean insertado = Boolean.FALSE;

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("email", email));
            parametros.add(new Pair<>("ciclo", ciclo));
            parametros.add(new Pair<>("curso", curso));
            parametros.add(new Pair<>("asignatura", asignatura));

            datos = parser.getJSONArrayFromUrl(urlMatricularProfesorAsignatura, parametros);

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
                        throw new ServidorPHPException("Error matriculando al profesor.");
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
     * Elimina un profesor del sistema y los datos de las asignaturas que imparte
     * @param token Token válido de un profesor
     * @param email Email del profesor a eliminar
     * @return Verdadero si se ha eliminado correctamente, falso en caso contrario
     * @throws ServidorPHPException
     */
    public Boolean eliminarProfesor(String token, String email) throws ServidorPHPException
    {
        // OK PARA ITP
        Boolean insertado = Boolean.FALSE;

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("email", email));

            datos = parser.getJSONArrayFromUrl(urlEliminarProfesor, parametros);

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
                        throw new ServidorPHPException("Error eliminando al profesor.");
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
     * Modifica los datos de un profesor
     * @param token Token válido de un profesor
     * @param email Email del profesor
     * @param nombre Nombre nuevo del profesor
     * @param tipo Tipo nuevo del profesor
     * @return Verdadero si se han modificado los datos correctamente, falso en caso contrario
     * @throws ServidorPHPException
     */
    public Boolean modificarProfesor(String token, String email, String nombre, String tipo) throws ServidorPHPException
    {
        // OK PARA ITP
        Boolean insertado = Boolean.FALSE;

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("nemail", email));
            parametros.add(new Pair<>("nnombre", nombre));
            parametros.add(new Pair<>("ntipo", tipo));

            datos = parser.getJSONArrayFromUrl(urlModificarProfesor, parametros);

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
                        throw new ServidorPHPException("Error modificando al profesor.");
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
     * Elimina una asignatura que imparte un profesor
     * @param token Token válido de un profesor
     * @param email Email del profesor
     * @param ciclo Ciclo de la asignatura
     * @param curso Curso de la asignatura
     * @param asignatura Nombre abreviado de la asignatura
     * @return Verdadero si se eliminado correctamente, falso en caso contrario
     * @throws ServidorPHPException
     */
    public Boolean quitarAsignaturaProfesor(String token, String email, String ciclo, String curso, String asignatura) throws ServidorPHPException
    {
        // OK PARA ITP
        Boolean insertado = Boolean.FALSE;

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("email", email));
            parametros.add(new Pair<>("ciclo", ciclo));
            parametros.add(new Pair<>("curso", curso));
            parametros.add(new Pair<>("asignatura", asignatura));

            datos = parser.getJSONArrayFromUrl(urlQuitarAsignaturaProfesor, parametros);

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
                        throw new ServidorPHPException("Error eliminando la asignatura al profesor.");
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
     * Modifica la contraseña de un profesor
     * @param token Token válido de un profesor
     * @param emailprofesor Email del profesor
     * @param nuevopassword Contraseña nueva del profesor
     * @param nuevotokenprofesor Nuevo token para el profesor
     * @return Verdadero si se ha modificado con éxito, falso en caso contrario
     * @throws ServidorPHPException
     */
    public Boolean modificarPasswordProfesor(String token, String emailprofesor, String nuevopassword, String nuevotokenprofesor) throws ServidorPHPException
    {
        // OK PARA ITP
        Boolean insertado = Boolean.FALSE;

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("email", emailprofesor));
            parametros.add(new Pair<>("npass", nuevopassword));
            parametros.add(new Pair<>("ntoken", nuevotokenprofesor));

            datos = parser.getJSONArrayFromUrl(urlModificarPasswordProfesor, parametros);

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
                        throw new ServidorPHPException("Error modificando la contraseña del profesor.");
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
        catch(Exception /*InterruptedException | ExecutionException | IOException | JSONException*/ e)
        {
            throw new ServidorPHPException(e.toString());
        }

        return insertado;
    }

    /**
     * Resetea la contraseña de un profesor a la contraseña por defecto
     * @param token Token válido de un profesor
     * @param emailprofesor Email del profesor
     * @param nuevopassword Contraseña nueva del profesor
     * @param nuevotokenprofesor Nuevo token para el profesor
     * @return Verdadero si se ha reseteado correctamente la contraseña, falso en caso contrario
     * @throws ServidorPHPException
     */
    public Boolean resetearPasswordProfesor(String token, String emailprofesor, String nuevopassword, String nuevotokenprofesor) throws ServidorPHPException
    {
        return modificarPasswordProfesor(token, emailprofesor, nuevopassword, nuevotokenprofesor);
    }
}
