import javax.servlet.*;
import javax.servlet.http.*;
import org.jcrontab.CrontabEntryBean;
import org.jcrontab.CrontabEntryDAO;



public class CrontabViewServlet extends HttpServlet {

		public void doPost(HttpServletRequest request,
			HttpServletResponse response) {
			doGet(request, response);
		}

		public void doGet (HttpServletRequest request,
    		       HttpServletResponse response) {
       		try {
		
			CrontabEntryBean[] listOfBeans= CrontabEntryDAO.getInstance().findAll();
			
    			request.setAttribute ("listOfBeans", listOfBeans);
    			getServletConfig().getServletContext().getRequestDispatcher("/CrontabView.jsp").forward(request, response);
  		} catch (Exception ex) {
	    		ex.printStackTrace ();
    		}
		}
}
