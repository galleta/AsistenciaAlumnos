package asistenciaalumnos.asistenciaalumnos;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import asistenciaalumnos.asistenciaalumnos.dialogos.DialogoConfirmacionBorrarTodasAsistencias;
import calendario.CalendarView;
import controlador.ControladorProfesor;
import modelo.profesor.Profesor;
import utilidades.Utilidades;

public class VistaPantallaPrincipal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener
{
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private ImageButton bSalir;
    private View cabecera;
    private CalendarView cv;
    private TextView tNombreUsuarioCabecera, tUsuarioConectado;
    private Context contexto;
    private ControladorProfesor controladorp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setTheme(R.style.RedTheme);

        // ***** Obtengo los recursos de la aplicación *****
        toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        cv = ((CalendarView)findViewById(R.id.calendar_view));
        bSalir = ((ImageButton)findViewById(R.id.bSalir));
        tUsuarioConectado = ((TextView)findViewById(R.id.tUsuarioConectado));
        cabecera = navigationView.getHeaderView(0);
        tNombreUsuarioCabecera = (TextView)cabecera.findViewById(R.id.tNombreUsuarioCabecera);
        // *************************************************

        contexto = this;
        controladorp = new ControladorProfesor(this);
        setSupportActionBar(toolbar);

        // Obtengo los datos que están guardados del profesor
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(
                        this);

        /*SharedPreferences.Editor editor = prefs.edit();
        editor.putString("token", "3959910AA55E2DA373180EC82994C1BEC5C080C3");
        editor.commit();*/

        String email = prefs.getString("usuario", "");
        String pass = prefs.getString("password", "");
        Boolean recordar = prefs.getBoolean("recordarme", Boolean.FALSE);
        String token = prefs.getString("token", "");

