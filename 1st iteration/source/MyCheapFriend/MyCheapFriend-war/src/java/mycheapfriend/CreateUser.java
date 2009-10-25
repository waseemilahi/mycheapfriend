/* To create a new user. */

package mycheapfriend;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Waseem Ilahi
 */
public class CreateUser extends HttpServlet {

    @Resource(mappedName="jms/MyCheapFriendFactory")
    private  ConnectionFactory connectionFactory;

    @Resource(mappedName="jms/MyCheapFriend")
    private  Queue queue;
/*
    UserObjFacadeRemote userFacade;

    InitialContext context;
*/
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String t_phone = request.getParameter("t_phone");
        long phone = (long)Integer.parseInt(t_phone);
        String password=request.getParameter("password");
        String email_domain=request.getParameter("email_domain");
/*
        try {
            context = new InitialContext();
            userFacade = (UserObjFacadeRemote) context.lookup("ejb.UserObjFacade");
        } catch (NamingException ex) {
            Logger.getLogger(CreateUser.class.getName()).log(Level.SEVERE, null, ex);
        }
*/

        if ((t_phone!=null) && (password!=null)&& (email_domain!=null)) {
            try {
                Connection connection = connectionFactory.createConnection();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageProducer messageProducer = session.createProducer( queue);

                ObjectMessage message = session.createObjectMessage();

                UserObj u = new UserObj();
                u.setPhone(phone);
                u.setPassword(password);
                u.setEmail_domain(email_domain);

                message.setObject(u);
                messageProducer.send(message);
                messageProducer.close();
                connection.close();
                response.sendRedirect("ListUsers");

            } catch (JMSException ex) {
                    ex.printStackTrace();
            }
        }




/*


        UserObj o = new UserObj();
        UserObj old = new UserObj();
        long number = (long)(Math.random());
        old.setPhone((long)number);
        old.setActive(Boolean.TRUE);
        old.setUnsubscribe(Boolean.FALSE);
        old.setSalt("Blah");
        old.setEmail_domain("blah@blah.com");
        old.setPassword("blahblah");
        old.setDebts(null);
        old.setLoans(null);
        old.setFriends(null);
        
        System.out.printf("\n The Generated Number is: %d\n", number);
        System.out.printf("\n The Phone Number is: %d\n", old.getPhone());

        userFacade.create(old);
        
        if(userFacade.contains(old)){
            System.out.println("\n At least found the Entity.\n");
        }

        List<UserObj> users = userFacade.findAll();
        System.out.printf("\n the phone for  the last user in the facade: %d\n" , (users.get(users.size()-1)).getPhone());

        o = userFacade.find(old);

    if(o == null){
        System.out.println("\n userFacade.find(obj) returned null. \n");
    }else if(o != null){

         int max_friends = 5;
        int num_friends = (int)(Math.random() * max_friends);

        List<Friend> old_friends = o.getFriends();
    
        for(int i = 0; i < num_friends; i++)
        {
            UserObj another_friend = new UserObj();
            another_friend.setPhone((long)(Math.random()*Math.pow(10,9)));
            Friend f = new Friend();
            f.setFriend(another_friend);
            f.setNickname(Math.random() + "");
            f.setParent(o);
            old_friends.add(f);
        }
        o.setFriends(old_friends);

        userFacade.edit(o);

        }
      */
        
        PrintWriter out = response.getWriter();
        try {

            out.println("<html>");
            out.println("<head>");
            out.println("<title>Create A User</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Enter phone and other info.</h1>");

            out.println("<form>");
            out.println("Phone: <input type='text' name='t_phone'><br/><br/>");
            out.println("Password: <input type='text' name='password'><br/><br/>");
            out.println("Email: <input type='text' name='email_domain'><br/><br/>");
            out.println("<input type='submit'><br/>");
            out.println("</form>");

            out.println("</body>");
            out.println("</html>");

        } finally {
            out.close();
        }
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
