package org.jcrontab.net;

import java.io.Serializable;
import java.util.Date;

public class Cookie
    implements Cloneable, Serializable
{

    /**
	 * @author vipup
	 */
	private static final long serialVersionUID = -6828169426773305291L;
	public Cookie(String name, String value)
        throws IllegalArgumentException
    {
        if(!isToken(name) || name.equalsIgnoreCase("Comment") || name.equalsIgnoreCase("Discard") || name.equalsIgnoreCase("Domain") || name.equalsIgnoreCase("Expires") || name.equalsIgnoreCase("Max-Age") || name.equalsIgnoreCase("Path") || name.equalsIgnoreCase("Secure") || name.equalsIgnoreCase("Version"))
        {
            throw new IllegalArgumentException("invalid cookie name: " + name);
        } else
        {
            mName = name;
            mValue = value;
            mComment = null;
            mDomain = null;
            mExpiry = null;
            mPath = "/";
            mSecure = false;
            mVersion = 0;
            return;
        }
    }

    public void setComment(String purpose)
    {
        mComment = purpose;
    }

    public String getComment()
    {
        return mComment;
    }

    public void setDomain(String pattern)
    {
        mDomain = pattern.toLowerCase();
    }

    public String getDomain()
    {
        return mDomain;
    }

    public void setExpiryDate(Date expiry)
    {
        mExpiry = expiry;
    }

    public Date getExpiryDate()
    {
        return mExpiry;
    }

    public void setPath(String uri)
    {
        mPath = uri;
    }

    public String getPath()
    {
        return mPath;
    }

    public void setSecure(boolean flag)
    {
        mSecure = flag;
    }

    public boolean getSecure()
    {
        return mSecure;
    }

    public String getName()
    {
        return mName;
    }

    public void setValue(String newValue)
    {
        mValue = newValue;
    }

    public String getValue()
    {
        return mValue;
    }

    public int getVersion()
    {
        return mVersion;
    }

    public void setVersion(int version)
    {
        mVersion = version;
    }

    private boolean isToken(String value)
    {
        boolean ret = true;
        int length = value.length();
        for(int i = 0; i < length && ret; i++)
        {
            char c = value.charAt(i);
            if(c < ' ' || c > '~' || "()<>@,;:\\\"/[]?={} \t".indexOf(c) != -1)
                ret = false;
        }

        return ret;
    }

    public Object clone()
    {
    	try{
    		return super.clone();
    	}catch (CloneNotSupportedException e) {
    		 throw new RuntimeException(e.getMessage());
		}   
    }

    public String toString()
    {
        StringBuffer ret = new StringBuffer(50);
        if(getSecure())
            ret.append("secure ");
        if(0 != getVersion())
        {
            ret.append("version ");
            ret.append(getVersion());
            ret.append(" ");
        }
        ret.append("cookie");
        if(null != getDomain())
        {
            ret.append(" for ");
            ret.append(getDomain());
            if(null != getPath())
                ret.append(getPath());
        } else
        if(null != getPath())
        {
            ret.append(" (path ");
            ret.append(getPath());
            ret.append(")");
        }
        ret.append(": ");
        ret.append(getName());
        ret.append(getName().equals("") ? "" : "=");
        if(getValue().length() > 40)
        {
            ret.append(getValue().substring(1, 40));
            ret.append("...");
        } else
        {
            ret.append(getValue());
        }
        if(null != getComment())
        {
            ret.append(" // ");
            ret.append(getComment());
        }
        return ret.toString();
    }

    //private static final String SPECIALS = "()<>@,;:\\\"/[]?={} \t";
    protected String mName;
    protected String mValue;
    protected String mComment;
    protected String mDomain;
    protected Date mExpiry;
    protected String mPath;
    protected boolean mSecure;
    protected int mVersion;
}
