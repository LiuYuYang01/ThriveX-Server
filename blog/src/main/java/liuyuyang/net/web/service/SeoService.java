package liuyuyang.net.web.service;

import liuyuyang.net.vo.seo.SeoArticleCheckVO;
import liuyuyang.net.vo.seo.SeoLinkCheckVO;
import liuyuyang.net.vo.seo.SeoSitemapCheckVO;

public interface SeoService {
    SeoArticleCheckVO checkArticleMeta();

    SeoSitemapCheckVO checkSitemap();

    SeoLinkCheckVO checkLinks();
}
