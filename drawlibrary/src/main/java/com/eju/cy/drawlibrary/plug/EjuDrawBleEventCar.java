package com.eju.cy.drawlibrary.plug;

/**
* @ Name: Caochen
* @ Date: 2019-08-23
* @ Time: 13:45
* @ Description： 单例工具类
*/
public class EjuDrawBleEventCar {

    private static EjuDrawBleEventCar instance;
    private BleObservableStatistics observableA;

    public static EjuDrawBleEventCar getDefault() {
        if (instance == null) {
            synchronized (EjuDrawBleEventCar.class) {
                if (instance == null) {
                    instance = new EjuDrawBleEventCar();
                }
            }
        }
        return instance;
    }

    private EjuDrawBleEventCar() {
        observableA = new BleObservableStatistics();
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
