package project.skitracker.providers;

import android.os.Environment;
import project.skitracker.MainActivity;
import project.skitracker.exceptions.NotImplementedException;
import project.skitracker.settings.Properties;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class KMLFileProvider
{
    private MainActivity mainActivity;
    private File kmlFile;
    private FileWriter fout;

    public KMLFileProvider(MainActivity sender)
    {
        Initialize(sender);
    }
    private void Initialize(MainActivity sender)
    {
        mainActivity = sender;
    }

    public boolean OpenFile(String filename) //TODO: Modify section LookAt
    {
        try
        {
            kmlFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
            fout = new FileWriter(kmlFile);
            fout.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            fout.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\"\n" + " xmlns:gx=\"http://www.google.com/kml/ext/2.2\">\n");
            fout.append("<Placemark>\n" +
                    "   <name>gx:altitudeMode Example</name>\n" +
                    "   <LookAt>\n" +
                    "       <longitude>146.806</longitude>\n" +
                    "       <latitude>12.219</latitude>\n" +
                    "       <heading>-60</heading>\n" +
                    "       <tilt>70</tilt>\n" +
                    "       <range>6300</range>\n" +
                    "       <gx:altitudeMode>clampToSeaFloor</gx:altitudeMode>\n" +
                    "  </LookAt>\n" +
                    "  <LineString>\n" +
                    "       <extrude>1</extrude>\n" +
                    "       <gx:altitudeMode>relativeToSeaFloor</gx:altitudeMode>\n" +
                    "       <coordinates>\n");
            Properties.is_kml_file_opened = true;
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    public synchronized boolean AppendFile(String s)
    {
        try
        {
            fout.append("           " + s);
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }
    public synchronized void AppendAllCoordinates(ArrayList<String> list)
    {
        if (list != null)
        {
            for (int i = 0; i < list.size(); i++)
            {
                AppendFile(list.get(i));
            }
        }
    }
    public boolean CloseFile()
    {
        Properties.is_kml_file_opened = false;
        try
        {
            AppendFile("        </coordinates>\n" +
                    "   </LineString>\n" +
                    "</Placemark>\n" +
                    "\n" +
                    "</kml>");
            fout.flush();
            fout.close();
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }
    public void setPlacemarkName(String name) throws NotImplementedException
    {
        throw new NotImplementedException();
    }
}
