package project.skitracker.providers;

import android.os.Environment;
import project.skitracker.MainActivity;
import project.skitracker.exceptions.NotImplementedException;
import project.skitracker.settings.Properties;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Klasa odpowiedzialna za tworzenie plików KML
 */
public class KMLFileProvider
{
    @Deprecated
    private MainActivity mainActivity;
    private File kmlFile;
    private FileWriter fout;

    public KMLFileProvider(MainActivity sender)
    {
        initialize(sender);
    }

    private void initialize(MainActivity sender)
    {
        mainActivity = sender;
    }

    /**
     * Metoda tworzy nowy plik, jeśli taki nie istnieje. Nazwa przekazywana do metody powinna byc unikalna!
     * @param filename Nazwa pliku
     * @return
     */
    public boolean openFile(String filename)
    {
        try
        {
            kmlFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
            fout = new FileWriter(kmlFile);
            fout.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
            fout.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\"\r\n" + " xmlns:gx=\"http://www.google.com/kml/ext/2.2\">\r\n");
            fout.append("<Placemark>\r\n" +
                    "   <name>track</name>\r\n" +
                    "   <LookAt>\r\n" +
                    "       <longitude>146.806</longitude>\r\n" +
                    "       <latitude>12.219</latitude>\r\n" +
                    "       <heading>-60</heading>\r\n" +
                    "       <tilt>70</tilt>\r\n" +
                    "       <range>6300</range>\r\n" +
                    "       <gx:altitudeMode>clampToSeaFloor</gx:altitudeMode>\r\n" +
                    "  </LookAt>\r\n" +
                    "  <LineString>\r\n" +
                    "       <extrude>1</extrude>\r\n" +
                    "       <gx:altitudeMode>relativeToSeaFloor</gx:altitudeMode>\r\n" +
                    "       <coordinates>\r\n");
            Properties.is_kml_file_opened = true;
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Metoda zapisująca do pliku jedne współrzędne
     * @param s String postaci (Dlugosc,Szerokosc)
     * @return Zwraca true jesli zapis się powiódł
     */
    public synchronized boolean addCoordinates(String s)
    {
        try
        {
            fout.append("           " + s + "\r\n");
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Metoda zapisująca do pliku liste współrzędnych
     * @param list Lista stringow postaci (Dlugosc,Szerokosc)
     * @return Zwraca true jesli zapis się powiódł
     */
    public synchronized void addAllCoordinates(ArrayList<String> list)
    {
        if (list != null)
        {
            for (int i = 0; i < list.size(); i++)
            {
                addCoordinates(list.get(i));
            }
        }
    }

    /**
     * Metoda służy do zamknięcia pliku
     * @return Zwraca true jesli zapis się powiódł
     */
    public boolean closeFile()
    {
        Properties.is_kml_file_opened = false;
        try
        {
            addCoordinates("        </coordinates>\r\n" +
                    "   </LineString>\r\n" +
                    "</Placemark>\r\n" +
                    "\r\n" +
                    "</kml>");
            fout.flush();
            fout.close();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