        // Compruebo si se ha cambiado la contraseña desde el último logueo del profesor
        try
        {
            // Si la contraseña es la misma que hay en el servidor almacenada
            // el profesor no ha cambiado la contraseña.
            // Entra normal
            //System.out.println("********** EN EL MAIN ME LLEGA -> " + email + " Y " + pass);
            Boolean logueado = controladorp.loginProfesor(email, pass);
            if( logueado || (logueado && !recordar) )
            {
                String dateInString = "2016-06-01";  // Start date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                try
                {
                    c.setTime(sdf.parse(dateInString));
                }
                catch (java.text.ParseException e)
                {
                    e.printStackTrace();
                }

                toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                drawer.setDrawerListener(toggle);
                toggle.syncState();

                // Hago que los colores de los iconos del menú del drawer layout no cambien
                navigationView.setItemIconTintList(null);
                navigationView.setNavigationItemSelectedListener(this);

                // Evento click normal sobre un día del calendario
                cv.setEventHandlerClick(new CalendarView.EventHandlerClick()
                {
                    @Override
                    public void onDayClick(Date date)
                    {

                    }
                });

                // Evento click prolongado sobre un día del calendario
                // Hago cuando se pulse prolongadamente un dia se muestre la pantalla de anotar asistencia con esa fecha
                cv.setEventHandlerLongClick(new CalendarView.EventHandlerLongClick()
                {
                    @Override
                    public void onDayLongPress(Date date)
                    {
                        SimpleDateFormat anio = new SimpleDateFormat("yyyy");
                        SimpleDateFormat mes = new SimpleDateFormat("MM");
                        SimpleDateFormat dia = new SimpleDateFormat("dd");
                        Intent intent = new Intent(VistaPantallaPrincipal.this, VistaAnotarAsistencia.class);
                        intent.putExtra("llamadodesde", "calendario");
                        intent.putExtra("dia", dia.format(date));
                        intent.putExtra("mes", mes.format(date));
                        intent.putExtra("anio", anio.format(date));
                        startActivity(intent);
                    }
                });

                bSalir.setOnClickListener(this);

                // Obtengo el usuario y lo pongo en la cabecera del menu
                tNombreUsuarioCabecera.setText(email);
                tUsuarioConectado.setText(email);

                // Compruebo si el profesor es administrador o no de la aplicación
                Profesor p = controladorp.obtenerProfesorPorEmail(token, email);

                switch (controladorp.obtenerProfesorPorEmail(token, email).getTipo())
                {
                    case "ADMINISTRADOR":
                        menusAdministrador();
                        break;
                    case "PROFESOR":
                        menusProfesor();
                        break;
                    case "DIRECTOR":
                        menusDirector();
                        break;
                }
            }
            else
            {
                // Si la contraseña no es la misma es porque el profesor la ha cambaido
                // volvemos a la pantalla de login con un mensaje
                volverALogin();
                Utilidades.mostrarToastText(this, Utilidades.obtenerStringXML(this, R.string.texto_error_servidor));
            }
        }
        catch (Exception e)
        {
            volverALogin();
            Utilidades.mostrarToastText(this, Utilidades.obtenerStringXML(this, R.string.texto_error_servidor));
            e.printStackTrace();
        }

    }

    /**
     * Va a la pantalla de login
     */
    private void volverALogin()
    {
        // Vacío las preferencias
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(
                        this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("recordarme", Boolean.FALSE);
        editor.putString("usuario", "");
        editor.putString("password", "");
        editor.putString("token", "");
        editor.commit();

        Intent intent = new Intent(VistaPantallaPrincipal.this, VistaLogin.class);
        // Hago que se limpie la pila de llamadas
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch( item.getItemId() )
        {
            case R.id.nav_agregaralumno:
                irAgregarAlumno();
                break;
            case R.id.nav_consultareliminaralumnos:
                irConsultarAlumnos(Boolean.TRUE);
                break;
            case R.id.nav_consultaralumnos:
                irConsultarAlumnos(Boolean.FALSE);
                break;
            case R.id.nav_anotarasistencia:
                irAnotarAsistencias();
                break;
            case R.id.nav_consultarasistencia:
                irConsultarAsistencias();
                break;
            case R.id.nav_modificarasistencia:
                irModificarAsistencia();
                break;
            case R.id.nav_sobreapp:
                irAAcerdaDe();
                break;
            case R.id.nav_configuracion:
                irAConfiguracion();
                break;
            case R.id.nav_borrarasistencias:
                FragmentManager fragmentManager = getFragmentManager();
                DialogoConfirmacionBorrarTodasAsistencias dialogo2 = new DialogoConfirmacionBorrarTodasAsistencias(this);
                dialogo2.show(fragmentManager, "tagAlerta");
                break;
            case R.id.nav_consultarfaltas:
                irAConsultarFechaFaltas();
                break;
            case R.id.nav_agregarprofesor:
                irAAgregarProfesor();
                break;
            case R.id.nav_consultareliminarprofesores:
                irAConsutarProfesores();
                break;
            case R.id.nav_misdatosprofesor:
                irAMisDatosProfesor();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Habilita el menú del administrador
     */
    private void menusAdministrador()
    {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_agregaralumno).setVisible(true);
        nav_Menu.findItem(R.id.nav_consultareliminaralumnos).setVisible(true);
        nav_Menu.findItem(R.id.nav_consultaralumnos).setVisible(false);
        nav_Menu.findItem(R.id.nav_agregarprofesor).setVisible(true);
        nav_Menu.findItem(R.id.nav_consultareliminarprofesores).setVisible(true);
        nav_Menu.findItem(R.id.nav_misdatosprofesor).setVisible(true);
        nav_Menu.findItem(R.id.nav_anotarasistencia).setVisible(true);
        nav_Menu.findItem(R.id.nav_consultarasistencia).setVisible(true);
        nav_Menu.findItem(R.id.nav_consultarfaltas).setVisible(true);
        nav_Menu.findItem(R.id.nav_modificarasistencia).setVisible(true);
        nav_Menu.findItem(R.id.nav_borrarasistencias).setVisible(true);
        nav_Menu.findItem(R.id.nav_configuracion).setVisible(true);
        nav_Menu.findItem(R.id.nav_sobreapp).setVisible(true);
        //nav_Menu.getItem(1).setVisible(Boolean.TRUE);
    }

    /**
     * Habilita el menú de los profesores
     */
    private void menusProfesor()
    {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_agregaralumno).setVisible(false);
        nav_Menu.findItem(R.id.nav_consultareliminaralumnos).setVisible(false);
        nav_Menu.findItem(R.id.nav_consultaralumnos).setVisible(true);
        nav_Menu.findItem(R.id.nav_agregarprofesor).setVisible(false);
        nav_Menu.findItem(R.id.nav_consultareliminarprofesores).setVisible(false);
        nav_Menu.findItem(R.id.nav_misdatosprofesor).setVisible(true);
        nav_Menu.findItem(R.id.nav_anotarasistencia).setVisible(true);
        nav_Menu.findItem(R.id.nav_consultarasistencia).setVisible(true);
        nav_Menu.findItem(R.id.nav_consultarfaltas).setVisible(true);
        nav_Menu.findItem(R.id.nav_modificarasistencia).setVisible(true);
        nav_Menu.findItem(R.id.nav_borrarasistencias).setVisible(false);
        nav_Menu.findItem(R.id.nav_configuracion).setVisible(true);
        nav_Menu.findItem(R.id.nav_sobreapp).setVisible(true);
        //nav_Menu.getItem(1).setVisible(Boolean.FALSE);
    }

    /**
     * Habilita el menú del director
     */
    private void menusDirector()
    {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_agregaralumno).setVisible(false);
        nav_Menu.findItem(R.id.nav_consultareliminaralumnos).setVisible(false);
        nav_Menu.findItem(R.id.nav_consultaralumnos).setVisible(true);
        nav_Menu.findItem(R.id.nav_agregarprofesor).setVisible(false);
        nav_Menu.findItem(R.id.nav_consultareliminarprofesores).setVisible(true);
        nav_Menu.findItem(R.id.nav_misdatosprofesor).setVisible(true);
        nav_Menu.findItem(R.id.nav_anotarasistencia).setVisible(true);
        nav_Menu.findItem(R.id.nav_consultarasistencia).setVisible(true);
        nav_Menu.findItem(R.id.nav_consultarfaltas).setVisible(true);
        nav_Menu.findItem(R.id.nav_modificarasistencia).setVisible(true);
        nav_Menu.findItem(R.id.nav_borrarasistencias).setVisible(false);
        nav_Menu.findItem(R.id.nav_configuracion).setVisible(true);
        nav_Menu.findItem(R.id.nav_sobreapp).setVisible(true);

        MenuItem consultarasistencia = nav_Menu.findItem(R.id.nav_consultarasistencia);
        consultarasistencia.setIcon(R.drawable.calendar_multiple_check2);
        MenuItem consultarfaltas = nav_Menu.findItem(R.id.nav_consultarfaltas);
        consultarfaltas.setIcon(R.drawable.alarm_light2);

        MenuItem consultarprofesores = nav_Menu.findItem(R.id.nav_consultareliminarprofesores);
        consultarprofesores.setTitle(R.string.menu_consultar_profesores);

        //nav_Menu.getItem(1).setVisible(Boolean.FALSE);
    }

    /**
     * Va a la pantalla de acerca de
     */
    private void irAAcerdaDe()
    {
        Intent intent = new Intent(VistaPantallaPrincipal.this, VistaAcercaDeApp.class);
        startActivity(intent);
    }

    /**
     * Va a la pantalla de mis datos de profesor
     */
    private void irAMisDatosProfesor()
    {
        Intent intent = new Intent(VistaPantallaPrincipal.this, VistaMisDatosProfesor.class);
        startActivity(intent);
    }

    /**
     * Va a la pantalla de consultar profesores
     */
    private void irAConsutarProfesores()
    {
        Intent intent = new Intent(VistaPantallaPrincipal.this, VistaConsultarProfesores.class);
        startActivity(intent);
    }

    /**
     * Va a la pantalla de agregar profesor
     */
    private void irAAgregarProfesor()
    {
        Intent intent = new Intent(VistaPantallaPrincipal.this, VistaAgregarProfesor.class);
        startActivity(intent);
    }

    /**
     * Va a la pantalla de modificar una asistencia
     */
    private void irModificarAsistencia()
    {
        Intent intent = new Intent(VistaPantallaPrincipal.this, VistaModificarDatosAsistencia.class);
        startActivity(intent);
    }

    /**
     * Va a la pantalla de agregar alumno
     */
    private void irAgregarAlumno()
    {
        Intent intent = new Intent(VistaPantallaPrincipal.this, VistaAgregarAlumno.class);
        startActivity(intent);
    }

    /**
     * Va a la pantalla de consultar fecha de faltas
     */
    private void irAConsultarFechaFaltas()
    {
        Intent intent = new Intent(VistaPantallaPrincipal.this, VistaConsultarFechaFaltas.class);
        startActivity(intent);
    }

    /**
     * Va a la pantalla de consultar alumnos
     * @param mostrarbotones Indica si se mostrarán los botones de eliminar y modificar los alumnos
     */
    private void irConsultarAlumnos(Boolean mostrarbotones)
    {
        Intent intent = new Intent(VistaPantallaPrincipal.this, VistaConsultarAlumnos.class);
        intent.putExtra("mostrarbotones", mostrarbotones);
        startActivity(intent);
    }

    /**
     * Va a la pantalla de anotar asistencias
     */
    private void irAnotarAsistencias()
    {
        Intent intent = new Intent(VistaPantallaPrincipal.this, VistaAnotarAsistencia.class);
        intent.putExtra("llamadodesde", "menu");
        startActivity(intent);
    }

    /**
     * Va a la pantalla de consultar asistencias
     */
    private void irConsultarAsistencias()
    {
        Intent intent = new Intent(VistaPantallaPrincipal.this, VistaConsultarAsistencia.class);
        startActivity(intent);
    }

    /**
     * Va a la pantalla del login
     */
    private void irALogin()
    {
        Intent intent = new Intent(VistaPantallaPrincipal.this, VistaLogin.class);
        // Hago que se limpie la pila de llamadas
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("vengo", "main");
        startActivity(intent);
    }

    /**
     * Va a la pantalla de configuración
     */
    private void irAConfiguracion()
    {
        Intent intent = new Intent(VistaPantallaPrincipal.this,
                VistaConfiguracion.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v)
    {
        switch( v.getId() )
        {
            case R.id.bSalir:
                irALogin();
                break;
        }
    }
}
