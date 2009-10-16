/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mycheapfriend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author michaelglass
 */
public class CreateServlet extends HttpServlet {
    InitialContext context;
    UserObjFacadeRemote facade;

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        try {
            context = new InitialContext();
            facade = (UserObjFacadeRemote) context.lookup("ejb.UserObjFacade");
        } catch (NamingException ex) {
            Logger.getLogger(ListServlet.class.getName()).log(Level.SEVERE, null, ex);
        }


        UserObj o = new UserObj();
        o.setId((long)(Math.random()*Math.pow(10,9)));
        int max_friends = 5;
        int num_friends = (int)(Math.random() * max_friends);

        facade.create(o);

        o= facade.find(o.getId());
//                List<Friend> old_friends = new ArrayList<Friend>();
        List<Friend> old_friends = o.getFriends();


        for(int i = 0; i < num_friends; i++)
        {
            UserObj another_friend = new UserObj();
            another_friend.setId((long)(Math.random()*Math.pow(10,9)));
            Friend f = new Friend();
            f.setFriend(another_friend);
            f.setNickname(Math.random() + "");
            f.setParent(o);
            old_friends.add(f);
        }
        o.setFriends(old_friends);
//
        facade.edit(o);


        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(response.SC_MOVED_TEMPORARILY);
        response.setHeader("Location", "/MyCheapFriend-war/ListServlet");
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
