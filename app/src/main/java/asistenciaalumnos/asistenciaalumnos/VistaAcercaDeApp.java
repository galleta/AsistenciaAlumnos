package asistenciaalumnos.asistenciaalumnos;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;
import utilidades.Utilidades;

/*
    Biblioteca del about us
    https://github.com/medyo/android-about-page
 */

public class VistaAcercaDeApp extends AppCompatActivity
{
    private Toolbar toolbar_acercade;
    private LinearLayout layoutprincipal;

    private Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(getString(R.string.copy_right), Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.about_icon_copy_right);
        copyRightsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VistaAcercaDeApp.this, copyrights, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }

    // https://github.com/medyo/android-about-page/blob/master/library/src/main/java/mehdi/sakout/aboutpage/AboutPage.java
    private Element elementoLinkedIn() {
        Element elementolinkedin = new Element();
        final String textolinkedin = Utilidades.obtenerStringXML(this, R.string.texto_visita_linkedin);
        elementolinkedin.setTitle(textolinkedin);
        elementolinkedin.setIconDrawable(R.drawable.linkedin_box);
        elementolinkedin.setIconTint(R.color.linkedin_background);
        elementolinkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://es.linkedin.com/in/francisco-jes%C3%BAs-delgado-almir%C3%B3n-584a63139"));
                startActivity(intent);
            }
        });
        return elementolinkedin;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acerca_de);

        // ***** Obtengo los recursos de la aplicación *****
        toolbar_acercade = findViewById(R.id.toolbar_acercade);
        layoutprincipal = findViewById(R.id.layoutprincipal);
        //tVersionApp = (TextView) findViewById(R.id.tVersionApp);
        // *************************************************

        //setSupportActionBar(toolbar_acercade);
        toolbar_acercade.setTitle(Utilidades.obtenerStringXML(this, R.string.title_activity_acerca_de));
        toolbar_acercade.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar_acercade.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Element adsElement = new Element();
        adsElement.setTitle("Advertise with us");

        String texto = Utilidades.obtenerStringXML(this, R.string.texto_aplicacion);
        texto += "\n\n" + String.format(getString(R.string.texto_autor_acercade), "Francisco Jesús Delgado Almirón");

        String textoversion = Utilidades.obtenerStringXML(this, R.string.texto_version);
        PackageInfo info = null;
        try
        {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
            textoversion = textoversion + " " + info.versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.mipmap.ic_launcher)
                .setDescription(texto)
                .addItem(new Element().setTitle(Utilidades.obtenerStringXML(this, R.string.texto_contacta)).setGravity(Gravity.CENTER))
                .addEmail("francisdelgado86@gmail.com", Utilidades.obtenerStringXML(this, R.string.texto_mandame_email))
                //.addWebsite("http://medyo.github.io/")
                //.addFacebook("the.medy")
                //.addTwitter("medyo80")
                //.addYoutube("UCdPQtdWIsg7_pi4mrRu46vA")
                .addPlayStore("Francisco+Jesús+Delgado+Almirón", Utilidades.obtenerStringXML(this, R.string.texto_visita_playstore))
                //.addInstagram("medyo80")
                .addGitHub("galleta", Utilidades.obtenerStringXML(this, R.string.texto_visita_github))
                .addItem(elementoLinkedIn())
                .addItem(new Element().setTitle(textoversion))
                .addItem(getCopyRightsElement())
                .create();

        layoutprincipal.addView(aboutPage);
    }
}
