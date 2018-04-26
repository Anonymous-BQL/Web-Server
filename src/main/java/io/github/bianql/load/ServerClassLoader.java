package io.github.bianql.load;

import io.github.bianql.server.Constants;

import java.util.ArrayList;
import java.util.List;

public class ServerClassLoader extends ClassLoader {
    private static ClassLoader common;
    private static ClassLoader serverClassLoader;

    static {
        List<ClassLoaderFactory.Repository> repositories = new ArrayList<>();
        repositories.add(new ClassLoaderFactory.Repository(Constants.SERVER_CLASS_LIB, ClassLoaderFactory.RepositoryType.DIR));
        try {
            common = ClassLoaderFactory.createClassLoader(repositories, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        serverClassLoader = new ServerClassLoader();
    }

    public static ClassLoader getServerClassLoader() {
        return serverClassLoader;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        ClassLoader system = ClassLoader.getSystemClassLoader();
        Class<?> clazz = null;
        try {
            clazz = system.loadClass(name);
        }catch (ClassNotFoundException e){
        }
        if (clazz != null) {
            return clazz;
        }
        return common.loadClass(name);
    }
}
