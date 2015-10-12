/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my_papplet_for_data;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;
import java.awt.Dimension;
import java.awt.Toolkit;
import processing.core.PApplet;

/**
 *
 * @author Lartsev
 */
public class MyMapFirst extends PApplet {
    
    // The map
    private UnfoldingMap map_first;
    
    //feed with magnitude 2.5+ Earthquakes - example
    //private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
    
    //zoom for map
    private float zoomScaleFirstMap;
    
    //привязать эту переменную к кнопке
    private boolean setMapAsFoto = true;
    
    //use native Java library to getting a screen size
    public Toolkit kit = Toolkit.getDefaultToolkit();
    public Dimension screenSize = kit.getScreenSize();
    
    public int screenWidth =  screenSize.width;
    public int screenHeight = screenSize.height;
    
    private final int screenWidthForApp = screenWidth-screenWidth/10;
    private final int screenHeightForApp = screenHeight-screenHeight/10;
        
    private final int startPointOnX_ForMap = screenWidth/7;
    private final int startPointOnY_ForMap = screenHeight/20;
    
    private final int screenWidthForMap = screenWidthForApp-screenWidthForApp/5;
    private final int screenHeightForMap = screenHeightForApp-screenHeightForApp/15;
    
@Override
    public void setup() {
        size(screenWidthForApp, screenHeightForApp, OPENGL);
        
        if (setMapAsFoto) {
            map_first = new UnfoldingMap(this, startPointOnX_ForMap, startPointOnY_ForMap, screenWidthForMap, screenHeightForMap, new Google.GoogleMapProvider());
            
        } else {
           map_first = new UnfoldingMap(this, startPointOnX_ForMap, startPointOnY_ForMap, screenWidthForMap, screenHeightForMap, new Microsoft.AerialProvider());
        }

        zoomScaleFirstMap = 1.85F;
        map_first.zoomTo(zoomScaleFirstMap);        
        
        MapUtils.createDefaultEventDispatcher(this, map_first);        
        
        
    }
    
    @Override
    public void draw() {
        map_first.draw();
    }    
            
    
}
