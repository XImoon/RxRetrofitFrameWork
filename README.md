# RxRetrofitFrameWork
FrameWork for internet-connection-code part
This Project provide a net framework depends on retrofit and rxjava. You can use it to save your code and manage the net model.

First, you should make sure that unity of server interfaces, like that:
  
  {
    code: 0,
    msg： “success”,
    data: {
            ……
          }
  }
  
Otherwise you should choose a other framework or modify the framework obey your rule.

This provide three way you can use:

  1. rxjava 1.1.10 + retrofit 2.1.0 + JSON      branch: dev-zora
  2. rxjava 2.1.3 + retrofit 2.3.0 + JSON       branch: dev-new
  3. rxjava 2.1.3 + retrofit 2.3.0 + xml        branch: dev-xml
  
  
Each way, you should know these Java file:

  NetProcessor.java:     This is really important that it deal too many action. Like request params, request way, 
                       response parser and so on. It use 'Chain structure' that seems clear.

  HttpManager.java:      This file is used as configuration of the retrofit. It's really sample to understand.
  
  NetServer.java:        Retrofit requires user to provide a interface file that act the internet connection.
                       Many framework will make you write many interface file on usually. By this way, you can
                       use this file combine with 'NetProcessor.java' instead of it. So, the only things that you
                       need to pay attention is writing a modle.
                       
  BeanModel.java:         This class is inherit parent class. The parent class only used as a Java bean. In this class,
                        it contains some function like some calculate, transform and obtain itself from server. And someone 
                        who call this function will give it a callback that could recall the caller.
                        
  NetServiceCallback.java:  Yes, that is the callback class. It's sample to understand.
  
  Finally, how to use the framework, see the java file 'MainActivity.java'.



  
  
