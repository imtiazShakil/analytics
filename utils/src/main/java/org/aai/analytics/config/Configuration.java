package org.aai.analytics.config;

import lombok.extern.log4j.Log4j2;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.Enumeration;
import java.util.Properties;

/**
 * <h3>Configuration class represents a persistent set of properties.</h3>
 * <p>The System properties plus application properties are stored and access by this class</p>
 *
 * <br><br>This class is thread-safe: multiple threads can share a single Properties
 * <br>object without the need for external synchronization.
 *
 * @author imtiaz
 */
@Log4j2
public class Configuration {

    private Properties properties;

    public Configuration(InputStream is) {
        properties = new Properties();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = reader.readLine();
            while (line != null) {
                if (line.length() > 0 && line.charAt(0) != '#') {
                    String[] output = line.split("=");
                    if (output.length == 2)
                        properties.setProperty(output[0].trim(), output[1].trim());
                }
                // read next line
                line = reader.readLine();
            }
            reader.close();
            is.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
    private Configuration() {

    }


    /**
     * Gets the property indicated by the specified key.
     *
     * @param key the name of the system property.
     * @return the string value of the system property, or null if there is no property with that key.
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String def) {
        return properties.getProperty(key, def);
    }


}
