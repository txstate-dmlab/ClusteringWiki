package edu.txstate.dmlab.clusteringwiki.util;

/**
 *  ClusteringWiki - personalized and collaborative clustering of search results
 *  Copyright (C) 2010  Texas State University-San Marcos
 *  
 *  Contact: http://dmlab.cs.txstate.edu
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * Utility class to allow instantiation of static bean values in Spring
 * 
 * @author David C. Anastasiu
 *
 */
public class StaticInitializerBeanFactoryPostProcessor implements
		BeanFactoryPostProcessor {

	private Map<String, Map<String, Object>> classes;

	public StaticInitializerBeanFactoryPostProcessor() {
	}

    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        for (Iterator<String> classIterator = classes.keySet().iterator(); classIterator.hasNext(); ) {
            String className = (String)classIterator.next();
//System.out.println("Class " + className + ":");
            Map<String, Object> vars = classes.get(className);
            Class<?> c = null;
            try {
                c = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new StaticInitializerBeansException("Class not found for " + className, e);
            }
            Method[] methods = c.getMethods();
            for (Iterator<String> fieldIterator = vars.keySet().iterator(); fieldIterator.hasNext(); ) {
                String fieldName = (String)fieldIterator.next();
                Object value = vars.get(fieldName);
                Method method = findStaticSetter(methods, fieldName);
                if (method == null) {
                    throw new StaticInitializerBeansException("No static setter method found for class " +
                            className + ", field " + fieldName);
                }
//System.out.println("\tFound method " + method.getName() + " for field " + fieldName + ", value " + value);
                try {
                    method.invoke(null, new Object[] {value});
                } catch (Exception e) {
                    throw new StaticInitializerBeansException("Invocation of method " + method.getName() +
                            " on class " + className + " with value " + value + " failed.", e);
                }
            }
        }
    }


    /**
     * Look for a static setter method for field named fieldName in Method[].
     * Return null if none found.
     * @param methods
     * @param fieldName
     * @return
     */
    private Method findStaticSetter(Method[] methods, String fieldName) {
        String methodName = setterName(fieldName);
        for (int i=0; i<methods.length; i++) {
            if (methods[i].getName().equals(methodName) &&
                        Modifier.isStatic(methods[i].getModifiers())) {
                return methods[i];
            }
        }
        return null;
    }

    /**
     * return the standard setter name for field fieldName
     * @param fieldName
     * @return
     */
    private String setterName(String fieldName) {
        String nameToUse = null;
        if (fieldName.length() == 1) {
            if (Character.isLowerCase(fieldName.charAt(0))) {
                nameToUse = fieldName.toUpperCase();
            } else {
                nameToUse = fieldName;
            }
        } else {
            if (Character.isLowerCase(fieldName.charAt(0)) && Character.isLowerCase(fieldName.charAt(1))) {
                nameToUse = fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
            }  else {
                nameToUse = fieldName;
            }
        }
        return "set" + nameToUse;
    }

    public void setClasses(Map<String, Map<String, Object>> theClasses) {
        classes = theClasses;
    }
}

class StaticInitializerBeansException extends BeansException {
	private static final long serialVersionUID = -418368483204710236L;
	StaticInitializerBeansException(String msg) {
         super(msg);
     }
     StaticInitializerBeansException(String msg, Throwable e) {
         super(msg, e);
     }
}
