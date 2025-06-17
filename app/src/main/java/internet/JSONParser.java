package internet;

/**
 * Created by francis on 21/09/16.
 */

import android.os.AsyncTask;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Esta clase implementa la traducción JSON a un servidor web
 *
 * Uso de POST y GET en Android: https://www.numetriclabz.com/android-post-and-get-request-using-httpurlconnection/
 */
public class JSONParser
{
    public static final String TAG = "JSONParser";

    /**
     * Constructor de la clase
     */
    public JSONParser() {}

    /**
     * Conecta con el servidor y devuelve un JSONArray con los datos obtenidos
     * @param url URL del servidor
     * @param parametros Parámetros de la consulta
     * @return JSONArray con los resultados de la consulta al servidor
     */
    public JSONArray getJSONArrayFromUrl(String url, ArrayList<Pair<String, String>> parametros) throws IOException, ExecutionException, InterruptedException
    {
        return new HttpPostJSONArrayClass(url, parametros).execute().get();
    }

    /**
     * Conecta con el servidor y devuelve un JSONObject con los datos obtenidos
     * @param url URL del servidor
     * @param parametros Parámetros de la consulta
     * @return JSONArray con los resultados de la consulta al servidor
     */
    public JSONObject getJSONObjectFromUrl(String url, ArrayList<Pair<String, String>> parametros) throws IOException, ExecutionException, InterruptedException
    {
        return new HttpPostJSONObjectClass(url, parametros).execute().get();
    }

    /**
     * Esta clase permite conectar vía HTTP POST con una url y devuelve el resultado en un JSONArray
     */
    private class HttpPostJSONArrayClass extends AsyncTask<String, String, JSONArray>
    {
        private String urlString;
        private ArrayList<Pair<String, String>> parametros;

        public HttpPostJSONArrayClass(String urlString, ArrayList<Pair<String, String>> parametros)
        {
            this.urlString = urlString;
            this.parametros = parametros;
        }

        protected void onPreExecute(){}

        @Override
        protected JSONArray doInBackground(String... params)
        {
            StringBuilder responseOutput = new StringBuilder();
            JSONArray jsonArr = null;

            try
            {
                // Obtengo los parámetros para la URL en caso de que haya
                String parametrosfinal = "";
                if( parametros != null )
                {
                    for(int i = 0; i < parametros.size(); i++)
                    {
                        parametrosfinal += (parametros.get(i).first + "=" + parametros.get(i).second);
                        // El símbolo & sirve para poner más de un parámetro
                        if( i < parametros.size()-1)
                            parametrosfinal += "&";
                    }
                }

                //System.out.println(this.urlString + "?" + parametrosfinal);

                // Conecto vía HTTP POST
                URL url = new URL(this.urlString);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                connection.setDoOutput(true);
                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(parametrosfinal);
                dStream.flush();
                dStream.close();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";

                while((line = br.readLine()) != null )
                {
                    responseOutput.append(line);
                }
                br.close();

                // RECOGEMOS LOS DATOS DEVUELTOS POR EL POST Y CONVERTIDOS A STRING Y LOS DEVOLVEMOS COMO UN OBJETO JSONArray
                jsonArr = new JSONArray(responseOutput.toString());
            }
            catch (IOException | JSONException e)
            {
                System.out.println(TAG + " Error obteniendo los datos: " + e.toString());
            }

            return jsonArr;
        }
    }

    /**
     * Esta clase permite conectar vía HTTP POST con una url y devuelve el resultado en un JSONObject
     */
    private class HttpPostJSONObjectClass extends AsyncTask<String, String, JSONObject>
    {
        private String urlString;
        private ArrayList<Pair<String, String>> parametros;

        public HttpPostJSONObjectClass(String urlString, ArrayList<Pair<String, String>> parametros)
        {
            this.urlString = urlString;
            this.parametros = parametros;
        }

        protected void onPreExecute(){}

        @Override
        protected JSONObject doInBackground(String... params)
        {
            StringBuilder responseOutput = new StringBuilder();
            JSONArray jsonArr = null;
            JSONObject devolver = null;

            try
            {
                // Obtengo los parámetros para la URL en caso de que haya
                String parametrosfinal = "";
                if( parametros != null )
                {
                    for(int i = 0; i < parametros.size(); i++)
                    {
                        parametrosfinal += (parametros.get(i).first + "=" + parametros.get(i).second);
                        // El símbolo & sirve para poner más de un parámetro
                        if( i < parametros.size()-1)
                            parametrosfinal += "&";
                    }
                }

                // Conecto vía HTTP POST
                URL url = new URL(this.urlString);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                connection.setDoOutput(true);
                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(parametrosfinal);
                dStream.flush();
                dStream.close();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";

                while((line = br.readLine()) != null )
                {
                    responseOutput.append(line);
                }
                br.close();

                // RECOGEMOS LOS DATOS DEVUELTOS POR EL POST Y CONVERTIDOS A STRING Y LOS DEVOLVEMOS COMO UN OBJETO JSONArray
                jsonArr = new JSONArray(responseOutput.toString());
                devolver = jsonArr.getJSONObject(0);
            }
            catch (IOException | JSONException e)
            {
                System.out.println(TAG + " Error obteniendo los datos: " + e.toString());
            }

            return devolver;
        }
    }

}



