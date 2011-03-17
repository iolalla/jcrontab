<%@page import="javax.xml.transform.stream.StreamResult"%>
<%@page import="javax.xml.transform.stream.StreamSource"%>
<%@page import="javax.xml.transform.Source"%>
<%@page import="javax.xml.transform.Transformer"%>
<%@page import="javax.xml.transform.TransformerFactory"%>
<%@ page contentType="xml" %>
<%@ page import="org.xml.sax.*" %> 
<%@ page import="java.io.*" %>
<%
try {
	// XML
	String xmlTmp =  request.getParameter("xml");
	xmlTmp = xmlTmp == null? "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?><?xml-stylesheet type=\"text/xsl\" href=\"view.xsl\" ?> <test><text>bla</text></test>":xmlTmp;
	Source xmlsource = new StreamSource(new StringReader(xmlTmp));
	//XSL
	TransformerFactory tFactory = TransformerFactory.newInstance();
	String xslTmp = request.getParameter("xsl");
	xslTmp = xslTmp == null? 	"<?xml version=\"1.0\"?><xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"	version=\"1.0\"><xsl:template match=\"test\"><html><body><h1>h1:<xsl:value-of select=\"text\" /></h1><form><input type=\"textarea\" name=\"xml\" ROWS=\"4\" COLS=\"40\" /><input type=\"textarea\" name=\"xsl\" ROWS=\"4\" COLS=\"40\" /><input type=\"submit\" label=\"transform\"/></form></body></html></xsl:template></xsl:stylesheet>":xslTmp;
	
	InputStream in = new ByteArrayInputStream(xslTmp.getBytes());
	InputStreamReader xslReader =  new InputStreamReader(in);

	Source xslSource = new StreamSource(xslReader);	
    Transformer transformer =  tFactory.newTransformer(xslSource); 
    // OUT
    //PrintStream outTmp = new PrintStream(response.getOutputStream());
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    System.out.println("XML-------"+xmlTmp);
    System.out.println("xsl-------"+xslTmp);
    transformer.transform(xmlsource, new StreamResult(bout));
    System.out.println("out-------"+bout .toString());
    bout .flush();
    bout .close();
    out.println(bout .toString());
    
} catch (Exception ex) {
    out.println(ex);
    //ex.printStackTrace(out);
}

%>
