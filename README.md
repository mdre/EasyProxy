# **EasyProxy** #
EasyProxy is a Java method proxy that mimic the behavior of CGLib. It creates a new instance of an Object that have all the methods redirected to the same interceptor.
This is achieved by creating a subclass of the base class where all the methods are overridden and redirected.
Final Methods and Syntetic Methods are ignored. 

To intecept the calls, you need to provide an object that implements the interface IEasyProxyInteceptor. This object will receive all the calls to the instance. 

The interface define the method `intecept` that receives all the calls to the instance and here you can decide what to do with it. As a parameter it receives the reference to the original method call in the `superMethod` reference. You can invoke it when you want. 

You can also define extra methods, add it to the subclass and process it in the interceptor. To do this you must define an interface and pass it as a parameter with the target class. All the methods in the interface will be implemented in the subclass and redirected to the interceptor. The `superMethod` will be null in this case.

Here is an example:

Suppose you hava a base class like this:

```Java
public class ASMFoo {

    String name;
    
    public ASMFoo() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void sayHello() {
        System.out.println("Hello "+this.name);
    }
}
```

To intercept all the calls, you must implement the IEasyProxy interface in a class and pass it as a parameter to the new instance.

```Java

public class ObjectProxyImpl implements  IEasyProxyInterceptor {

    @Override
    public Object intercept(Object target, Method method, Method superMethod, Object... args) throws Throwable {
        System.out.println("Intercepted: "+method.toString());
        System.out.println("superMethod: "+superMethod);
        System.out.println("anotations: "+Arrays.toString(method.getAnnotations()));
        
        Object result = null;
        switch(method.getName()) {
            case "setName":
                args[0] = "*" + args[0] + "*";
                result = superMethod.invoke(target, args);
                break;
            
            default:
                result = superMethod.invoke(target, args);
                
        }
        return result;
    }
}
```

To create a intercepted proxy class just do the following:

```java
EasyProxy ep = new EasyProxy();
ObjectProxyImpl op = new ObjectProxyImpl();
Class<ASMFoo> ft = (Class<ASMFoo>)ep.getProxyClass(ASMFoo.class, IObjectProxy.class);
System.out.println("class generated!!");
System.out.println("");
System.out.println("");
System.out.println("creating a new instance ...");
ASMFoo  fti = ft.getConstructor(IEasyProxyInterceptor.class).newInstance(op);
```

Now the instance has all the methods redirection, but you can use it as a normal object.

```java
fti.setName("Bart");
fti.sayHello("");
```

and the output is: 

```
Hello *Bart* 
```

It is also possible to add methods to the subclass and implement it in the inteceptor. Just define an interface and pass it to the `getProxyClass`.

```java
public interface IObjectProxy { 
    public String ___getLast();
}
```

```java
public class ObjectProxyImpl implements IObjetProxy,  IEasyProxyInterceptor {

    String lastMethod = "";

    @Override
    public Object intercept(Object target, Method method, Method superMethod, Object... args) throws Throwable {
        System.out.println("Intercepted: "+method.toString());
        System.out.println("superMethod: "+superMethod);
        System.out.println("anotations: "+Arrays.toString(method.getAnnotations()));
        
        lastMethod = method.getName();

        Object result = null;
        switch(method.getName()) {
            case "setName":
                args[0] = "*" + args[0] + "*";
                result = superMethod.invoke(target, args);
                break;
                
            case "___getLast":
                result = this.___getLast();
                break;

            default:
                result = superMethod.invoke(target, args);
                
        }
        return result;
    }

    @Override
    public String ___getLast() {
        return this.lastMethod;
    }
}
```
Now when you create an instance you can do this: 

```Java
Class<ASMFoo> ft = (Class<ASMFoo>)ep.getProxyClass(ASMFoo.class, IObjectProxy.class);
ASMFoo  foo = ft.getConstructor(IEasyProxyInterceptor.class).newInstance(op);

foo.setName("Bart");
System.out.println(((IObjectProxy)foo).___getLast());
```

and the output is:

```
setName
```

