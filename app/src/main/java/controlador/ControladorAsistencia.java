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
import modelo.asistencia.Asistencia;
import modelo.asistencia.AsistenciaAsignaturaDia;
import modelo.asistencia.AsistenciaModificar;
import modelo.asistencia.AsistenciaTotalAsignatura;
import utilidades.Utilidades;

public class ControladorAsistencia
{
    private Context contexto;

    private final int RESULTADO_OK = 1;
    private final int RESULTADO_ERROR = 2;
    private final int RESULTADO_ERROR_DESCONOCIDO = 3;

    private final String TAG = "ControladorAsistencia";

    private static String urlAnotarAsistencia = Utilidades.urlservidor + "insertarAsistencia.php";
    private static String urlobtenerAsistenciasAlumnoAsignaturaDia = Utilidades.urlservidor + "obtenerAsistenciasAlumnoAsignaturaDia.php";
    private static String urlobtenerTotalAsistenciaAlumnoAsignatura = Utilidades.urlservidor + "obtenerTotalAsistenciaAlumnoAsignatura.php";
    private static String urlobtenerAsistenciasAlumnoAsignaturaFecha = Utilidades.urlservidor + "obtenerAsistenciasAlumnoAsignaturaFecha.php";
    private static String urlborrarAsistencia = Utilidades.urlservidor + "borrarAsistencia.php";
    private static String urleliminarTodasAsistencias = Utilidades.urlservidor + "eliminarTodasAsistencias.php";
    private static String urlmodificarAsistencia = Utilidades.urlservidor + "modificarAsistencia.php";
    private static String urljustificarAsistencia = Utilidades.urlservidor + "justificarAsistencia.php";
    private static String urlenviarEmailAsistencia = Utilidades.urlservidor + "enviarEmailAsistencia.php";

    public ControladorAsistencia(Context contexto)
    {
        this.contexto = contexto;
    }

    /**
     * Anota una asistena de un alumno en el servidor
     * @param token Token válido de un profesor
     * @param idalumno Alumno
     * @param tipoasistencia Tipo de asistencia
     * @param fecha Fecha de la asistencia
     * @param asignatura Asignatura de la asistencia
     * @param ciclo Ciclo de la asignatura
     * @param curso Curso de la asignatura
     * @return Verdadero si se ha anotado con exito, falso en caso contrario
     * @throws ServidorPHPException
     */
    public Boolean insertarAsistencia(String token, int idalumno, String tipoasistencia, String fecha, String asignatura, String ciclo, String curso) throws ServidorPHPException
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
            parametros.add(new Pair<>("nombreasignatura", asignatura));
            parametros.add(new Pair<>("cicloasignatura", ciclo));
            parametros.add(new Pair<>("cursoasignatura", curso));
            parametros.add(new Pair<>("tipoasistencia", tipoasistencia));
            parametros.add(new Pair<>("fechaasistencia", fecha));

            datos = parser.getJSONArrayFromUrl(urlAnotarAsistencia, parametros);

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
        catch(InterruptedException | ExecutionException | IOException | JSONException e)
        {
            throw new ServidorPHPException(e.toString());
        }

