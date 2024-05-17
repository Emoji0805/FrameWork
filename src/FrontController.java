package controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import annotation.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class FrontController extends HttpServlet {

    private List<String> controllerNames;
    private List<Class<?>> Listecontroller;
    private boolean isChecked = false;

    // public void init() throws ServletException {
    //     String packageToScan = this.getInitParameter("package_name");

    //     controllerNames = new ArrayList<>();

    //     try {
    //         String path = getClass().getClassLoader().getResource(packageToScan.replace('.', '/')).getPath();
    //         String decodedPath = URLDecoder.decode(path, "UTF-8");
    //         File packageDir = new File(decodedPath);

    //         File[] files = packageDir.listFiles();
    //         if (files != null) {
    //             for (File file : files) {
    //                 if (file.isFile() && file.getName().endsWith(".class")) {
    //                     String className = packageToScan + "." + file.getName().replace(".class", "");
    //                     Class<?> clazz = Class.forName(className);
    //                     if (clazz.isAnnotationPresent(ControllerAnnotation.class)) {
    //                         controllerNames.add(clazz.getSimpleName());
    //                     }
    //                 }
    //             }
    //         }
    //     } catch (IOException | ClassNotFoundException e) {
    //         e.printStackTrace();
    //     }
    // }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processedRequest(req, res);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        processedRequest(req, res);
    }

    protected void processedRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        StringBuffer url = req.getRequestURL();
        PrintWriter out = res.getWriter();
        out.println("Go london : "+url);
        
        if(!this.isChecked){
          String packageScan=this.getInitParameter("package_name");
          try{
               this.Listecontroller=this.getListeControllers(packageScan);
               this.isChecked=true;
          }
          catch (Exception e){
                e.printStackTrace();
          }
        }
        /* Affichage des Controller */
        for (Class<?> classs : Listecontroller) {
            out.println(classs.getName());
        }
    }

    boolean isController(Class<?> c) {
        return c.isAnnotationPresent(ControllerAnnotation.class);
    }
    List<Class<?>> getListeControllers(String packageName) throws Exception {
        List<Class<?>> res=new ArrayList<Class<?>>();
        
        //répertoire racine du package
        String path = this.getClass().getClassLoader().getResource(packageName.replace('.', '/')).getPath();
        String decodedPath = URLDecoder.decode(path, "UTF-8");
        File packageDir = new File(decodedPath);

        // parcourir tous les fichiers dans le répertoire du package
        File[] files = packageDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".class")) {
                    String className = packageName + "." + file.getName().replace(".class", "");
                    Class<?> classe = Class.forName(className);
                    if (isController(classe)) {
                        res.add(classe);
                    }
                }
            }
        }
        return res;
    }
}
