package red.asuka.alice.datasource.config;

import org.apache.ibatis.io.VFS;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author: Tomonori
 * @Date: 2019/11/18 10:52
 * @Desc: VFS虚拟文件系统抽象类：提供用于访问应用程序服务器内资源的非常简单的API
 */
public class MybatisVFS extends VFS {

    /**
     * ResourcePatternResolver: 读取指定路径下的类信息
     * new PathMatchingResourcePatternResolver(): 获取Spring资源解析器
     * this.getClass().getClassLoader(): 获取runtime运行实例对象的类装载器
     * ClassLoader: 作用将.class装载到jvm内存中
     */
    private final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver(this.getClass().getClassLoader());

    /**
     * 对当前环境有效返回true
     * @return
     */
    @Override
    public boolean isValid() {
        return true;
    }

    /**
     * 递归列出所有资源的完整资源路径，这些资源是在指定路径下找到的所有资源的子级
     * @param url 路径
     * @param path 要列出的资源的路径
     * @return
     * @throws IOException
     */
    @Override
    protected List<String> list(URL url, String path) throws IOException {
        String urlString = url.toString();
        String baseUrlString = urlString.endsWith("/") ? urlString : urlString.concat("/");
        /**
         * 获取baseUrlString下的所有.class的资源信息
         */
        Resource[] resources = this.resourceResolver.getResources(baseUrlString + "**/*.class");

        return Stream.of(resources).map(resource -> preserveSubPackageName(baseUrlString, resource, path))
                .collect(Collectors.toList());
    }

    /**
     * 保留下级包名称
     * @param baseUrlString
     * @param resource
     * @param rootPath
     * @return
     */
    private static String preserveSubPackageName(String baseUrlString, Resource resource, String rootPath) {
        try {
            return rootPath + (rootPath.endsWith("/") ? "" : "/") + resource.getURL().toString().substring(baseUrlString.length());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