        return insertado;
    }

    /**
     * Obtiene todas las asistencias de los alumnos de una asignatura en un día
     * @param token Token válido de un profesor
     * @param idalumno Identificador del alumno
     * @param idasignatura Identificador de la asignatura
     * @param fecha Fecha para consultar las asistencias
     * @return Array con todas las asistencias de los alumnos matriculados en dicha asignatura en dicha fecha
     * @throws ServidorPHPException
     */
    public ArrayList<AsistenciaAsignaturaDia> calcularAsistenciaAlumnoAsignaturaDia(String token, int idalumno, int idasignatura, String fecha) throws ServidorPHPException
    {
        ArrayList<AsistenciaAsignaturaDia> devolver = new ArrayList<>();

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("idalumno", String.valueOf(idalumno)));
            parametros.add(new Pair<>("idasignatura", String.valueOf(idasignatura)));
            parametros.add(new Pair<>("fecha", fecha));

            datos = parser.getJSONArrayFromUrl(urlobtenerAsistenciasAlumnoAsignaturaDia, parametros);

            if( datos != null )
            {
                if( datos != null )
                {
                    int resultado = datos.getJSONObject(0).getInt("estado");
                    //System.out.println("EL RESULTADO ES " + resultado);

                    switch(resultado)
                    {
                        case RESULTADO_OK:
                            JSONArray datosasistencias = datos.getJSONObject(0).getJSONArray("mensaje");
                            for(int i = 0; i < datos.length(); i++)
                            {
                                String nombre = datosasistencias.getJSONObject(i).getString("nombre");
                                if( !nombre.equals("null") )
                                {
                                    String apellidos = datosasistencias.getJSONObject(i).getString("apellidos");
                                    String asignaturadevuelva = datosasistencias.getJSONObject(i).getString("nombreabreviado");
                                    String fechadevuelva = fecha;
                                    int totalasiste = datosasistencias.getJSONObject(i).getInt("totalasiste");
                                    int totalfalta = datosasistencias.getJSONObject(i).getInt("totalfalta");
                                    int totalfaltajustificada = datosasistencias.getJSONObject(i).getInt("totalfaltajustificada");
                                    int totalretraso = datosasistencias.getJSONObject(i).getInt("totalretraso");
                                    AsistenciaAsignaturaDia asis = new AsistenciaAsignaturaDia(nombre, apellidos, asignaturadevuelva, fechadevuelva, totalasiste, totalfalta, totalfaltajustificada, totalretraso);
                                    devolver.add(asis);
                                }
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

        return devolver;
    }

    /**
     * Obtiene el total hasta el momento de asistencias, faltas, faltas justificadas y retrasos de un alumno en una asignatura
     * @param token Token válido de un profesor
     * @param idasignatura Identificador de la asignatura
     * @param NOTAASISTENCIA Nota máxima que tendrá la asistencia
     * @param PESOASISTIDO Peso de las asistencias
     * @param PESOFALTA Peso de las faltas de asistencia
     * @param PESOFALTAJUSTIFICADA Peso de las faltas justificadas
     * @param PESORETRASO Peso de los retrasos
     * @return Array con los totales de asistencias calculados de los alumnos matriculados de la asignatura indicada
     * @throws ServidorPHPException
     */
    public ArrayList<AsistenciaTotalAsignatura> obtenerTotalAsistenciaAlumnoAsignatura(String token, int idasignatura, int NOTAASISTENCIA, double PESOASISTIDO, double PESOFALTA, double PESOFALTAJUSTIFICADA, double PESORETRASO) throws ServidorPHPException
    {
        ArrayList<AsistenciaTotalAsignatura> totales = new ArrayList<>();

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("idasignatura", String.valueOf(idasignatura)));

            datos = parser.getJSONArrayFromUrl(urlobtenerTotalAsistenciaAlumnoAsignatura, parametros);

            if( datos != null )
            {
                if( datos != null )
                {
                    int resultado = datos.getJSONObject(0).getInt("estado");
                    //System.out.println("EL RESULTADO ES " + resultado);

                    switch(resultado)
                    {
                        case RESULTADO_OK:
                            JSONArray datosasistencias = datos.getJSONObject(0).getJSONArray("mensaje");
                            for(int i = 0; i < datosasistencias.length(); i++)
                            {
                                String nombre = datosasistencias.getJSONObject(i).getString("nombre");
                                if( !nombre.equals("null") )
                                {
                                    String apellidos = datosasistencias.getJSONObject(i).getString("apellidos");
                                    String asignaturadevuelta = datosasistencias.getJSONObject(i).getString("nombreabreviado");
                                    int totalasiste = datosasistencias.getJSONObject(i).getInt("totalasiste");
                                    int totalfalta = datosasistencias.getJSONObject(i).getInt("totalfalta");
                                    int totalfaltajustificada = datosasistencias.getJSONObject(i).getInt("totalfaltajustificada");
                                    int totalretraso = datosasistencias.getJSONObject(i).getInt("totalretraso");
                                    int totalhoras = datosasistencias.getJSONObject(i).getInt("totalhoras");

                                    double porcentaje = (totalasiste*PESOASISTIDO) + (totalfalta*PESOFALTA) + (totalfaltajustificada*PESOFALTAJUSTIFICADA) + (totalretraso*PESORETRASO);
                                    double porcentajenota = (porcentaje/totalhoras)*100.0;
                                    double notaasistencia = NOTAASISTENCIA * (porcentajenota/100f);

                                    AsistenciaTotalAsignatura notatotal = new AsistenciaTotalAsignatura(nombre, apellidos, asignaturadevuelta, totalasiste, totalfalta, totalfaltajustificada, totalretraso, totalhoras, porcentajenota, notaasistencia);
                                    totales.add(notatotal);
                                }
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

        return totales;
    }

    /**
     * Obtiene todas las asistencias de un alumno en una asignatura en una fecha concreta
     * @param token token Token válido de un profesor
     * @param idalumno Identificador del alumno
     * @param idasignatura Identificador de la asignatura
     * @param fecha Fecha para consultar
     * @return Asistencias de ese alumno en esa asignatura en esa fecha
     * @throws ServidorPHPException
     */
    public ArrayList<Asistencia> obtenerAsistenciasAsignaturaAlumnoFecha(String token, int idalumno, int idasignatura, String fecha) throws ServidorPHPException
    {
        ArrayList<Asistencia> devolver = new ArrayList<>();

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("idalumno", String.valueOf(idalumno)));
            parametros.add(new Pair<>("idasignatura", String.valueOf(idasignatura)));
            parametros.add(new Pair<>("fecha", fecha));

            datos = parser.getJSONArrayFromUrl(urlobtenerAsistenciasAlumnoAsignaturaFecha, parametros);

            if( datos != null )
            {
                if( datos != null )
                {
                    int resultado = datos.getJSONObject(0).getInt("estado");
                    //System.out.println("EL RESULTADO ES " + resultado);

                    switch(resultado)
                    {
                        case RESULTADO_OK:
                            JSONArray datosasistencias = datos.getJSONObject(0).getJSONArray("mensaje");
                            for(int i = 0; i < datosasistencias.length(); i++)
                            {
                                JSONObject jsonasistencia = datosasistencias.getJSONObject(i);
                                int idalumnoa = datosasistencias.getJSONObject(i).getInt("idalumno");
                                String nombrealumno = datosasistencias.getJSONObject(i).getString("nombrealumno");
                                String apellidosalumno = datosasistencias.getJSONObject(i).getString("apellidosalumno");
                                Alumno alumnoasis = new Alumno(idalumnoa, nombrealumno, apellidosalumno);
                                String nombreabreviado = datosasistencias.getJSONObject(i).getString("nombreabreviado");
                                String ciclo = datosasistencias.getJSONObject(i).getString("ciclo");
                                String curso = datosasistencias.getJSONObject(i).getString("curso");
                                int idasistencia = datosasistencias.getJSONObject(i).getInt("idasistencia");
                                String tipoasistencia = datosasistencias.getJSONObject(i).getString("tipoasistencia");
                                String fechaasistencia = datosasistencias.getJSONObject(i).getString("fechaasistencia");

                                Asistencia asis = new Asistencia(idasistencia, alumnoasis, tipoasistencia, fecha, nombreabreviado, ciclo, curso);
                                devolver.add(asis);
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

        return devolver;
    }

    /**
     * Elimina una asistencia
     * @param token Token válido de un profesor
     * @param idasistencia Identificador de la asistencia a eliminar
     * @return Verdadero si se ha anotado con exito, falso en caso contrario
     * @throws ServidorPHPException
     */
    public Boolean eliminarAsistencia(String token, int idasistencia) throws ServidorPHPException
    {
        // OK PARA ITP
        Boolean eliminado = Boolean.FALSE;

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("idasistencia", String.valueOf(idasistencia)));

            datos = parser.getJSONArrayFromUrl(urlborrarAsistencia, parametros);

            if( datos != null )
            {
                int resultado = datos.getJSONObject(0).getInt("estado");
                //System.out.println("EL RESULTADO ES " + resultado);

                switch(resultado)
                {
                    case RESULTADO_OK:
                        eliminado = Boolean.TRUE;
                        break;
                    case RESULTADO_ERROR:
                        System.out.println(datos);
                        throw new ServidorPHPException("Error eliminando la asistencia.");
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

        return eliminado;
    }

    /**
     * Elimina todas las asistencias de la base de datos (MUY PELIGROSO)
     * @param token Token válido de un profesor
     * @return Verdadero si se ha anotado con exito, falso en caso contrario
     * @throws ServidorPHPException
     */
    public Boolean eliminarTodasAsistencias(String token) throws ServidorPHPException
    {
        // OK PARA ITP
        Boolean eliminado = Boolean.FALSE;

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));

            datos = parser.getJSONArrayFromUrl(urleliminarTodasAsistencias, parametros);

            if( datos != null )
            {
                int resultado = datos.getJSONObject(0).getInt("estado");

                switch(resultado)
                {
                    case RESULTADO_OK:
                        eliminado = Boolean.TRUE;
                        break;
                    case RESULTADO_ERROR:
                        System.out.println(datos);
                        throw new ServidorPHPException("Error eliminando las asistencias.");
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

        return eliminado;
    }

    /**
     * Modifica el valor de una asistencia
     * @param token Token válido de un profesor
     * @param idasistencia Identificador de la asistencia a modificiar
     * @param nuevovalor Nuevo valor de la asistencia. Puede ser ASISTENCIA, FALTA, FALTAJUSTIFICADA o RETRASO
     * @return Verdadero si se ha modificado con exito, falso en caso contrario
     * @throws ServidorPHPException
     */
    public Boolean modificarAsistencia(String token, int idasistencia, String nuevovalor) throws ServidorPHPException
    {
        // OK PARA ITP
        Boolean modificado = Boolean.FALSE;

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("idasistencia", String.valueOf(idasistencia)));
            parametros.add(new Pair<>("nuevovalor", nuevovalor));

            datos = parser.getJSONArrayFromUrl(urlmodificarAsistencia, parametros);

            if( datos != null )
            {
                int resultado = datos.getJSONObject(0).getInt("estado");

                switch(resultado)
                {
                    case RESULTADO_OK:
                        modificado = Boolean.TRUE;
                        break;
                    case RESULTADO_ERROR:
                        System.out.println(datos);
                        throw new ServidorPHPException("Error modificando el valor de la asistencia.");
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

        return modificado;
    }

    /**
     * Descripción: Justifica una asistencia por fecha en bloque
     * @param idalumno Identificador del alumno
     * @param idasignatura Identificador de la asignatura
     * @param fechafalta Fecha de la falta que se va a justificar
     * @return Si se ha modificado correctamente la asistencia o no
     * @throws ServidorPHPException
     */
    public Boolean justificarAsistencia(String token, int idalumno, int idasignatura,  String fechafalta) throws ServidorPHPException
    {
        // OK PARA ITP
        Boolean modificado = Boolean.FALSE;

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("idalumno", String.valueOf(idalumno)));
            parametros.add(new Pair<>("idasignatura", String.valueOf(idasignatura)));
            parametros.add(new Pair<>("fechafalta", fechafalta));

            datos = parser.getJSONArrayFromUrl(urljustificarAsistencia, parametros);

            if( datos != null )
            {
                int resultado = datos.getJSONObject(0).getInt("estado");

                switch(resultado)
                {
                    case RESULTADO_OK:
                        modificado = Boolean.TRUE;
                        break;
                    case RESULTADO_ERROR:
                        System.out.println(datos);
                        throw new ServidorPHPException("Error modificando el valor de la asistencia.");
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

        return modificado;
    }

    /**
     * Envía por correo electrónico un PDF con el resumen de la asistencia del alumno indicado
     * @param token Token válido de un profesor
     * @param idalumno Identificador del alumno al que se le consulta la asistencia
     * @param idasignatura Identificador de la asignatura
     * @param cursoescolar Curso escolar en el que nos encontramos
     * @param sistemaoperativo Sistema operativo desde el que se envía el email (Android o iOS)
     * @param rutaficheroservidor Nombre del fichero pdf que se generará
     * @param destinatario Dirección de email del destinatario del correo electrónico
     * @return Verdadero si se ha enviado el email correctamente, falso en caso contrario
     * @throws ServidorPHPException
     */
    public Boolean enviarEmailPDFAsistencia(String token, int idalumno, int idasignatura, String cursoescolar, String sistemaoperativo, String rutaficheroservidor, String destinatario) throws ServidorPHPException
    {
        // OK PARA ITP
        Boolean enviado = Boolean.FALSE;

        try
        {
            ArrayList<Pair<String, String>> parametros = new ArrayList<>();
            JSONParser parser = new JSONParser();
            JSONArray datos;

            parametros.add(new Pair<>("v", token));
            parametros.add(new Pair<>("idalumno", String.valueOf(idalumno)));
            parametros.add(new Pair<>("idasignatura", String.valueOf(idasignatura)));
            parametros.add(new Pair<>("cursoescolar", cursoescolar));
            parametros.add(new Pair<>("sistemaoperativo", sistemaoperativo));
            parametros.add(new Pair<>("rutaficheroservidor", rutaficheroservidor));
            parametros.add(new Pair<>("destinatario", destinatario));

            datos = parser.getJSONArrayFromUrl(urlenviarEmailAsistencia, parametros);

            if( datos != null )
            {
                int resultado = datos.getJSONObject(0).getInt("estado");

                switch(resultado)
                {
                    case RESULTADO_OK:
                        enviado = Boolean.TRUE;
                        break;
                    case RESULTADO_ERROR:
                        System.out.println(datos);
                        throw new ServidorPHPException("Error enviando el email.");
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

        return enviado;
    }
}
