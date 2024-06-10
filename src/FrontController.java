package controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.rmi.CORBA.Util;
import javax.rmi.CORBA.Util;
import annotation.*;
import model.*;
import javax.servlet.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.*;
import Utils.*;
import annotation.*;

public class FrontController extends HttpServlet {

    HashMap<String, Mapping> mapp = new HashMap<>();

    public void init() throws ServletException{

        try{
            String packageToScan = getInitParameter("package_name");
            mapp = getListeClasses(packageToScan , ControllerAnnotation.class);
       
        } catch (PackageNotFound e) {
            e.printStackTrace();
            // throw new ServletException("Le répertoire du package spécifié n'existe pas.", e);
        }
        catch (Exception e) {
            e.printStackTrace(); 
            throw new ServletException(e.getMessage());
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
        res.setContentType("text/plain");
        String url = req.getRequestURI();
        PrintWriter out = res.getWriter();
        out.println("Youhouuu");

        boolean urlExist = false;
        for(String cle : mapp.keySet()) {
            if(cle.equals(url)) {
                out.println("Cle : "+cle+"\n");
                out.println("Url : "+url +"\n");
                out.println("Methode associe : "+ mapp.get(cle).getMethodeName());
                out.println("avec la class : "+ mapp.get(cle).getClassName());
                
                try{
                    
                    Class<?> clazz = Class.forName(mapp.get(cle).getClassName());
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    Method method = clazz.getMethod(mapp.get(cle).getMethodeName());

                    Object result = method.invoke(instance);

                    if(result instanceof ModelView){
                        ModelView mv = (ModelView) result;
                        String targetUrl ="pages/" + mv.getUrl();
                        ServletContext context = getServletContext();
                        String realPath = context.getRealPath(targetUrl);

                        if (realPath == null || !new File(realPath).exists()) {
                            throw new ServletException("La page JSP " + targetUrl + " n'existe pas.");
                        }
                     
                        HashMap<String, Object> data = mv.getData();
                        for (String keyData : data.keySet()) {
                            req.setAttribute(keyData, data.get(keyData));
                        }

                        RequestDispatcher dispatch = req.getRequestDispatcher(targetUrl);
                        dispatch.forward(req, res);
                    }
                    else if (result instanceof String) {
                        out.println(result.toString());
                    } else {
                        throw new ServletException("Type de retour inconnu : " + result.getClass().getName());
                    }
                    out.println("Resultat de l'execution: " + result.toString());

                } catch(Exception e){
                    e.printStackTrace(out);
                }
                urlExist = true;    
                break;
            }
            
        }
        if (!urlExist) {
            out.println("Aucune methode n\\'est associee a l\\'url : " + url);
        }
    }

    
    public HashMap getListeClasses(String packageName , Class<?> annotationClass) throws Exception {
        
        HashMap<String, Mapping> map = new HashMap<>();

    try{
        String path = Thread.currentThread().getContextClassLoader().getResource(packageName.replace('.', '/')).getPath();

        String decodedPath = URLDecoder.decode(path, "UTF-8");
        File packageDir = new File(decodedPath);
        
        if (!packageDir.exists()) {
            throw new PackageNotFound("Le repertoire du package " + packageName + " n'existe pas.");
        }

        File[] files = packageDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".class")) {
                    String className = packageName + "." + file.getName().replace(".class", "");
                    Class<?> classe = Class.forName(className);
                    if (classe.isAnnotationPresent(annotationClass.asSubclass(java.lang.annotation.Annotation.class))) {
                        for (Method method : classe.getDeclaredMethods()) {
                            if (method.isAnnotationPresent(Get.class)) {
                                Get annotation = method.getAnnotation(Get.class);

                                for(String key : map.keySet()){
                                    if(annotation.value().equals(key))
                                    throw new Exception("Duplicate url : " +annotation.value());
                                }

                                String nameClass = classe.getName();
                                String annotationName = annotation.value();
                                String methodeName = method.getName();
                                map.put(annotationName, new Mapping(nameClass, methodeName));

                                System.out.println("Méthode annotée : " + method.getName());
                                System.out.println("Valeur de l'annotation : " + annotation.value());
                            }
                        }
                    }
                }
            }
        }
    } catch (IOException e) {
        throw new Exception("Package introuvable");
    }
        return map;
    }
}
