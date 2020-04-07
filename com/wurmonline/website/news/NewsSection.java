// 
// Decompiled by Procyon v0.5.30
// 

package com.wurmonline.website.news;

import java.util.Collection;
import com.wurmonline.website.Block;
import com.wurmonline.website.LoginInfo;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import com.wurmonline.website.Section;

public class NewsSection extends Section
{
    private SubmitNewsBlock submitBlock;
    private List<NewsBlock> news;
    
    public NewsSection() {
        this.submitBlock = new SubmitNewsBlock();
        this.news = new ArrayList<NewsBlock>();
    }
    
    @Override
    public String getName() {
        return "News";
    }
    
    @Override
    public String getId() {
        return "news";
    }
    
    @Override
    public List<Block> getBlocks(final HttpServletRequest req, final LoginInfo loginInfo) {
        final List<Block> list = new ArrayList<Block>();
        if ("delete".equals(req.getParameter("action")) && loginInfo != null && loginInfo.isAdmin()) {
            this.delete(req.getParameter("id"));
        }
        list.addAll(this.news);
        if (loginInfo != null && loginInfo.isAdmin()) {
            list.add(this.submitBlock);
        }
        return list;
    }
    
    private void delete(final String id) {
    }
    
    @Override
    public void handlePost(final HttpServletRequest req, final LoginInfo loginInfo) {
        if (loginInfo != null && loginInfo.isAdmin()) {
            final String title = req.getParameter("title");
            String text = req.getParameter("text");
            text = text.replaceAll("\r\n", "<br>");
            text = text.replaceAll("\r", "<br>");
            text = text.replaceAll("\n", "<br>");
            this.news.add(new NewsBlock(new News(title, text, loginInfo.getName())));
        }
    }
}
