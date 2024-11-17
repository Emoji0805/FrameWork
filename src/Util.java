package Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import java.lang.reflect.*;
import java.util.Map;
import java.lang.Number;
import annotation.*;
import javax.servlet.http.Part;

public class Util {
    
    public static Object[] getParameterValues(HttpServletRequest request, Method method,
            Class<Param> paramAnnotationClass, Class<ParamObject> paramObjectAnnotationClass) throws Exception{
        Parameter[] parameters = method.getParameters();
        Object[] parameterValues = new Object[parameters.length];
        Map<String, String[]> params = request.getParameterMap();

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(paramAnnotationClass)) {
                if (request.getContentType() != null && request.getContentType().toLowerCase().startsWith("multipart/")) {
                    Part filePart = request.getPart("file");
                    if (filePart != null) {
                        Fichier fichier = new Fichier(filePart);
                        parameterValues[i] = fichier;
                    } else {
                        throw new ServletException("File part is missing.");
                    }
                }
                else{
                    Param param = parameters[i].getAnnotation(paramAnnotationClass);
                    String paramName = param.value();
                    String paramValue = request.getParameter(paramName);
                    parameterValues[i] = convertParameterValue(paramValue, parameters[i].getType());
                }
               
            } else if (parameters[i].isAnnotationPresent(paramObjectAnnotationClass)) {
                ParamObject paramObject = parameters[i].getAnnotation(paramObjectAnnotationClass);
                String objName = paramObject.objName();

                try {
                    Object paramObjectInstance = parameters[i].getType().getDeclaredConstructor().newInstance();
                    Field[] fields = parameters[i].getType().getDeclaredFields();
                    for (Field field : fields) {
                        String fieldName = field.getName();
                        String paramValue = request.getParameter(objName + "." + fieldName);
                        System.out.println(paramValue); 
                        System.out.println(objName + "." + fieldName);

                        String key = objName + "." + fieldName;
                        
                        validateField(params, field, key);
                        field.setAccessible(true);
                        field.set(paramObjectInstance, convertParameterValue(paramValue, field.getType()));
                    }
                    parameterValues[i] = paramObjectInstance;
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Failed to create and populate parameter object: " + e.getMessage());
                }
            } else {
                // throw new Exception("ETU 2743");
                String paramName = parameters[i].getName();
                String paramValue = request.getParameter(paramName);
                parameterValues[i] = convertParameterValue(paramValue,
                                                        parameters[i].getType());
            }
        }
        return parameterValues;
    }

     public static void validateField(Map<String, String[]> params, Field field, String key) throws Exception {

        if(field.isAnnotationPresent(Required.class)) {
            if (params.get(key) == null || params.get(key)[0].isEmpty()) {
                throw new Exception("The parameter " + key + " is required.");
            }
        }

        if (field.isAnnotationPresent(Length.class)) {
        if (params.get(key) != null) {
            String value = params.get(key)[0].replace(" ", "");
            Length length = field.getAnnotation(Length.class);

            if (value.length() > length.value()) {
                throw new Exception("Le parametre " + key + " ne doit pas depasser les " + length.value() + " caracteres.");
            }
        }
    }
        
        if (field.isAnnotationPresent(Numeric.class)) {
            if (params.get(key) != null) {
                try {
                    Double.parseDouble(params.get(key)[0]);
                } catch (Exception e) {
                    throw new Exception("The parameter " + key + " must be a number.");
                }
            }
        }
        if (field.isAnnotationPresent(Range.class)) {
            if (params.get(key) != null) {
                try {
                    double value = Double.parseDouble(params.get(key)[0]);
                    Range range = field.getAnnotation(Range.class);
                    if (value < range.min() || value > range.max()) {
                        System.out.println("Ambany na ambony");
                        throw new Exception("The parameter " + key + " must be within the range [" + range.min() + ", " + range.max() + "].");
                    }
                } catch (NumberFormatException e) {
                    throw new Exception("The parameter " + key + " must be a number.");
                }
        
            }
        }

    }
    public static Object convertParameterValue(String value, Class<?> type) {

        //  if (value == null || value.isEmpty()) { // Vérifiez aussi si la chaîne est vide
        //     if (type.isPrimitive()) {
        //         if (type == int.class) return 0; // Valeur par défaut pour les int manquants
        //         if (type == boolean.class) return false;
        //         if (type == long.class) return 0L;
        //         if (type == double.class) return 0.0;
        //         if (type == float.class) return 0.0f;
        //         if (type == short.class) return (short) 0;
        //         if (type == byte.class) return (byte) 0;
        //         if (type == char.class) return '\u0000';
        //     }
        //     return null;
        // }

        if (type == String.class) {
            return value;
        } else if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (type == long.class || type == Long.class) {
            return Long.parseLong(value);
        } else if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        } else if (type == float.class || type == Float.class) {
            return Float.parseFloat(value);
        } else if (type == short.class || type == Short.class) {
            return Short.parseShort(value);
        } else if (type == byte.class || type == Byte.class) {
            return Byte.parseByte(value);
        } else if (type == char.class || type == Character.class) {
            if (value.length() != 1) {
                throw new IllegalArgumentException("Invalid character value: " + value);
            }
            return value.charAt(0);
        }
        return null;
    }
}
