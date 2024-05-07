package serv;

import java.io.*; 
import javax.servlet.*;
import javax.servlet.http.*;


public class FrontController extends HttpServlet {


    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

            PrintWriter out=response.getWriter();
            out.println("Welcome");
            out.println("URL : "+request.getRequestURL().toString());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

}
