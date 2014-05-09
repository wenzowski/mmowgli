package edu.nps.moves.mmowgli.components;

import java.util.HashMap;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LTileLayer;

public class LeafletLayers
{
  public static class LeafletProvider
  {
    public LayerKey key; public String prettyName; public String link; public String attribution; public String maxZoom;
    public LeafletProvider(LayerKey key, String prettyName, String link, String attribution, String maxZoom)
    {
      this.key = key; this.prettyName = prettyName; this.link = link; this.attribution = attribution; this.maxZoom = maxZoom;
    }
  }
  
  static private HashMap<LayerKey,LeafletProvider> providers;
  
  // Keys
  public static enum LayerKey {
    ESRI_WORLDPHYSICAL,
    ESRI_NATGEOWORLDMAP,
    OPENWEATHERMAP_CLOUDS,
    OPENWEATHERMAP_RAINCLASSIC,
    OPENWEATHERMAP_TEMPERATURE,
    MAPQUESTOPEN_AERIAL,
    STAMEN_TONERLITE
  }
  
  // Data
  static private int key=0, prettyName= 1, link=2, attribution=3, maxZoom=4;
  static private Object data[][] = {
      {
          LayerKey.ESRI_WORLDPHYSICAL,
          "ESRI World Physical",
          "http://server.arcgisonline.com/ArcGIS/rest/services/World_Physical_Map/MapServer/tile/{z}/{y}/{x}",
          "Tiles &copy; Esri &mdash; Source: US National Park Service", "8"
      },
      {
          LayerKey.ESRI_NATGEOWORLDMAP,
          "ESRI National Geographic World Map",
          "http://server.arcgisonline.com/ArcGIS/rest/services/NatGeo_World_Map/MapServer/tile/{z}/{y}/{x}",
          "Tiles &copy; Esri &mdash; National Geographic, Esri, DeLorme, NAVTEQ, UNEP-WCMC, USGS, NASA, ESA, METI, NRCAN, GEBCO, NOAA, iPC",
          "16"
      },
      {
          LayerKey.OPENWEATHERMAP_CLOUDS,
          "Open Weather Map / Clouds",
          "http://{s}.tile.openweathermap.org/map/clouds/{z}/{x}/{y}.png",
          "Map data &copy; <a href=\"http://openweathermap.org\">OpenWeatherMap</a>",
          ""
      },
      {
          LayerKey.OPENWEATHERMAP_RAINCLASSIC,
          "Open Weather Map / Rain Classic",
          "http://{s}.tile.openweathermap.org/map/rain_cls/{z}/{x}/{y}.png",
          "Map data &copy; <a href=\"http://openweathermap.org\">OpenWeatherMap</a>",
          ""
      },
      {
        LayerKey.OPENWEATHERMAP_TEMPERATURE,
        "Open Weather Map / Temperature",
        "http://{s}.tile.openweathermap.org/map/temp/{z}/{x}/{y}.png",
        "Map data &copy; <a href=\"http://openweathermap.org\">OpenWeatherMap</a>",
        ""
      },
      {
        LayerKey.MAPQUESTOPEN_AERIAL,
        "MapQuest Open Aerial",
        "http://oatile1.mqcdn.com/tiles/1.0.0/sat/{z}/{x}/{y}.jpg",
        "Tiles Courtesy of <a href=\"http://www.mapquest.com/\">MapQuest</a> &mdash; Portions Courtesy NASA/JPL-Caltech and U.S. Depart. of Agriculture, Farm Service Agency",
        ""
      },
      {
        LayerKey.STAMEN_TONERLITE,
        "Stamen Toner Lite",
        "http://a.tile.stamen.com/toner-lite/{z}/{x}/{y}.png",
        "Map tiles by <a href=\"http://stamen.com\">Stamen Design</a>, <a href=\"http://creativecommons.org/licenses/by/3.0\">CC BY 3.0</a> &mdash; Map data &copy; <a href=\"http://openstreetmap.org\">OpenStreetMap</a> contributors, <a href=\"http://creativecommons.org/licenses/by-sa/2.0/\">CC-BY-SA</a>",
        ""
      }
  };
  
  static {
    providers = new HashMap<LayerKey,LeafletProvider>();
    for(Object[] sa : data) {
      providers.put((LayerKey)sa[key], new LeafletProvider(
          (LayerKey)sa[key],
          sa[prettyName].toString(),
          sa[link].toString(),
          sa[attribution].toString(),
          sa[maxZoom].toString()));
    }
  }
  
  public static LeafletProvider installProvider(LayerKey key, LMap map) throws Exception
  {
    LeafletProvider prov = providers.get(key);
    if(prov == null)
      throw new Exception("Unrecognized provider key");

    LTileLayer tileLayer = new LTileLayer(prov.link);
    tileLayer.setAttributionString(prov.attribution);
   // tileLayer.set
  //  tileLayer.setOpacity(opacity);
   // tileLayer.setMaxZoom(maxZoom);
   // tileLayer.setSubDomains(string);
    //todo
    map.addBaseLayer(tileLayer,prov.prettyName);
    
    return prov;
  }
}
