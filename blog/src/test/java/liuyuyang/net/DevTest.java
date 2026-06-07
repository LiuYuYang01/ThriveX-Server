package liuyuyang.net;

import liuyuyang.net.web.mapper.ArticleMapper;
import liuyuyang.net.web.mapper.ArticleTagMapper;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DevTest {
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private ArticleTagMapper articleTagMapper;

    // @Test
    // public void test() {
    //     List<Article> list = articleMapper.selectList(null);
    //     list.forEach(k -> {
    //         System.out.println("____________________");
    //         System.out.println("文章 ID：" + k.getId());
    //
    //         int[] tag_ids = k.getTagIds().isEmpty() ? new int[0] : java.util.Arrays.stream(k.getTagIds().split(",")).mapToInt(Integer::parseInt).toArray();
    //
    //         if (tag_ids.length > 0) {
    //             for (Integer tag_id : tag_ids) {
    //                 System.out.println("标签 ID：" + tag_id);
    //                 ArticleTag articleTag =  new ArticleTag();
    //                 articleTag.setArticleId(k.getId());
    //                 articleTag.setTagId(tag_id);
    //                 articleTagMapper.insert(articleTag);
    //             }
    //         }
    //         System.out.println("____________________");
    //     });
    // }
}