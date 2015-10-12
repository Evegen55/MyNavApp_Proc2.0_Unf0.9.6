/*
 * Copyright (C) 2015 Lartsev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package my_papplet_for_data;

//Java utilities libraries
import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;

//Processing library
import processing.core.PApplet;
import processing.core.PGraphics;
        
//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.HashMap;

//Parsing library
import parsing.ParseFeed;

/** 
 * 
 * EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author  UC San Diego Intermediate Software Development MOOC team; Lartsev
 * Date: July 17, 2015
 * */
public class MyEarthquakeCityMap extends PApplet {
 
    // IF YOU ARE WORKING OFFLINE, change the value of this variable to true
    private static final boolean offline = false;
    
    /**
     *Less than this threshold is a light earthquake
     */
    public static final float THRESHOLD_MODERATE = 5;

    /**
     *Less than this threshold is a minor earthquake
     */
    public static final float THRESHOLD_LIGHT = 4;

    /** This is where to find the local tiles, for working without an Internet connection */
    public static String mbTilesString = "blankLight-1-3.mbtiles";
    
    // The map
    private UnfoldingMap map; 
    
    //feed with magnitude 2.5+ Earthquakes
    private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
    
    //loading data
    //private HashMap<String, Object> listOfProperties;
    
    //Properties and location
    private List<PointFeature> earthquakes;
    
    //Visual representation
    private List<Marker> markers;
    
    //simple point marker for a feature
    private SimplePointMarker spm;
    
    //simple point marker for adding on a list
    private SimplePointMarker mrk;
   
    private PGraphics pg;
    
    //color for somehing
    private int color;
    
    //radius of marker
    private int radius;
    
    //zoom for map
    private float zoomScale;
    
    //привязать эту переменную к кнопке
    private boolean setMapAsFoto = false; 
    
    //use native Java library to getting a screen size
    private final Toolkit kit = Toolkit.getDefaultToolkit();
    private final Dimension screenSize = kit.getScreenSize();
    
    private final int screenWidth =  screenSize.width;
    private final int screenHeight = screenSize.height;
    
    private final int screenWidthForApp = screenWidth-screenWidth/10;
    private final int screenHeightForApp = screenHeight-screenHeight/10;
        
    private final int startPointOnX_ForMap = screenWidth/7;
    private final int startPointOnY_ForMap = screenHeight/20;
    
    private final int screenWidthForMap = screenWidthForApp-screenWidthForApp/5;
    private final int screenHeightForMap = screenHeightForApp-screenHeightForApp/15;    

    /**
     *
     */
    @Override
    public void setup() {
        size(screenWidthForApp, screenHeightForApp, OPENGL);
        
        if (offline) {
            map = new UnfoldingMap(this, startPointOnX_ForMap, startPointOnY_ForMap, screenWidthForMap, screenHeightForMap, new MBTilesMapProvider(mbTilesString));
            earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
        } else if (setMapAsFoto){
            map = new UnfoldingMap(this, startPointOnX_ForMap, startPointOnY_ForMap, screenWidthForMap, screenHeightForMap, new Google.GoogleMapProvider());
            // IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
            // earthquakesURL = "2.5_week.atom";
        } else {
            map = new UnfoldingMap(this, startPointOnX_ForMap, startPointOnY_ForMap, screenWidthForMap, screenHeightForMap, new Microsoft.AerialProvider());
        }
        zoomScale = 1.85F;
        map.zoomTo(zoomScale); 
                        
        MapUtils.createDefaultEventDispatcher(this, map); 
        
        // The List you will populate with new SimplePointMarkers
        markers = new ArrayList<Marker>(); 
        
        //Use provided parser to collect properties for each earthquake
        //PointFeatures have a getLocation method
        earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
        if (earthquakes.size() > 0) {
            for (PointFeature f : earthquakes) {
                //create simple point marker for a each feature:
                mrk = createSimplePointMarker(f);
                markers.add(mrk);
            }
        }
        
        // prevent thread from starving everything else
        //noLoop();
        
        System.out.println("screenWidth = " + screenWidth + "\n" + "screenHeight = " + screenHeight + "\n");
    }

