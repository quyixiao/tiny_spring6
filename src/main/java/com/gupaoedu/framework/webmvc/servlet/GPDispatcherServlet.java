package com.gupaoedu.framework.webmvc.servlet;

import com.gupaoedu.framework.annotation.GPController;
import com.gupaoedu.framework.annotation.GPRequestMapping;
import com.gupaoedu.framework.context.support.GPApplicationContext;
import com.gupaoedu.framework.webmvc.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
public class GPDispatcherServlet extends HttpServlet {

    private final String LOCATION = "contextConfigLocation";

    // 读者可以思考一下这个设计的经典之处
    // GPHandlerMapping 最核心的设计，也是最经典的设计
    // 它直接干掉了Structs,Webwork 等MVC 框架
    private List<GPHandlerMapping> handlerMappings = new ArrayList<>();

    private Map<GPHandlerMapping, GPHandlerAdapter> handlerAdaptes = new HashMap<>();

    private List<GPViewResolver> viewResolvers = new ArrayList<>();

    private GPApplicationContext context;

    @Override
    public void init(ServletConfig config) throws ServletException {
        //相当于把Ioc容器初始化了
        context = new GPApplicationContext(config.getInitParameter(LOCATION));

        initStrategies(context);
    }

    protected void initStrategies(GPApplicationContext context) {
        // 有九种策略
        // 针对每个用户请求，都会经过一些处理策略处理，最终才能结果输出
        // 每种策略可以自定义干预，但是最终的结果都是一致的
        // =================这里就是传说中的九大组件===============================
        initMultipartResolver(context); // 文件上下文解析，如果请求的类型是multipart，将通过multipartResolver进行文件上下文传递
        //
        initLocaleResolver(context);// 本地化参数
        initThemeResolver(context);//主题解析
        /**我们自己会实现*/
        //GPHandlerMapping 用来保存Controller 中的配置的RequestMapping 和Method 的对应关系

        initHandlerMapping(context);
        // HandlerAdapter来实现匹配Method,参数，包括类转换，动态赋值
        initHandlerAdapters(context);//通过HandlerAdapter进行多类型的参数动态匹配
        initHandlerExceptionResolvers(context);// 通过HandlerAdapter进行多种类型的参数动态匹配
        initRequestToViewNameTranslator(context);//直接将请求解析到视图名
        /**我们自己会实现**/
        // 通过ViewResolvers 实现动态模板解析
        // 自己解析一套模板语言
        initViewResolvers(context);//通过viewResolver将逻辑视图解析到具体的视图实现
        iniFlashMapping(context);// Flash 映射管理器
    }

    private void iniFlashMapping(GPApplicationContext context) {
    }

    private void initViewResolvers(GPApplicationContext context) {
        // 在页面中输入http://localhost/first.html
        //解决页面名字和模板文件关联的问题
        String templateRoot = context.getConfig().getProperty("templateRoot");

        log.info("initViewResolvers = " + templateRoot);

        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();


        File templateRootDir = new File(templateRootPath);
        for (File template : templateRootDir.listFiles()) {
            log.info("html fileName :" + template.getName());
            this.viewResolvers.add(new GPViewResolver(templateRoot));
        }
    }

    private void initRequestToViewNameTranslator(GPApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(GPApplicationContext context) {
    }

    private void initHandlerAdapters(GPApplicationContext context) {
        // 在初始阶段，我们能做的就是，将这些参数的名字或者类型参数按一定的顺序保存下来
        // 因为在后面的反射调用的时候，传参数是一个数组
        // 可以通过记录这些参数的位置index,逐个从数组中取值，这样就和参数的顺序没有关系了
        for (GPHandlerMapping handlerMapping : this.handlerMappings) {
            // 每个方法都有一个参数列表，这里保存的形参列表
            this.handlerAdaptes.put(handlerMapping, new GPHandlerAdapter());
        }
    }

    private void initThemeResolver(GPApplicationContext context) {
    }

    private void initLocaleResolver(GPApplicationContext context) {
    }


    private void initMultipartResolver(GPApplicationContext context) {

    }


    // Controller 将配置中的RequestMapping 和Method进行一一对应
    private void initHandlerMapping(GPApplicationContext context) {
        // 按照我们的通常的理解就是一个Map
        // Map<String,Method> map;
        // map.put(url,method)
        // 首先从容器中获取所有的容器
        String[] beanNames = context.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            try {
                // 到Mvc层，对外提供了一个方法只有一个getBean()方法
                // 返回的对象不是BeanWrapper，怎么办
                Object controller = context.getBean(beanName);
                log.info(" ====url======beanName==="+beanName);
                // Object controller = GPAopUtils.getTargetObject(proxy);
                Class<?> clazz = controller.getClass();
                if (!clazz.isAnnotationPresent(GPController.class)) {
                    continue;
                }

                log.info(" ==-----------------------------==="+beanName);
                String baseUrl = "";
                if (clazz.isAnnotationPresent(GPRequestMapping.class)) {
                    GPRequestMapping requestMapping = clazz.getAnnotation(GPRequestMapping.class);
                    baseUrl = requestMapping.value();
                    //扫描所有的public方法
                }
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (!method.isAnnotationPresent(GPRequestMapping.class)) {
                        continue;
                    }
                    GPRequestMapping requestMapping = method.getAnnotation(GPRequestMapping.class);
                    String regex = ("/" + baseUrl + requestMapping.value().replaceAll("\\*", ".")).replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);
                    log.info("url pattern :" +regex );
                    this.handlerMappings.add(new GPHandlerMapping(controller, method, pattern));
                    log.info("Mapping :" + regex + "," + method);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        // 根据请求的url 来获取一个handler
        GPHandlerMapping handler = getHandler(req);
        if (handler == null) {
            processDispatchResult(req, resp, new GPModelAndView("404"));
            return;
        }
        GPHandlerAdapter ha = getHandlerAdapter(handler);
        // 这一步只是调用方法,得到返回值
        GPModelAndView mv = ha.handle(req, resp, handler);
        // 这一步才是真的输出
        processDispatchResult(req, resp, mv);
    }


    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, GPModelAndView mv) throws Exception {
        if (null == mv) {
            return;
        }
        if (this.viewResolvers != null) {
            for (GPViewResolver viewResolver : this.viewResolvers) {
                GPView view = viewResolver.resolveViewName(mv.getViewName(), null);
                if (view != null) {
                    view.render(mv.getModel(), req, resp);
                    return;
                }
            }
        }
    }

    private GPHandlerAdapter getHandlerAdapter(GPHandlerMapping handler) {
        if (this.handlerAdaptes.isEmpty()) {
            return null;
        }
        GPHandlerAdapter ha = this.handlerAdaptes.get(handler);
        if (ha.supports(handler)) {
            return ha;
        }
        return null;
    }

    private GPHandlerMapping getHandler(HttpServletRequest req) {
        if (this.handlerMappings.isEmpty()) {
            return null;
        }
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath, "").replaceAll("/+", "/");
        for (GPHandlerMapping handler : this.handlerMappings) {
            Matcher matcher = handler.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handler;
        }
        return null;
    }


}
