package com.eju.cy.drawlibrary.plug;

/**
* @ Name: Caochen
* @ Date: 2019-08-23
* @ Time: 13:45
* @ Description： 单例工具类
*/
public class EjuDrawEventCar {

    private static EjuDrawEventCar instance;
    private ConcreteObservableStatistics observableA;

    public static EjuDrawEventCar getDefault() {
        if (instance == null) {
            synchronized (EjuDrawEventCar.class) {
                if (instance == null) {
                    instance = new EjuDrawEventCar();
                }
            }
        }
        return instance;
    }

    private EjuDrawEventCar() {
        observableA = new ConcreteObservableStatistics();
    }


    public void register(EjuDrawObserver observer) {
        observableA.addObserver(observer);
    }


    public void unregister(EjuDrawObserver observer) {
        observableA.removeObserver(observer);
    }


    public void post(Object obj) {
        observableA.notifyObservers(obj);
    }
}
