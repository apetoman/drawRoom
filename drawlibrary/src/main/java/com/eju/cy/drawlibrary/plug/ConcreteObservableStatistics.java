package com.eju.cy.drawlibrary.plug;


import java.util.ArrayList;

/**
 * @ Name: Caochen
 * @ Date: 2019-08-23
 * @ Time: 11:32
 * @ Description： 被观察者的实现
 */
public class ConcreteObservableStatistics implements EjuDrawObservable {
    private ArrayList<EjuDrawObserver> observers;

    @Override
    public void addObserver(EjuDrawObserver observer) {
        if (observers == null) {
            observers = new ArrayList<>();
        }
        observers.add(observer);
    }

    @Override
    public void removeObserver(EjuDrawObserver observer) {
        if (observers == null || observers.size() <= 0) {
            return;
        }
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Object obj) {
        if (observers == null || observers.size() <= 0) {
            return;
        }
        for (EjuDrawObserver observer : observers) {
            observer.update(obj);
        }
    }
}
