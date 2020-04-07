// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.website.news;

import java.util.Date;
import com.wurmonline.website.LoginInfo;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import com.wurmonline.website.Block;

public class NewsBlock extends Block
{
    private News news;
    
    public NewsBlock(final News aNews) {
        this.news = aNews;
    }
    
    @Override
    public void write(final HttpServletRequest req, final PrintWriter out, final LoginInfo loginInfo) {
        out.print("<b>" + this.news.getTitle() + "</b> posted " + new Date(this.news.getTimestamp()) + " by " + this.news.getPostedBy() + "<br>");
        out.print("<br>");
        out.print(this.news.getText());
        if (loginInfo != null && loginInfo.isAdmin()) {
            out.print("<hr>ADMIN [ <a href=\"news.jsp?id=" + this.news.getId() + "&action=edit\">Edit</a> | <a href=\"news.jsp?id=" + this.news.getId() + "&action=delete\">Delete</a> ]");
        }
    }
    
    protected News getNews() {
        return this.news;
    }
}
