package controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import annotation.*;

import javax.servlet.*;
import javax.servlet.http.*;
import Utils.*;

public class FrontController extends HttpServlet {

    HashMap<String, Mapping> mapp = new HashMap<>();

    public void init() throws ServletException{

        try{
            String packageToScan = getInitParameter("package_name");
            mapp = getListeClasses(packageToScan);
        }
        catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        processedRequest(req, res);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        processedRequest(req, res);
    }

    protected void processedRequest(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String url = req.getRequestURL().toString();
        PrintWriter out = res.getWriter();
        out.println("Youhouuu");
        for(String cle : mapp.keySet()) {
            if(cle.equals(url)) {
                out.println("Url : "+url +"\n");
                out.println("Methode associe : "+mapp.get(cle).getMethodeName());
                out.println("avec la class : "+mapp.get(cle).getClassName());
                
            try{
                Class<?> clazz = Class.forName(mapp.get(cle).getClassName());
                Object instance = clazz.getDeclaredConstructor().newInstance();
                Method method = clazz.getMethod(mapp.get(cle).getMethodeName());

                Object result = method.invoke(instance);
                out.println("Resultat de l'execution: " + result.toString());
            } catch(Exception e){
                e.printStackTrace(out);
            }
            }
        }
    }

    
    public HashMap getListeClasses(String packageName) throws Exception {
        
        HashMap<String, Mapping> map = new HashMap<>();

        //répertoire racine du package
        String path = Thread.currentThread().getContextClassLoader().getResource(packageName.replace('.', '/')).getPath();

        String decodedPath = URLDecoder.decode(path, "UTF-8");
        File packageDir = new File(decodedPath);
 

        File[] files = packageDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".class")) {
                    String className = packageName + "." + file.getName().replace(".class", "");
                    Class<?> classe = Class.forName(className);
                     for (Method method : classe.getDeclaredMethods()) {

                        if (method.isAnnotationPresent(Get.class)) {
                            Get annotation = method.getAnnotation(Get.class);
                            String nameClass = classe.getName();
                            String annotationName = annotation.value();
                            String methodeName = method.getName();
]
                            map.put(annotationName, new Mapping(nameClass, methodeName));

                            System.out.println("Méthode annotée : " + method.getName());
                            System.out.println("Valeur de l'annotation : " + annotation.value());
                        }
                    }
                }
            }
        }
        return map;
    }
}
