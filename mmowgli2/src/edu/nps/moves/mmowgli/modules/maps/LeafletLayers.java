package edu.nps.moves.mmowgli.modules.maps;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LTileLayer;

public class LeafletLayers
{
  public static String LAYERDATAFILE = "LeafletLayersTabDelim.txt"; // in this package
  private static HashMap<String,LeafletProvider> providers;

  public static class LeafletProvider
  {
    public String key = null;
    public Boolean baseLayer = false;
    public String prettyName = "";
    public String link = "";
    public String attribution = "";
    public Integer maxZoom = null;
    public Double opacity = null;
    public String subdomains = null;

    public LeafletProvider()
    {
    }

    public LeafletProvider(String key, String prettyName, Boolean baseLayer,
        String link, String attribution, Integer maxZoom, Double opacity,
        String subdomains)
    {
      this.key = key;
      this.baseLayer = baseLayer;
      this.prettyName = prettyName;
      this.link = link;
      this.attribution = attribution;
      this.maxZoom = maxZoom;
      this.opacity = opacity;
      this.subdomains = subdomains;
    }
  }

  // Data
  // The rows in the tab-delimited data file, in this package/directory on the classpath, are in this order
  public static  int KEY = 0, PRETTYNAME = 1, BASELAYER = 2, LINK = 3,
      ATTRIBUTION = 4, MAXZOOM = 5, OPACITY = 6, SUBDOMAINS = 7;
  
  //@formatter:off
  static {
    providers = new HashMap<String, LeafletProvider>();

    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(LeafletLayers.class.getResourceAsStream(LAYERDATAFILE)));
      boolean headerRead = false;
      while(br.ready()) {
        String line = br.readLine();
        if(!headerRead) {
          headerRead = true;
          continue;
        }
        String[] sa = line.split("\t");
        LeafletProvider prov = new LeafletProvider();

        if(sa.length>=KEY+1) {
          prov.key = sa[KEY];
          if (sa.length>=PRETTYNAME+1) {
            prov.prettyName = handleString(sa[PRETTYNAME]);
            if(sa.length>=BASELAYER+1) {
              prov.baseLayer = handleBoolean(sa[BASELAYER]);
              if(sa.length>=LINK+1) {
                prov.link = handleString(sa[LINK]);
                if(sa.length>=ATTRIBUTION+1) {
                  prov.attribution = handleString(sa[ATTRIBUTION]);
                  if(sa.length>=MAXZOOM+1) {
                    prov.maxZoom = handleInteger(sa[MAXZOOM]);
                    if(sa.length>=OPACITY+1) {
                      prov.opacity=handleDouble(sa[OPACITY]);
                      if(sa.length>=SUBDOMAINS+1) {
                        prov.subdomains=handleString(sa[SUBDOMAINS]);
                      }
                    }
                  }
                }
              }
            }
          }
        }      
        providers.put(prov.key, prov);
      } 
    }                 
    catch(Throwable ex) {
      System.err.println("Program data error in LeafletLayers: "+ex.getClass().getSimpleName()+" / "+ex.getLocalizedMessage());
    }
  }
  
  private static String handleString(String s)
  {
    return s;
  }
  
  private static Boolean handleBoolean(String b)
  {
    if(b == null || b.length()<=0)
      return false;
    return Boolean.parseBoolean(b);
  }
  
  private static Integer handleInteger(String i)
  {
    if(i == null || i.length()<=0)
      return null;
    return Integer.parseInt(i);
  }
  
  private static Double handleDouble(String d)
  {
    if(d == null || d.length()<=0)
      return null;
    return Double.parseDouble(d);
  }

  public static LeafletProvider installProvider(String key, LMap map) throws Exception
  {
    LeafletProvider prov = providers.get(key);
    if (prov == null)
      throw new Exception("Unrecognized provider key");

    LTileLayer tileLayer = new LTileLayer(prov.link);
    if(prov.attribution != null && prov.attribution.length()>0)
      tileLayer.setAttributionString(prov.attribution);
    if(prov.maxZoom != null)
      tileLayer.setMaxZoom(prov.maxZoom);
    if(prov.opacity != null)
      tileLayer.setOpacity(prov.opacity);
    if(prov.subdomains != null && prov.subdomains.length()>0)      
      tileLayer.setSubDomains(prov.subdomains);

    if(prov.baseLayer)
      map.addBaseLayer(tileLayer, prov.prettyName);
    else
      map.addOverlay(tileLayer, prov.prettyName);

    return prov;
  }
  
  public static void installAllProviders(LMap map) throws Exception
  {
    List<String> lis = new ArrayList<String>(providers.keySet());
    Collections.sort(lis);
    for(String key : lis)
      installProvider(key,map);
  }
}
