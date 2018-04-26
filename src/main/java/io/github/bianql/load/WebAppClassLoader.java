package io.github.bianql.load;

import java.util.ArrayList;
import java.util.List;

public class WebAppClassLoader extends ClassLoader {
    private String appDir;
    private ClassLoader webLoader;

    public WebAppClassLoader(String appDir) {
        this.appDir = appDir;
        List<ClassLoaderFactory.Repository> repositories = new ArrayList<>();
        repositories.add(new ClassLoaderFactory.Repository(appDir + "/WEB-INF/lib", ClassLoaderFactory.RepositoryType.GLOB));
        repositories.add(new ClassLoaderFactory.Repository(appDir + "/WEB-INF/classes", ClassLoaderFactory.RepositoryType.DIR));
        try {
            webLoader = ClassLoaderFactory.createClassLoader(repositories, ServerClassLoader.getServerClassLoader());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> clazz = null;
        try {
            clazz = ServerClassLoader.getServerClassLoader().loadClass(name);
        }catch (ClassNotFoundException e){

        }
        if (clazz != null) {
            return clazz;
        }
        return webLoader.loadClass(name);
    }
}
