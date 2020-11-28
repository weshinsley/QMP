package com.teapotrecords.qmp;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
public class External {
  
  String git_path = null;
  String javac_path = null;
  String ffmpeg_path = null;
  
  private String test_shell(String command, String args, String expected) {
    String result = null;
    try {
      Process p = Runtime.getRuntime().exec(command+" "+args);
      InputStream stderr = p.getErrorStream();
      InputStream stdout = p.getInputStream();
      BufferedReader brerr = new BufferedReader(new InputStreamReader(stderr));
      BufferedReader brout = new BufferedReader(new InputStreamReader(stdout));
      p.waitFor();
      String s = brout.readLine();
      while (s!=null) {
        if (s.startsWith(expected)) {
          result = command;
          s = null;
        } else s = brout.readLine(); 
      }
      brout.close();
      s = brerr.readLine();
      while (s!=null) {
        if (s.startsWith(expected)) {
          result = command;
          s = null;
        } else s = brerr.readLine(); 
      }
      brerr.close();
  
    } catch (Exception e) {}
    return result;
  }
  
  public void update() {
    if ((git_path != null) && (javac_path != null)) {
      try {
        Process p = Runtime.getRuntime().exec(git_path+" pull");
        p.waitFor();
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String s = br.readLine();
        while (s!=null) {
          System.out.println(s);
          s = br.readLine();
        }
          
        br.close();
        System.out.println(p.exitValue());
        p = Runtime.getRuntime().exec(javac_path + " com/teapotrecords/QMP/*.java");
        p.waitFor();
        br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        s = br.readLine();
        while (s!=null) {
          System.out.println(s);
          s = br.readLine();
        }
        br.close();
        System.out.println(p.exitValue());
        
        
         
      } catch (Exception e) {e.printStackTrace(); }
    }
  }
  
  public String[] convertMOV(String fname, String fabspath) {
    String[] res = null;
    System.out.println("ffmpeg_path is "+ffmpeg_path);
    if (ffmpeg_path != null) {
      
      String outpath = "cache/"+System.currentTimeMillis()+".mp4";
      try {
        Process p = Runtime.getRuntime().exec(ffmpeg_path + " -i \""+fabspath+"\" -c:v libx264 -crf 18 -c:a copy "+outpath);
        p.waitFor();
        res = new String[2];
        File f = new File(outpath);
        res[0] = fname;
        res[1] = f.getAbsolutePath();
      } catch (Exception e) { e.printStackTrace(); }
    }
    return res;
  }
  
  public External() {
    git_path = test_shell("git", "--version", "git");
    javac_path = test_shell("javac", "-version", "javac");
    ffmpeg_path = test_shell("ffmpeg", "-version", "ffmpeg");
    System.out.println(git_path+" "+javac_path+" "+ffmpeg_path);
    //update();
  }
}