    /**
     *
     */
    @Override
    public void draw() {
        background(150,150,150);
        map.draw();
        map.addMarkers(markers);
        addKey();
        visPropert();
        
    }

    /**
     * A suggested helper method that takes in an earthquake feature and
     * returns a SimplePointMarker for that earthquake with list of properties
     * @param feature is a de.fhpotsdam.unfolding.data.PointFeature object
     * @return de.fhpotsdam.unfolding.marker.SimplePointMarker object
     */
    public SimplePointMarker createSimplePointMarker(PointFeature feature) {
        pg = new PGraphics();
        Object magObj = feature.getProperty("magnitude");
        float magnitude = Float.parseFloat(magObj.toString());
        spm = new SimplePointMarker(feature.getLocation());
        //Here is an example of how to use Processing's color method to generate
        //an int that represents the color blue,yellow and red.
        if (magnitude<THRESHOLD_LIGHT){
            color = color(0, 0, 255);
            radius = 5;
        } else if (magnitude>THRESHOLD_LIGHT && magnitude<THRESHOLD_MODERATE) {
            color = color(255, 255, 0);
            radius = 10;            
        } else if (magnitude>=THRESHOLD_MODERATE) {
            color = color(255, 0, 0);
            radius = 20;            
        }
        spm.setRadius(radius);
        spm.setColor(color);
        //set list properties for marker as equal to list of properties feature
        spm.setProperties(feature.getProperties());
        spm.draw(pg,10,10);

        return spm;
	}
	
    /**
     *
     * helper method to draw key in GUI
     * Remember you can use Processing's graphics methods here
     */
    public void addKey() {
        fill(255, 255, 255);
        rect(25,50,150,250);//изменить способ задания координат на привязанные к экрану - везде!
        fill(0, 0, 255);
        ellipse(50,135,5,5);
        fill(255, 255, 0);
        ellipse(50,105,10,10);
        fill(255, 0, 0);
        ellipse(50,75,20,20);
        noFill();
        rect(startPointOnX_ForMap, startPointOnY_ForMap, screenWidthForMap, screenHeightForMap);
    } 
    
        
    /**
     *
     * helper method to draw properties pop-up window in GUI near marker
     */
    public void visPropert() {
        //get mouse coordinates
        Point location = MouseInfo.getPointerInfo().getLocation();
        float xPos = (float) location.getX();
        float yPos = (float) location.getY();
        
        ScreenPosition sp = new ScreenPosition(xPos, yPos);
        boolean isHitted = map.isHit(sp);
        
        if(isHitted){
        //get a marker nearest on a mouse
        SimplePointMarker spmFromMap = 
                (SimplePointMarker) map.getDefaultMarkerManager().getNearestMarker(xPos, yPos);
        
        if (spmFromMap == null) {
            System.out.println("spmFromMap is NULL");
        } else {
            System.out.println(spmFromMap.getProperties());
            //get a marker screen position and draw a rectangle with text
            ScreenPosition sp_spmFromMap = spmFromMap.getScreenPosition(map);
            float xPosMark = sp_spmFromMap.x;
            float yPosMark = sp_spmFromMap.y;
            fill(xPosMark,yPosMark,100,100);
            rect(xPosMark,yPosMark,100,100);
            
        }
        }
    }
    
    //РАЗОБРАТЬСЯ, ДОРАБОТАТЬ!!!!!
    @Override
    public void keyPressed() {
        if (key == '1') {
            setMapAsFoto = true;
        } else if (key == '2') {
            setMapAsFoto = false;
        }
    }
    
    /**
     *
     * @param args the command line arguments
     */
    /*    public static void main(String args[]) {
    
    PApplet.main(new String[] {
    "--present", "my_papplet_for_data.MyEarthquakeCityMap"
    });
    }*/
}
