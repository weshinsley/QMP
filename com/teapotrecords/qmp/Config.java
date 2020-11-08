package com.teapotrecords.qmp;

import java.io.File;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Config {
  QMP parent;
  int screen_x = 0;
  int screen_y = 0;
  int screen_w = 800;
  int screen_h = 600;
  String current_conf = "default.xml";
  String current_conf_short = "default.xml";

  public Config(QMP parent) {
    this.parent = parent;
  }

  // Some XML helper stuff

  private static Element loadDocument(String file) {
    Element root = null;
    try {
      File f = new File(file);
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(f);
      root = doc.getDocumentElement();
      root.normalize();
    } catch (Exception e) { e.printStackTrace(); }
    return root;
  }

  public static Node getTag(Node root, String name) {
    NodeList nl = root.getChildNodes();
    Node result = null;
    for (int i = 0; i < nl.getLength(); i++) {
      if (nl.item(i).getNodeName().equals(name)) {
        result = nl.item(i);
        i = nl.getLength();
      }
    }
    return result;
  }

  public static String getAttribute(Node parent, String attname)  {
    Node n = parent.getAttributes().getNamedItem(attname);
    if (n == null) return null;
    else return n.getTextContent();
  }

  // File handling of system and current config files

  public void saveCurrentConfig() {
    try {
      PrintWriter PW = new PrintWriter(new File(current_conf));
      PW.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
        "<config>\n" +
        "  <id name=\"qmpconfig\" v=\"" + parent.getVersion() + "\" />\n" +
        "  <screen x=\"" + screen_x + "\" y=\"" + screen_y + "\" w=\"" + screen_w + "\" h=\"" + screen_h + "\" />\n" +
        "  <movies>\n");
      for (int i=0; i < parent.full_paths.size(); i++) {
        PW.println("    <movie full_path=\"" + parent.full_paths.get(i) + "\" short_name=\"" + parent.ol_movies.get(i)+"\" />");
      }
      PW.println("  </movies>\n</config>");
      PW.close();
    } catch (Exception e) {
      System.out.println("Exception creating default config...");
    }
  }

  public void saveQMPConfig() {
    try {
      PrintWriter PW = new PrintWriter(new File("qmp.xml"));
      PW.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
          "<qmpcon>\n" +
          "  <lastconfig>" + current_conf + "</lastconfig>\n" +
          "</qmpcon>\n");
      PW.close();
    } catch (Exception e) {
      System.out.println("Exception creating QMP config...");
    }
  }

  public static int countChildren(Node parent,String tag) {
    int i = 0;
    for (int j = 0; j < parent.getChildNodes().getLength(); j++) {
      if (parent.getChildNodes().item(j).getNodeType() == Node.ELEMENT_NODE) {
        if (parent.getChildNodes().item(j).getNodeName().equals(tag)) i++;
      }
    }
    return i;
  }

  public static Node getChildNo(Node parent,String tag,int n) {
    int i = 0;
    Node result = null;
    for (int j = 0; j < parent.getChildNodes().getLength(); j++) {
      if (parent.getChildNodes().item(j).getNodeType() == Node.ELEMENT_NODE) {
        if (parent.getChildNodes().item(j).getNodeName().equals(tag)) {
          if (i == n) {
            result = parent.getChildNodes().item(j);
            j = parent.getChildNodes().getLength();
          }
          i++;
        }
      }
    }
    return result;
  }

  // First-time load QMP system config

  public void loadQMPConfig() {
    if (!new File("qmp.xml").exists()) saveQMPConfig();
    Element qmp_config = loadDocument("qmp.xml");
    Node last_config_tag = getTag(qmp_config, "lastconfig");
    if (last_config_tag == null) {
      saveQMPConfig();
      qmp_config = loadDocument("qmp.xml");
      last_config_tag = getTag(qmp_config, "lastconfig");
    }
    current_conf = last_config_tag.getTextContent();
  }

  // Load current configuration file

  public void loadCurrentConfig() {
    if (!new File(current_conf).exists()) saveCurrentConfig();
    current_conf_short = new File(current_conf).getName();
    Element config = loadDocument(current_conf);
    Node id_tag = getTag(config, "id");
    if (getAttribute(id_tag,  "name").equals("qmpconfig")) {
      // Could do more validation here - eg versions later...
      Node screen_tag = getTag(config, "screen");
      screen_x = Integer.parseInt(getAttribute(screen_tag, "x"));
      screen_y = Integer.parseInt(getAttribute(screen_tag, "y"));
      screen_w = Integer.parseInt(getAttribute(screen_tag, "w"));
      screen_h = Integer.parseInt(getAttribute(screen_tag, "h"));
      parent.full_paths.clear();
      parent.ol_movies.clear();
      Node movies_tag = getTag(config, "movies");
      int n_movies = countChildren(movies_tag, "movie");
      for (int m=0; m<n_movies; m++) {
        Node movie_tag = getChildNo(movies_tag, "movie", m);
        String full_path = getAttribute(movie_tag, "full_path");
        String short_name = getAttribute(movie_tag, "short_name");
        if (new File(full_path).exists()) {
          parent.full_paths.add(full_path);
          parent.ol_movies.add(short_name);
        }
      }
    }
    saveQMPConfig();
  }
}
