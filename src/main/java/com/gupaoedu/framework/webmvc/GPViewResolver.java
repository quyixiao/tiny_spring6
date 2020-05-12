package com.gupaoedu.framework.webmvc;

import java.io.File;
import java.util.Locale;

// 1.设计这个类的主要目的就是：
// 2.将一个静态的文件转换成一个动态的文件
// 3.根据不同的用户传递不同的参数
// 最终输出的字符串，交给Response输出
public class GPViewResolver {

    private File templateRootDir;

    private String viewName;


    private final static String DEFAULT_TEMPLATE_SUFFIX = ".html";

    public GPViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        this.templateRootDir = new File(templateRootPath);
    }


    public GPView resolveViewName(String viewName, Locale locale) throws Exception {
        this.viewName = viewName;
        if (null == viewName || "".equals(viewName.trim())) {
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+", "/"));
        return new GPView(templateFile);
    }


    public String getViewName() {
        return viewName;
    }
}
